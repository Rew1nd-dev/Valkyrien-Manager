package com.verr1.valkyrienmanager.network;

import com.verr1.valkyrienmanager.VManagerServer;
import com.verr1.valkyrienmanager.manager.db.general.item.NetworkKey;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ServerBoundRequestVDataPacket implements RootPacket{

    private final List<NetworkKey<?>> keys;
    private final long id;

    public ServerBoundRequestVDataPacket(long id, NetworkKey<?>... keys){
        this.keys = List.of(keys);
        this.id = id;
    }

    public ServerBoundRequestVDataPacket(FriendlyByteBuf buf){

        this.id = buf.readLong();
        int size = buf.readInt();
        this.keys = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            int finalI = i;
            Optional
                .ofNullable(buf.readNbt())
                .ifPresentOrElse(
                    nbt -> this.keys.add(NetworkKey.deserialize(nbt)),
                    () -> {
                        throw new IllegalStateException("Failed to read NBT of Index: " + finalI);
                    }
                );
        }
    }

    @Override
    public void rootEncode(FriendlyByteBuf buf) {
        buf.writeLong(id);
        buf.writeInt(keys.size());
        for (NetworkKey<?> key : keys) {
            buf.writeNbt(key.serialize());
        }
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        context.queue(() -> VManagerServer.DATA_BASE.response(id, context.getPlayer(), keys.toArray(new NetworkKey[0])));
    }
}
