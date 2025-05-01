package com.verr1.valkyrienmanager.network;

import com.verr1.valkyrienmanager.VManagerServer;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;

public class ServerBoundRequestSyncTagsPacket implements RootPacket{

    public ServerBoundRequestSyncTagsPacket(){

    }

    public ServerBoundRequestSyncTagsPacket(FriendlyByteBuf buf){

    }

    @Override
    public void rootEncode(FriendlyByteBuf buf) {

    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        context.queue(() -> {
            VManagerServer.DATA_BASE.responseSyncTags(context.getPlayer());
        });
    }
}
