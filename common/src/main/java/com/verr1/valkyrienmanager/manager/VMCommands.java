package com.verr1.valkyrienmanager.manager;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.verr1.valkyrienmanager.ValkyrienManagerServer;
import com.verr1.valkyrienmanager.foundation.data.ShipOwnerData;
import com.verr1.valkyrienmanager.manager.db.VDataExtension;
import com.verr1.valkyrienmanager.manager.db.VSMDataBase;
import com.verr1.valkyrienmanager.util.ComponentUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaterniond;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static com.verr1.valkyrienmanager.manager.VManager.INVALID_ID;
import static com.verr1.valkyrienmanager.manager.db.VDataExtension.Field.ID;
import static org.valkyrienskies.mod.common.util.VectorConversionsMCKt.toJOML;

public class VMCommands {

    private static final int MAX_QUERIES_PER_PAGE = 5;

    public static VManager manager(){
        return ValkyrienManagerServer.manager();
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
                        lt("ls").then(
                            arg("page", IntegerArgumentType.integer()).executes(
                                    VMCommands::QueryVSMPageCommand
                            )
                        )
                ).then(
                        lt("query")
                        .then(
                                arg("id", LongArgumentType.longArg()).executes(
                                        VMCommands::QueryIdCommand
                                ).then(
                                        lt("--cluster").executes(
                                                VMCommands::QueryClusterCommand
                                        )
                                )
                        ).then(
                                lt("--cluster").executes(
                                        VMCommands::QueryViewedClusterCommand
                                )
                        ).then(
                                lt("--owned").executes(
                                        VMCommands::QueryOwnedCommand
                                )
                        ).then(
                                lt("--around").executes(
                                        VMCommands::QueryAroundCommand
                                )
                        ).executes(
                                        VMCommands::QueryViewedCommand
                        )

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
                )
        );
    }


    private static int QueryOwnedCommand(CommandContext<CommandSourceStack> context){
        CommandSourceStack source = context.getSource();
        ServerPlayer sourcePlayer = source.getPlayer();
        VDataExtension.Field[] lod = new VDataExtension.Field[]{ID}; // parse(context);
        if(sourcePlayer == null){
            source.sendFailure(Component.literal("You must be a player to query ownership!"));
            return 0;
        }

        Set<Long> ids = VSMDataBase.query(vData -> vData.getOwner().isOwner(sourcePlayer));
        if(ids.isEmpty()){
            source.sendFailure(Component.literal("No ships found!"));
            return 0;
        }
        applyTitle("Query Data: ", componentOf(ids, lod)).forEach(
                l -> l.forEach(
                        c -> source.sendSuccess(() -> c, false)
                )
        );

        return 1;
    }

    private static int QueryAroundCommand(CommandContext<CommandSourceStack> context){
        CommandSourceStack source = context.getSource();
        ServerPlayer sourcePlayer = source.getPlayer();
        VDataExtension.Field[] lod = new VDataExtension.Field[]{ID}; //parse(context);
        if(sourcePlayer == null){
            source.sendFailure(Component.literal("You must be a player to query ownership!"));
            return 0;
        }

        Set<Long> ids = VSMDataBase.query(vData -> vData.getPlayersAroundOnCreation().contains(ShipOwnerData.of(sourcePlayer)));

        if(ids.isEmpty()){
            source.sendFailure(Component.literal("No ships found!"));
            return 0;
        }
        applyTitle("Query Data: ", componentOf(ids, lod)).forEach(
                l -> l.forEach(
                        c -> source.sendSuccess(() -> c, false)
                )
        );

        return 1;
    }

    private static int QueryViewedClusterCommand(CommandContext<CommandSourceStack> context){
        CommandSourceStack source = context.getSource();
        VDataExtension.Field[] lod = new VDataExtension.Field[]{ID}; // parse(context);
        if(source.getPlayer() == null){
            source.sendFailure(Component.literal("You must be a player to own a ship!"));
            return 0;
        }
        ServerPlayer sourcePlayer = source.getPlayer();
        Set<Long> ids = manager().clusterOf(manager().pick(sourcePlayer)).ids;
        if(ids.isEmpty()){
            source.sendFailure(Component.literal("No cluster found of this id!"));
            return 0;
        }
        applyTitle("Query Data: ", componentOf(ids, lod)).forEach(
                l -> l.forEach(
                        c -> source.sendSuccess(() -> c, false)
                )
        );

        return 1;
    }

    private static int QueryViewedCommand(CommandContext<CommandSourceStack> context){
        CommandSourceStack source = context.getSource();
        VDataExtension.Field[] lod = parse(context);
        if(source.getPlayer() == null){
            source.sendFailure(Component.literal("You must be a player query by view!"));
            return 0;
        }
        ServerPlayer sourcePlayer = source.getPlayer();
        long id = manager().pick(sourcePlayer);
        if(id == INVALID_ID){
            source.sendFailure(Component.literal("No ship found in your view!"));
            return 0;
        }

        if(!VSMDataBase.has(id)){
            source.sendFailure(Component.literal("No data found for ID: " + id + " Something Might Be Wrong Of VSM"));
        }
        VDataExtension data = VSMDataBase.get(id);
        ArrayList<Component> components = componentOf(data, lod);
        components.add(0, Component.literal("Query Result: ").withStyle(ComponentUtil.titleStyle()));
        components.forEach(
                c -> source.sendSuccess(() -> c, false)
        );

        return 1;
    }

    private static int QueryIdCommand(CommandContext<CommandSourceStack> context){
        Long id = context.getArgument("id", Long.class);
        VDataExtension.Field[] lod = parse(context);
        CommandSourceStack source = context.getSource();
        if(!VSMDataBase.has(id)){
            source.sendFailure(Component.literal("No data found for ID: " + id));
            return 0;
        }
        VDataExtension data = VSMDataBase.get(id);
        List<Component> components = componentOf(data, lod);
        components.forEach(
                c -> source.sendSuccess(() -> c, false)
        );
        return 1;
    }

    private static int QueryClusterCommand(CommandContext<CommandSourceStack> context){
        Long id = context.getArgument("id", Long.class);
        CommandSourceStack source = context.getSource();
        VDataExtension.Field[] lod = new VDataExtension.Field[]{ID}; //parse(context);
        Set<Long> ids = manager().clusterOf(id).ids;
        if(ids.isEmpty()){
            source.sendFailure(Component.literal("No cluster found of this id!"));
            return 0;
        }

        componentOf(ids, lod).forEach(
                l -> l.forEach(
                        c -> source.sendSuccess(() -> c, false)
                )
        );

        return 1;
    }

    private static int QueryVSMPageCommand(CommandContext<CommandSourceStack> context){
        int page = context.getArgument("page", Integer.class);
        CommandSourceStack source = context.getSource();
        List<List<Component>> info = getVSMPage(page);
        info.forEach(
                l -> l.forEach(
                        c -> source.sendSuccess(() -> c, false)
                )
        );
        return 1;
    }

    private static int OwnByIdCommand(CommandContext<CommandSourceStack> context){
        long id = context.getArgument("id", Long.class);
        CommandSourceStack source = context.getSource();
        if(!VSMDataBase.has(id)){
            source.sendFailure(Component.literal("No data found for ID: " + id));
            return 0;
        }

        VDataExtension data = VSMDataBase.get(id);
        if(source.getPlayer() == null){
            source.sendFailure(Component.literal("You must be a player to own a ship!"));
            return 0;
        }
        data.setOwnBy(source.getPlayer());

        source.sendSuccess(() -> Component.literal("You are now the owner of ID: " + id), false);
        return 1;
    }

    private static int OwnByViewCommand(CommandContext<CommandSourceStack> context){
        CommandSourceStack source = context.getSource();
        if(source.getPlayer() == null){
            source.sendFailure(Component.literal("You must be a player to own a ship!"));
            return 0;
        }
        ServerPlayer sourcePlayer = source.getPlayer();
        Set<Long> cluster = manager().clusterOf(manager().pick(sourcePlayer)).ids;
        if(cluster.isEmpty()){
            source.sendFailure(Component.literal("No ships found in your vicinity!"));
            return 0;
        }
        cluster.forEach(
                id -> {
                    VDataExtension data = VSMDataBase.get(id);
                    data.setOwnBy(sourcePlayer);
                }
        );
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



    private static ArrayList<List<Component>> getVSMPage(int page, VDataExtension.Field... lod){
        var it = VSMDataBase.V_DATA.entrySet().iterator();
        ArrayList<List<Component>> result = new ArrayList<>();

        int totalPages = (int) Math.ceil((double) VSMDataBase.V_DATA.size() / MAX_QUERIES_PER_PAGE);

        result.add(List.of(
                ComponentUtil.QUERY_TITLE.plainCopy().append(Component.literal(" Page: " + page + " / " + totalPages)),
                ComponentUtil.DASH
        ));

        for(int j = 0; j < MAX_QUERIES_PER_PAGE * page; j++){
            if(it.hasNext()){it.next();}
        }
        for (int j = 0; j < MAX_QUERIES_PER_PAGE; j++){
            if(it.hasNext()){
                var entry = it.next();

                result.add(componentOf(entry.getValue(), lod));

            } else {
                break;
            }
        }
        return result;
    }

    private static ArrayList<Component> componentOf(VDataExtension data, VDataExtension.Field... lod){
        var components =
                VDataLod(lod).apply(data);
        components.add(ComponentUtil.DASH);
        return components;
    }

    private static ArrayList<List<? extends Component>> applyTitle(String title, ArrayList<List<? extends Component>> components){
        var res = new ArrayList<>(components);
        res.add(0, List.of(
                ComponentUtil.titleWithContent(title, "", ComponentUtil.titleStyle(), ComponentUtil.contentStyle())
        ));
        return res;
    }

    private static ArrayList<List<? extends Component>> componentOf(Set<Long> ids, VDataExtension.Field... lod){

        return new ArrayList<>(ids.stream().map(
                i -> {
                    if (!VSMDataBase.has(i)) {
                        return List.of(Component.literal("No data found for ID: " + i));
                    }
                    VDataExtension data = VSMDataBase.get(i);
                    return componentOf(data, lod);
                }
        ).toList());

    }

    private static VDataExtension.Field[] parse(CommandContext<CommandSourceStack> context){
        /*
        CompoundTag tag = context.getArgument("nbt", CompoundTag.class);
        ArrayList<VDataExtension.Field> fields = new ArrayList<>();
        Arrays.stream(VDataExtension.Field.values()).map(Enum::name).filter(
                tag::contains
        ).forEach(
            s -> {
                try{
                    fields.add(VDataExtension.Field.valueOf(s));
                }catch (IllegalArgumentException ignored){

                }
            }
        );
        return fields.toArray(new VDataExtension.Field[0]);
        * */
        return VDataExtension.Field.values();
    }

    private static Function<VDataExtension, ArrayList<Component>> VDataLod(VDataExtension.Field... requests){
        return data -> new ArrayList<>(Arrays.stream(requests).map(f -> f.toComponent(data)).toList());
    }

}
