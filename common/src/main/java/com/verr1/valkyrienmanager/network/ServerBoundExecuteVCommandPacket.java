package com.verr1.valkyrienmanager.network;

import com.verr1.valkyrienmanager.manager.vcommand.Commands;
import com.verr1.valkyrienmanager.manager.vcommand.VCommand;
import dev.architectury.networking.NetworkManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class ServerBoundExecuteVCommandPacket implements RootPacket{

    public ServerBoundExecuteVCommandPacket(CompoundTag contextTag, Commands Type) {
        this.contextTag = contextTag;
        this.type = Type;
    }

    public ServerBoundExecuteVCommandPacket(FriendlyByteBuf buf) {
        this.contextTag = buf.readNbt();
        this.type = buf.readEnum(Commands.class);
    }

    private final CompoundTag contextTag;
    private final Commands type;

    @Override
    public void rootEncode(FriendlyByteBuf buf) {
        buf.writeNbt(contextTag);
        buf.writeEnum(type);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        context.queue(() -> type.executeServer(contextTag));
    }
}
