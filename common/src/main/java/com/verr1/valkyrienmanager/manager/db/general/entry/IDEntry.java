package com.verr1.valkyrienmanager.manager.db.general.entry;

import com.verr1.valkyrienmanager.util.SerializeUtils;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

public class IDEntry extends IDOperatorEntry<Long>{

    private long clientView;

    public IDEntry(long id) {
        super(id);
    }

    @Override
    Long data(long id) {
        return id;
    }

    /**
     * Retrieves the type of the value stored in the entry.
     *
     * @return The class type of the value.
     */
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
        return clientView;
    }

    /**
     * Sets the value of the entry. This method should only be called on the server side.
     * Some entries are view-only and do not support this operation.
     * Examples of such entries include ClusterEntry and SlugEntry.
     *
     * @param value The value to set, of type V.
     */
    @Override
    public void set(Long value) {
        throw new UnsupportedOperationException("IDEntry is immutable");
    }

    @Override
    public @NotNull CompoundTag serializePacket() {
        return SerializeUtils.LONG.serialize(get());
    }

    @Override
    public void deserializePacket(@NotNull CompoundTag tag) {
        clientView = SerializeUtils.LONG.deserialize(tag);
    }


}
