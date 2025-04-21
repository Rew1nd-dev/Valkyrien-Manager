package com.verr1.valkyrienmanager.network;

import com.verr1.valkyrienmanager.VManagerClient;
import dev.architectury.networking.NetworkManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class ClientBoundVDataSyncPacket implements RootPacket{


    private final CompoundTag tag;

    public ClientBoundVDataSyncPacket(CompoundTag content) {
        this.tag = content;
    }

    @Override
    public void rootEncode(FriendlyByteBuf buf) {
        buf.writeNbt(tag);
    }

    public ClientBoundVDataSyncPacket(FriendlyByteBuf buf){
        this.tag = buf.readNbt();
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        context.queue(() -> VManagerClient.CLIENT_VIEW.receive(tag));
    }
}
