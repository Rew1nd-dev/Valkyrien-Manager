package com.verr1.valkyrienmanager.manager.vcommand.all;

import com.verr1.valkyrienmanager.manager.vcommand.VCommand;
import com.verr1.valkyrienmanager.util.CompoundTagBuilder;
import com.verr1.valkyrienmanager.util.SerializeUtils;
import net.minecraft.nbt.CompoundTag;

public abstract class VUnaryIDOperator implements VCommand<Long> {

    @Override
    public Class<Long> type() {
        return Long.class;
    }

    @Override
    public Long deserialize(CompoundTag contextTag) {
        return SerializeUtils.LONG.deserialize(contextTag.getCompound("id"));
    }

    @Override
    public CompoundTag serialize(Long contextTag) {
        return CompoundTagBuilder.create().withCompound("id", SerializeUtils.LONG.serialize(contextTag)).build();
    }


    @Override
    public abstract void execute(Long context);
}
