package com.verr1.valkyrienmanager.manager.vcommand.all.teleport;

import com.verr1.valkyrienmanager.util.CompoundTagBuilder;
import com.verr1.valkyrienmanager.util.SerializeUtils;
import net.minecraft.nbt.CompoundTag;
import org.joml.Quaterniond;
import org.joml.Vector3d;

public record VTeleportContext(
        long id,
        Vector3d destination,
        Quaterniond rotation
) {

    public static VTeleportContext deserialize(CompoundTag tag){
        return new VTeleportContext(
                SerializeUtils.LONG.deserialize(tag.getCompound("id")),
                SerializeUtils.VECTOR3D.deserialize(tag.getCompound("destination")),
                SerializeUtils.QUATERNION4D.deserialize(tag.getCompound("rotation"))
        );
    }

    public CompoundTag serialize(){
        return CompoundTagBuilder.create()
                .withCompound("id", SerializeUtils.LONG.serialize(id))
                .withCompound("destination", SerializeUtils.VECTOR3D.serialize(destination))
                .withCompound("rotation", SerializeUtils.QUATERNION4D.serialize(rotation))
                .build();
    }

}
