package com.verr1.valkyrienmanager.network;

import com.verr1.valkyrienmanager.VManagerClient;
import dev.architectury.networking.NetworkManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class ClientBoundSyncSnapConfigPacket implements RootPacket{

    private final CompoundTag serializedSnap;

    public ClientBoundSyncSnapConfigPacket(CompoundTag serializedSnap) {
        this.serializedSnap = serializedSnap;
    }

    public ClientBoundSyncSnapConfigPacket(FriendlyByteBuf buf) {
        serializedSnap = buf.readNbt();
    }

    @Override
    public void rootEncode(FriendlyByteBuf buf) {
        buf.writeNbt(serializedSnap);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        context.queue(() -> {
            VManagerClient.SNAP_CLIENT_VIEW.receiveSyncedConfig(serializedSnap);
        });
    }
}
