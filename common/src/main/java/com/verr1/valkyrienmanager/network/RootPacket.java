package com.verr1.valkyrienmanager.network;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;

public interface RootPacket {

    void rootEncode(FriendlyByteBuf buf);
    void handle(NetworkManager.PacketContext context);

}
