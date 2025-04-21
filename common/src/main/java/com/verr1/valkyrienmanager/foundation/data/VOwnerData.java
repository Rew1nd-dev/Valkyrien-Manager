package com.verr1.valkyrienmanager.foundation.data;

import com.verr1.valkyrienmanager.VManagerServer;
import com.verr1.valkyrienmanager.util.CompoundTagBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public record VOwnerData(String playerName, UUID playerUUID) {
    public static VOwnerData NOT_OWNED = new VOwnerData("Not Owned", new UUID(0, 0));


    public static VOwnerData of(Player p){
        return new VOwnerData(p.getName().getString(), p.getUUID());
    }

    public boolean matchName(String name){
        return playerName.equals(name);
    }

    public boolean isOwner(ServerPlayer player){
        return playerUUID.equals(player.getUUID());
    }


    public CompoundTag serialize(){
        return new CompoundTagBuilder().withString("name", playerName).withUUID("uuid", playerUUID).build();
    }


    public static VOwnerData deserialize(CompoundTag tag){
        return new VOwnerData(tag.getString("name"), tag.getUUID("uuid"));
    }


    @Override
    public boolean equals(Object o) {
        if (o instanceof VOwnerData vOwnerData) {
            return playerUUID.equals(vOwnerData.playerUUID);
        }
        return false;
    }

    @Override
    public String toString() {
        return playerName;
    }
}
