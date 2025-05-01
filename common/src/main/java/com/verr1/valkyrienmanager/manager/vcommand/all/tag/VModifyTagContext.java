package com.verr1.valkyrienmanager.manager.vcommand.all.tag;

import com.verr1.valkyrienmanager.util.CompoundTagBuilder;
import com.verr1.valkyrienmanager.util.SerializeUtils;
import net.minecraft.nbt.CompoundTag;

public record VModifyTagContext(long id, String tagName, boolean add, boolean modifyCluster) {

    public CompoundTag serialize(){
        return CompoundTagBuilder.create()
                .withCompound("id", SerializeUtils.LONG.serialize(id))
                .withCompound("tagName", SerializeUtils.STRING.serialize(tagName))
                .withCompound("add", SerializeUtils.BOOLEAN.serialize(add))
                .withCompound("modifyCluster", SerializeUtils.BOOLEAN.serialize(modifyCluster))
                .build();
    }


    public static VModifyTagContext deserialize(CompoundTag tag){
        return new VModifyTagContext(
                SerializeUtils.LONG.deserialize(tag.getCompound("id")),
                SerializeUtils.STRING.deserialize(tag.getCompound("tagName")),
                SerializeUtils.BOOLEAN.deserialize(tag.getCompound("add")),
                SerializeUtils.BOOLEAN.deserialize(tag.getCompound("modifyCluster"))
        );
    }

}
