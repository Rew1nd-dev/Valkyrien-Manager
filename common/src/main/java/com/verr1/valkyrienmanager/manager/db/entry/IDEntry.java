package com.verr1.valkyrienmanager.manager.db.entry;

import com.verr1.valkyrienmanager.util.SerializeUtils;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

public class IDEntry implements IEntry<Long>{

    private long id;

    public IDEntry(long id) {
        this.id = id;
    }


    @Override
    public Long get() {
        return id;
    }

    @Override
    public Class<Long> getType() {
        return Long.class;
    }

    @Override
    public void set(Long value) {
        throw new UnsupportedOperationException("IDEntry is immutable");
    }

    @Override
    public @NotNull CompoundTag serialize() {
        return SerializeUtils.LONG.serialize(id);
    }

    @Override
    public void deserialize(@NotNull CompoundTag tag) {
        id = SerializeUtils.LONG.deserialize(tag);
    }

}
