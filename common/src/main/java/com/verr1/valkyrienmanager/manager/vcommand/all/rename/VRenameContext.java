package com.verr1.valkyrienmanager.manager.vcommand.all.rename;

import com.verr1.valkyrienmanager.util.CompoundTagBuilder;
import com.verr1.valkyrienmanager.util.SerializeUtils;
import net.minecraft.nbt.CompoundTag;

public record VRenameContext(long id, String newName, boolean modifyCluster) {

    public CompoundTag serialize() {
        return CompoundTagBuilder.create()
                .withCompound("id", SerializeUtils.LONG.serialize(id))
                .withCompound("newName", SerializeUtils.STRING.serialize(newName))
                .withCompound("modifyCluster", SerializeUtils.BOOLEAN.serialize(modifyCluster))
                .build();
    }

    public static VRenameContext deserialize(CompoundTag tag) {
        return new VRenameContext(
                SerializeUtils.LONG.deserialize(tag.getCompound("id")),
                SerializeUtils.STRING.deserialize(tag.getCompound("newName")),
                SerializeUtils.BOOLEAN.deserialize(tag.getCompound("modifyCluster"))
        );
    }

}
