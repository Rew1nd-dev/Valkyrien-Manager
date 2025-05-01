package com.verr1.valkyrienmanager.network;

import com.verr1.valkyrienmanager.VManagerClient;
import com.verr1.valkyrienmanager.foundation.data.VTag;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class ClientBoundSyncTagsPacket implements RootPacket{

    private final List<VTag> vtags;

    public ClientBoundSyncTagsPacket(List<VTag> tags) {
        this.vtags = tags;
    }

    public ClientBoundSyncTagsPacket(FriendlyByteBuf buf) {
        int size = buf.readInt();
        this.vtags = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            Optional.ofNullable(buf.readNbt())
                    .map(VTag::deserialize)
                    .ifPresent(
                        vtags::add
                );
        }
    }

    @Override
    public void rootEncode(FriendlyByteBuf buf) {

        buf.writeInt(vtags.size());
        for (VTag key : vtags) {
            buf.writeNbt(key.serialize());
        }

    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        context.queue(() -> {
            VManagerClient.CLIENT_VIEW.receiveSyncedTag(vtags);
        });
    }
}
