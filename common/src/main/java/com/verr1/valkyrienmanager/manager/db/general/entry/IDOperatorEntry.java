package com.verr1.valkyrienmanager.manager.db.general.entry;

import com.verr1.valkyrienmanager.util.SerializeUtils;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

public abstract class IDOperatorEntry<T> implements IEntry<T> {

    protected long id = 0;

    public IDOperatorEntry(long id) {
        this.id = id;
    }

    @Override
    public @NotNull CompoundTag serialize() {
        return SerializeUtils.LONG.serialize(id);
    }

    @Override
    public void deserialize(@NotNull CompoundTag tag) {
        id = SerializeUtils.LONG.deserialize(tag);
    }

    abstract T data(long id);

    @Override
    public final T get() {
        return data(id);
    }

    @Override
    public abstract @NotNull CompoundTag serializePacket();

    @Override
    public abstract void deserializePacket(@NotNull CompoundTag tag);
}
