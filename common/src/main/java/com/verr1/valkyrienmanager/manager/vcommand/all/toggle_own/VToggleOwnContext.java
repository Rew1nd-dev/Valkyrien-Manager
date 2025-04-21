package com.verr1.valkyrienmanager.manager.vcommand.all.toggle_own;

import com.verr1.valkyrienmanager.util.CompoundTagBuilder;
import com.verr1.valkyrienmanager.util.SerializeUtils;
import net.minecraft.nbt.CompoundTag;

public record VToggleOwnContext(long id, String playerName) {

    public CompoundTag serialize() {
        return CompoundTagBuilder.create()
                .withLong("id", id)
                .withString("playerName", playerName)
                .build();
    }

    public static VToggleOwnContext deserialize(CompoundTag contextTag) {
        long id = contextTag.getLong("id");
        String playerName = contextTag.getString("playerName");
        return new VToggleOwnContext(id, playerName);
    }

}
