package com.verr1.valkyrienmanager.manager.db.entry;

import com.verr1.valkyrienmanager.foundation.data.VOwnerData;
import com.verr1.valkyrienmanager.util.SerializeUtils;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;

public class BirthEntry implements IEntry<Long>{

    private long stamp = 0;



    @Override
    public Long get() {
        return stamp;
    }

    @Override
    public Class<Long> getType() {
        return Long.class;
    }

    @Override
    public void set(Long value) {
        stamp = value;
    }

    @Override
    public @NotNull CompoundTag serialize() {
        return SerializeUtils.LONG.serialize(stamp);
    }

    @Override
    public void deserialize(@NotNull CompoundTag tag) {
        stamp = SerializeUtils.LONG.deserialize(tag);
    }



}
