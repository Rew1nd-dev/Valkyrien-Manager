package com.verr1.valkyrienmanager.manager.db.general.entry;

import com.verr1.valkyrienmanager.util.SerializeUtils;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

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

    /**
     * Retrieves the value of the entry for client-side read operations.
     * This method should only be called on the client side.
     *
     * @return The value of type V.
     */
    @Override
    public Long view() {
        return stamp;
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
