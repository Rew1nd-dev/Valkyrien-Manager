package com.verr1.valkyrienmanager.manager;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.verr1.valkyrienmanager.VManagerClient;
import com.verr1.valkyrienmanager.VManagerServer;
import com.verr1.valkyrienmanager.foundation.data.VOwnerData;
import com.verr1.valkyrienmanager.manager.db.VDataBase;
import com.verr1.valkyrienmanager.manager.db.item.VItem;
import com.verr1.valkyrienmanager.manager.db.v1.VDataExtension;
import com.verr1.valkyrienmanager.manager.db.v1.VSMDataBase;
import com.verr1.valkyrienmanager.util.ComponentUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaterniond;
import org.valkyrienskies.core.api.ships.Ship;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.verr1.valkyrienmanager.manager.VManager.INVALID_ID;
import static com.verr1.valkyrienmanager.manager.db.v1.VDataExtension.Field.ID;
import static org.valkyrienskies.mod.common.util.VectorConversionsMCKt.toJOML;

public class VMCommands {

    private static final int MAX_QUERIES_PER_PAGE = 5;

    public static VManager manager(){
        return VManagerServer.manager();
    }

    public static VDataBase db(){
        return VManagerServer.DATA_BASE;
    }

    private static LiteralArgumentBuilder<CommandSourceStack> lt(String name){
        return LiteralArgumentBuilder.literal(name);
    }

    private static<T> RequiredArgumentBuilder<CommandSourceStack, T> arg(String name, ArgumentType<T> type){
        return RequiredArgumentBuilder.argument(name, type);
    }

    public static void registerServerCommands(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(
                Commands.literal(
                    "vsm"
                ).then(
                        lt("own").then(
                                arg("id", LongArgumentType.longArg()).executes(
                                        VMCommands::OwnByIdCommand
                                )
                        ).executes(
                                VMCommands::OwnByViewCommand
                        )
                ).then(
                        lt("tp").then(
                                arg("id", LongArgumentType.longArg()).then(
                                        arg("coordinate", Vec3Argument.vec3()).executes(
                                                VMCommands::TeleportCommand
                                        )
                                )
                        )
                ).then(
                        lt("addMissing").executes(
                                VMCommands::AddMissingCommand
                        )
                ).then(
                        lt("id").executes(
                                VMCommands::QueryViewIDCommand
                        )
                ).then(
                        lt("removeInvalid").executes(
                            VMCommands::ValidateVDataCommand
                        )
                )
        );
    }

    public static void registerClientCommands(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(
                Commands.literal(
                        "vsm"
                ).then(
                        lt("ui").executes(
                                VMCommands::OpenGUICommand
                        )
                ).then(
                        lt("reload").executes(
                                VMCommands::ReloadViewCommand
                        )
                )
        );
    }


    public static int ValidateVDataCommand(CommandContext<CommandSourceStack> context){
        AtomicInteger count = new AtomicInteger(0);
        Set<Long> present = manager().allShips().stream().map(Ship::getId).collect(Collectors.toSet());
        db().data().keySet().stream().filter(present::contains).forEach(i -> {db().remove(i); count.getAndIncrement();});

        context.getSource().sendSuccess(() -> Component.literal("Remove: " + count.get() + " invalid keys"), false);
        return 1;
    }

    public static int QueryViewIDCommand(CommandContext<CommandSourceStack> context){
        CommandSourceStack source = context.getSource();
        if(source.getPlayer() == null){
            source.sendFailure(Component.literal("You must be a player to view a ship!"));
            return 0;
        }
        ServerPlayer sourcePlayer = source.getPlayer();
        long id = manager().pick(sourcePlayer);
        if(id == INVALID_ID){
            source.sendFailure(Component.literal("No ships found in your vicinity!"));
            return 0;
        }
        source.sendSuccess(() -> Component.literal("Ship ID: " + id), false);
        return 1;
    }

    private static int OpenGUICommand(CommandContext<CommandSourceStack> context){
        VManagerClient.manager().openGUI();
        return 1;
    }

    private static int ReloadViewCommand(CommandContext<CommandSourceStack> context){
        VManagerClient.manager().requestAll();
        return 1;
    }

    private static int AddMissingCommand(CommandContext<CommandSourceStack> context){
        AtomicInteger count = new AtomicInteger(0);
        manager()
                .allShips()
                .stream()
                .map(Ship::getId)
                .filter(id -> db().getOptional(id).isEmpty())
                .forEach(
                        id -> {db().put(id, VItem.createEmpty(id)); count.getAndIncrement();}
                );

        context.getSource().sendSuccess(() -> Component.literal("Added: " + count.get() + " missing keys"), false);
        return 1;
    }

    private static int OwnByIdCommand(CommandContext<CommandSourceStack> context){
        long id = context.getArgument("id", Long.class);
        Player sourcePlayer = context.getSource().getPlayer();
        manager().ownCluster(sourcePlayer, id);
        return 1;
    }

    private static int OwnByViewCommand(CommandContext<CommandSourceStack> context){
        CommandSourceStack source = context.getSource();
        if(source.getPlayer() == null){
            source.sendFailure(Component.literal("You must be a player to own a ship!"));
            return 0;
        }
        ServerPlayer sourcePlayer = source.getPlayer();
        long id = manager().pick(sourcePlayer);
        if(id == INVALID_ID){
            source.sendFailure(Component.literal("No ships found in your vicinity!"));
            return 0;
        }
        Set<Long> cluster = manager().clusterOf(id).ids;
        manager().ownCluster(sourcePlayer, id);
        source.sendSuccess(() -> Component.literal("You are now the owner of IDs: " + cluster), false);
        return 1;
    }

    private static int TeleportCommand(CommandContext<CommandSourceStack> context){
        Long id = context.getArgument("id", Long.class);
        Vec3 destination = Vec3Argument.getVec3(context, "coordinate");

        manager().shipOf(id).ifPresentOrElse(
                s -> manager().teleportWithCluster(s.getId(), toJOML(destination), new Quaterniond()),
                () -> {
                    CommandSourceStack source = context.getSource();
                    source.sendFailure(Component.literal("No ship found for ID: " + id));
                }
        );

        return 1;
    }




}
