package com.verr1.valkyrienmanager.manager.vcommand;


import com.verr1.valkyrienmanager.manager.vcommand.all.teleport.VTeleport;
import com.verr1.valkyrienmanager.manager.vcommand.all.toggle_own.VToggleOwn;
import com.verr1.valkyrienmanager.manager.vcommand.all.toggle_static.VToggleStatic;
import net.minecraft.nbt.CompoundTag;

public enum Commands {

    TP(new VTeleport()),
    TOGGLE_STATIC(new VToggleStatic()),
    TOGGLE_OWN(new VToggleOwn());


    <T> Commands(VCommand<T> command) {
        register(this, command);
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

    public void executeServer(CompoundTag context) {
        if (VCommand.COMMAND_EXECUTORS.containsKey(this)) {
            VCommand<?> command = VCommand.COMMAND_EXECUTORS.get(this);
            command.executeServer(context);
        } else {
            throw new IllegalArgumentException("Command " + this + " is not registered");
        }
    }

    public void executeUnsafe(Object context) {
        if (VCommand.COMMAND_EXECUTORS.containsKey(this)) {
            VCommand<?> command = VCommand.COMMAND_EXECUTORS.get(this);
            command.executeUnsafe(context);
        } else {
            throw new IllegalArgumentException("Command " + this + " is not registered");
        }
    }

    public static void register(){
    };

}
