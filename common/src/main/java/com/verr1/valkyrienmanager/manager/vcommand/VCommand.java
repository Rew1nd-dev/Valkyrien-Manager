package com.verr1.valkyrienmanager.manager.vcommand;

import com.verr1.valkyrienmanager.VManagerServer;
import com.verr1.valkyrienmanager.network.ServerBoundExecuteVCommandPacket;
import com.verr1.valkyrienmanager.registry.VMPackets;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;

public interface VCommand<T> {


    HashMap<Commands, VCommand<?>> COMMAND_EXECUTORS = new HashMap<>();

    Class<T> type();

    default QualifyResult qualify(Player executor, CompoundTag contextTag){
        return qualify(executor, deserialize(contextTag));
    }

    default QualifyResult qualify(Player executor, T context){
        return new QualifyResult(
                VManagerServer.INSTANCE.getPlayerList().isOp(executor.getGameProfile()),
                "You Need Operator Permission"
        );
    }

    T deserialize(CompoundTag contextTag);

    CompoundTag serialize(T contextTag);

    @SuppressWarnings("unchecked")
    default void executeClient(Object context, Commands identifier){
         if(context.getClass().isAssignableFrom(type())) {
            T tcontext = (T) context;

            CompoundTag contextTag = serialize(tcontext);

            VMPackets.CHANNEL.sendToServer(new ServerBoundExecuteVCommandPacket(contextTag, identifier));
        }
    }

    default void executeServer(CompoundTag contextTag){
        execute(deserialize(contextTag));
    }

    void execute(T context);

    @SuppressWarnings("unchecked")
    default void executeUnsafe(Object object) throws IllegalArgumentException {
        if(object.getClass().isAssignableFrom(type())) {
            T tcontext = (T) object;
            execute(tcontext);
        } else {
            throw new IllegalArgumentException("Command type mismatch: expected " + type().getName() + " but got " + object.getClass().getName());
        }
    }


    record QualifyResult(boolean permitted, String message){

    }

}
