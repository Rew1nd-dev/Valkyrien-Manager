package com.verr1.valkyrienmanager.manager.vcommand;


import com.verr1.valkyrienmanager.VManagerServer;
import com.verr1.valkyrienmanager.manager.db.snapshot.item.SnapShotConfig;
import com.verr1.valkyrienmanager.manager.vcommand.all.VEnumCommand;
import com.verr1.valkyrienmanager.manager.vcommand.all.VStringCommand;
import com.verr1.valkyrienmanager.manager.vcommand.all.rename.VRename;
import com.verr1.valkyrienmanager.manager.vcommand.all.tag.VModifyTag;
import com.verr1.valkyrienmanager.manager.vcommand.all.teleport.VTeleport;
import com.verr1.valkyrienmanager.manager.vcommand.all.toggle_own.VToggleOwn;
import com.verr1.valkyrienmanager.manager.vcommand.all.toggle_static.VToggleStatic;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;


public enum Commands {

    TP(new VTeleport()),
    TOGGLE_STATIC(new VToggleStatic()),
    TOGGLE_OWN(new VToggleOwn()),
    TAG(new VModifyTag()),
    RENAME(new VRename()),


    ADD_TAG(ctx -> VManagerServer.DATA_BASE.register(ctx)),
    REMOVE_TAG(ctx -> VManagerServer.DATA_BASE.unregister(ctx)),
    ADD_REPLACE(ctx -> VManagerServer.SNAP_DATA_BASE.snapConfig().neglect(ctx)),
    REMOVE_REPLACE(ctx -> VManagerServer.SNAP_DATA_BASE.snapConfig().forget(ctx)),
    ADD_PLACE(ctx -> VManagerServer.SNAP_DATA_BASE.snapConfig().forbid(ctx)),
    REMOVE_PLACE(ctx -> VManagerServer.SNAP_DATA_BASE.snapConfig().allow(ctx)),

    REPLACE_MODE(VEnumCommand.of(
            m -> VManagerServer.SNAP_DATA_BASE.snapConfig().replaceMode = m,
            SnapShotConfig.ReplaceMode.class
    )),
    PLACE_MODE(VEnumCommand.of(
            m -> VManagerServer.SNAP_DATA_BASE.snapConfig().placeMode = m,
            SnapShotConfig.PlaceMode.class
    )),
    ;


    <T> Commands(VCommand<T> command) {
        register(this, command);
    }

    Commands(VStringCommand stringCommand){
        register(this, stringCommand);
    }

    <E extends Enum<?>> Commands(VEnumCommand<E> enumCommand){
        register(this, enumCommand);
    }


    public static void register(Commands id, VCommand<?> executor) {
        if (VCommand.COMMAND_EXECUTORS.containsKey(id)) {
            throw new IllegalArgumentException("Command " + id + " is already registered");
        }

        VCommand.COMMAND_EXECUTORS.put(id, executor);
    }


    public void execute(Object context) {
        if (VCommand.COMMAND_EXECUTORS.containsKey(this)) {
            VCommand<?> command = VCommand.COMMAND_EXECUTORS.get(this);
            command.executeClient(context, this);
        } else {
            throw new IllegalArgumentException("Command " + this + " is not registered");
        }
    }

    public void executeServer(CompoundTag context, Player executor) {

        if (VCommand.COMMAND_EXECUTORS.containsKey(this)) {
            VCommand<?> command = VCommand.COMMAND_EXECUTORS.get(this);
            VCommand.QualifyResult result = command.qualify(executor, context);
            if(result.permitted()) command.executeServer(context);
            else executor.sendSystemMessage(Component.literal("Fail To Execute: " + this + " Reason: " + result.message()).withStyle(s -> s.withBold(true).withColor(ChatFormatting.RED)));
        } else {
            throw new IllegalArgumentException("Command " + this + " is not registered");
        }
    }

    public void executeUnsafe(Object context) throws IllegalArgumentException {
        if (VCommand.COMMAND_EXECUTORS.containsKey(this)) {
            VCommand<?> command = VCommand.COMMAND_EXECUTORS.get(this);
            command.executeUnsafe(context);
        } else {
            throw new IllegalArgumentException("Command " + this + " is not registered");
        }
    }

    ;

}
