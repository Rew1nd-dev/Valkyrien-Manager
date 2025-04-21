package com.verr1.valkyrienmanager.network;

import com.verr1.valkyrienmanager.VManagerServer;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;

public class ServerBoundRequestSyncKeysPacket implements RootPacket{

    public ServerBoundRequestSyncKeysPacket(){

    }

    public ServerBoundRequestSyncKeysPacket(FriendlyByteBuf buf){

    }

    @Override
    public void rootEncode(FriendlyByteBuf buf) {

    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        context.queue(() -> {
            VManagerServer.DATA_BASE.responseSyncKeys(context.getPlayer());
        });
    }
}
