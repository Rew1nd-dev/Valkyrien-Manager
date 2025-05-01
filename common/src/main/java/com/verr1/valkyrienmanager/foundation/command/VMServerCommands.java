package com.verr1.valkyrienmanager.foundation.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.verr1.valkyrienmanager.VManagerServer;
import com.verr1.valkyrienmanager.manager.VManager;
import com.verr1.valkyrienmanager.manager.db.general.VDataBase;
import com.verr1.valkyrienmanager.manager.db.general.item.VItem;
import com.verr1.valkyrienmanager.manager.db.snapshot.SnapshotDataBase;
import com.verr1.valkyrienmanager.manager.db.snapshot.item.SnapShotConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaterniond;
import org.valkyrienskies.core.api.ships.Ship;


import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.verr1.valkyrienmanager.manager.VManager.INVALID_ID;
import static org.valkyrienskies.mod.common.util.VectorConversionsMCKt.toJOML;

public class VMServerCommands {

    private static final int MAX_QUERIES_PER_PAGE = 5;

    public static VManager manager(){
        return VManagerServer.manager();
    }

    public static VDataBase db(){
        return VManagerServer.DATA_BASE;
    }

    public static SnapshotDataBase sdb(){
        return VManagerServer.SNAP_DATA_BASE;
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
                                        VMServerCommands::OwnByIdCommand
                                )
                        ).executes(
                                VMServerCommands::OwnByViewCommand
                        )
                ).then(
                        lt("tp").then(
                                arg("id", LongArgumentType.longArg()).then(
                                        arg("coordinate", Vec3Argument.vec3()).executes(
                                                VMServerCommands::TeleportCommand
                                        )
                                )
                        )
                ).then(
                        lt("addMissing").executes(
                                VMServerCommands::AddMissingCommand
                        )
                ).then(
                        lt("id").executes(
                                VMServerCommands::QueryViewIDCommand
                        )
                ).then(
                        lt("removeInvalid").executes(
                            VMServerCommands::ValidateVDataCommand
                        )
                ).then(
                        lt("tag-register").then(
                                arg("name", StringArgumentType.string()).executes(
                                        VMServerCommands::RegisterVTagCommand
                                )

                        )
                ).then(
                        lt("tag-unregister").then(
                                arg("name", StringArgumentType.string()).executes(
                                        VMServerCommands::UnregisterVTagCommand
                                )
                        )
                ).then(
                        lt("snap").executes(
                                VMServerCommands::SnapViewCommand
                        )
                ).then(
                        lt("rewind").executes(
                                VMServerCommands::RepairViewCommand
                        )
                ).then(
                        lt("config").then(
                                lt("rewind-neglect").executes(
                                        VMServerCommands::NeglectViewBlockCommand
                                )
                        ).then(
                                lt("rewind-allow").executes(
                                        VMServerCommands::AllowViewBlockCommand
                                )
                        ).then(
                                lt("rewind-mode").then(
                                        arg("mode", StringArgumentType.string()).executes(
                                                VMServerCommands::RewindSetModeCommand
                                        )
                                )
                        )
                ).then(
                        lt("query").then(
                                lt("all-rewind-neglect").executes(
                                    VMServerCommands::QueryAllNeglectCommand
                                )
                        )
                )

        );
    }

    /*
    * .suggests((context, builder) -> {
                                            for (var mode : SnapShotConfig.ReplaceMode.values()) {
                                                builder.suggest(mode.name().toLowerCase());
                                            }
                                            return builder.buildFuture();
                                        })
    * */

    public static int QueryAllNeglectCommand(CommandContext<CommandSourceStack> context){
        CommandSourceStack source = context.getSource();
        source.sendSuccess(() -> Component.literal("Neglected blocks:"), false);
        sdb().snapConfig().replaceBlackList.stream().sorted().forEach(
                block -> source.sendSuccess(() -> Component.literal(block), false)
        );
        return 1;
    }

    public static int RewindSetModeCommand(CommandContext<CommandSourceStack> context){
        String mode = context.getArgument("mode", String.class);
        try{
            SnapShotConfig.ReplaceMode r_mode = SnapShotConfig.ReplaceMode.valueOf(mode.toUpperCase());
            sdb().snapConfig().replaceMode = r_mode;
        }catch (IllegalArgumentException e){
            context.getSource().sendFailure(Component.literal("Invalid mode: " + mode).append(
                    Component.literal(" Valid modes: " + Arrays.stream(SnapShotConfig.ReplaceMode.values()).collect(Collectors.toSet()))
            ));
            return 0;
        }
        return 1;
    }

    public static int AllowViewBlockCommand(CommandContext<CommandSourceStack> context){
        CommandSourceStack source = context.getSource();
        if(source.getPlayer() == null){
            source.sendFailure(Component.literal("You must be a player to set neglect a block!"));
            return 0;
        }
        ServerPlayer sourcePlayer = source.getPlayer();
        BlockState state = manager().pickBlock(sourcePlayer);
        sdb().forget(state.getBlock());
        source.sendSuccess(() -> Component.literal("Allowed block: " + state.getBlock().getDescriptionId()), false);
        return 1;
    }

    public static int NeglectViewBlockCommand(CommandContext<CommandSourceStack> context){
        CommandSourceStack source = context.getSource();
        if(source.getPlayer() == null){
            source.sendFailure(Component.literal("You must be a player to set neglect a block!"));
            return 0;
        }
        ServerPlayer sourcePlayer = source.getPlayer();
        BlockState state = manager().pickBlock(sourcePlayer);
        sdb().neglect(state.getBlock());
        source.sendSuccess(() -> Component.literal("Neglected block: " + state.getBlock().getDescriptionId()), false);
        return 1;
    }


    public static int SnapViewCommand(CommandContext<CommandSourceStack> context){
        CommandSourceStack source = context.getSource();
        if(source.getPlayer() == null){
            source.sendFailure(Component.literal("You must be a player to snap a ship!"));
            return 0;
        }
        ServerPlayer sourcePlayer = source.getPlayer();
        long id = manager().pick(sourcePlayer);
        if(id == INVALID_ID){
            source.sendFailure(Component.literal("No ships found in your vicinity!"));
            return 0;
        }
        manager().clusterOf(id).ids.forEach(
                id_ -> manager().snap(id_)
        );
        return 1;
    }

    public static int RepairViewCommand(CommandContext<CommandSourceStack> context){
        long id = idOrInform(context);
        if(id == INVALID_ID)return 0;
        manager().clusterOf(id).ids.forEach(
                id_ -> manager().repair(id_)
        );
        return 1;
    }

    public static long idOrInform(CommandContext<CommandSourceStack> context){
        CommandSourceStack source = context.getSource();
        if(source.getPlayer() == null){
            source.sendFailure(Component.literal("You must be a player to snap a ship!"));
            return INVALID_ID;
        }
        ServerPlayer sourcePlayer = source.getPlayer();
        long id = manager().pick(sourcePlayer);
        if(id == INVALID_ID){
            source.sendFailure(Component.literal("No ships found in your vicinity!"));
            return INVALID_ID;
        }
        return id;
    }

    public static int RegisterVTagCommand(CommandContext<CommandSourceStack> context){
        String name = context.getArgument("name", String.class);
        db().register(name);
        context.getSource().sendSuccess(() -> Component.literal("Registered tag: " + name), false);
        return 1;
    }

    public static int UnregisterVTagCommand(CommandContext<CommandSourceStack> context){
        String name = context.getArgument("name", String.class);
        db().unregister(name);
        context.getSource().sendSuccess(() -> Component.literal("unregistered tag: " + name), false);
        return 1;
    }

    public static int ValidateVDataCommand(CommandContext<CommandSourceStack> context){
        AtomicInteger count = new AtomicInteger(0);
        Set<Long> present = manager().allShips().stream().map(Ship::getId).collect(Collectors.toSet());
        db().data().keySet().stream().filter(i -> !present.contains(i)).forEach(i -> {db().remove(i); count.getAndIncrement();});

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
