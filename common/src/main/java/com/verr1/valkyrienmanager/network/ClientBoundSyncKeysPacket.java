package com.verr1.valkyrienmanager.network;

import com.verr1.valkyrienmanager.VManagerClient;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ClientBoundSyncKeysPacket implements RootPacket{

    private final List<Long> keys;

    public ClientBoundSyncKeysPacket(List<Long> keys) {
        this.keys = keys;
    }

    public ClientBoundSyncKeysPacket(FriendlyByteBuf buf) {
        int size = buf.readInt();
        this.keys = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            this.keys.add(buf.readLong());
        }
    }

    @Override
    public void rootEncode(FriendlyByteBuf buf) {

        buf.writeInt(keys.size());
        for (Long key : keys) {
            buf.writeLong(key);
        }

    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        context.queue(() -> {
            VManagerClient.CLIENT_VIEW.receiveSyncedKey(new HashSet<>(keys));
        });
    }
}
