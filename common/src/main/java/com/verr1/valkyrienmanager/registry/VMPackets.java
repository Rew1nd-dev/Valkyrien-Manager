package com.verr1.valkyrienmanager.registry;

import com.verr1.valkyrienmanager.network.*;
import dev.architectury.networking.NetworkChannel;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.verr1.valkyrienmanager.VManagerMod.MOD_ID;

public enum VMPackets {

    CLIENT_V_DATA_SYNC(ClientBoundVDataSyncPacket.class, ClientBoundVDataSyncPacket::new),
    CLIENT_KEY_SYNC(ClientBoundSyncKeysPacket.class, ClientBoundSyncKeysPacket::new),

    SERVER_V_DATA_REQUEST(ServerBoundRequestVDataPacket.class, ServerBoundRequestVDataPacket::new),
    SERVER_KEY_REQUEST(ServerBoundRequestSyncKeysPacket.class, ServerBoundRequestSyncKeysPacket::new),
    SERVER_EXECUTE_COMMAND(ServerBoundExecuteVCommandPacket.class, ServerBoundExecuteVCommandPacket::new),
    ;


    public static final NetworkChannel CHANNEL =
            NetworkChannel.create(new ResourceLocation(MOD_ID, "main"));

    final PacketType<?> type;

    <T extends RootPacket> VMPackets(
            Class<T> type,
            Function<FriendlyByteBuf, T> factory
    ){
        this.type = new PacketType<>(type, factory);
    }


    private static class PacketType<T extends RootPacket> {
        private final Class<T> type;
        private final BiConsumer<T, FriendlyByteBuf> encoder;
        private final Function<FriendlyByteBuf, T> decoder;
        private final BiConsumer<T, Supplier<NetworkManager.PacketContext>> handler;


        private PacketType(
                Class<T> type,
                Function<FriendlyByteBuf, T> factory
        ) {
            this.type = type;
            this.encoder = T::rootEncode;
            this.decoder = factory;
            this.handler = (pkt, ctx) -> pkt.handle(ctx.get());
        }
    }

    public static void register(){
        Arrays
            .stream(values())
            .map(pkt -> pkt.type)
            .forEach(
                pkt -> CHANNEL.register(
                        pkt.type,
                        pkt.encoder,
                        pkt.decoder,
                        pkt.handler
                )
        );
    }

}
