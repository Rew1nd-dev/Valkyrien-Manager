package com.verr1.valkyrienmanager.foundation.data;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public record ShipOwnerData(String playerName, UUID playerUUID) {
    public static ShipOwnerData NOT_OWNED = new ShipOwnerData("Not Owned", new UUID(0, 0));


    public static ShipOwnerData of(Player p){
        return new ShipOwnerData(p.getName().getString(), p.getUUID());
    }

    public boolean isOwner(ServerPlayer player){
        return playerUUID.equals(player.getUUID());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ShipOwnerData shipOwnerData) {
            return playerUUID.equals(shipOwnerData.playerUUID);
        }
        return false;
    }

    @Override
    public String toString() {
        return playerName;
    }
}
