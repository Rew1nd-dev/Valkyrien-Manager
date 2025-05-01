package com.verr1.valkyrienmanager.network;

import com.verr1.valkyrienmanager.VManagerServer;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;

public class ServerBoundRequestSyncSnapConfigPacket implements RootPacket{

    public ServerBoundRequestSyncSnapConfigPacket(){

    }

    public ServerBoundRequestSyncSnapConfigPacket(FriendlyByteBuf buf){

    }

    @Override
    public void rootEncode(FriendlyByteBuf buf) {

    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        context.queue(() -> {
            VManagerServer.SNAP_DATA_BASE.responseSyncSnapConfig(context.getPlayer());
        });
    }
}
