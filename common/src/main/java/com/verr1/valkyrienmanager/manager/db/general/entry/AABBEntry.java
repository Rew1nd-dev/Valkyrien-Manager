package com.verr1.valkyrienmanager.manager.db.general.entry;

import com.verr1.valkyrienmanager.VManagerServer;
import com.verr1.valkyrienmanager.util.SerializeUtils;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import org.joml.primitives.AABBi;
import org.joml.primitives.AABBic;
import org.valkyrienskies.core.api.ships.Ship;

public class AABBEntry extends IDOperatorEntry<AABBic> {


    private AABBic clientView = new AABBi(0, 0, 0, 0, 0, 0);

    public AABBEntry(long id) {
        super(id);
    }


    /**
     * Retrieves the type of the value stored in the entry.
     *
     * @return The class type of the value.
     */
    @Override
    public Class<AABBic> getType() {
        return AABBic.class;
    }

    /**
     * Sets the value of the entry. This method should only be called on the server side.
     * Some entries are view-only and do not support this operation.
     * Examples of such entries include ClusterEntry and SlugEntry.
     *
     * @param value The value to set, of type V.
     */
    @Override
    public void set(AABBic value) {
        throw new UnsupportedOperationException("AABBEntry is immutable");
    }


    @Override
    public AABBic view() {
        return clientView;
    }


    @Override
    AABBic data(long id) {
        return VManagerServer.manager().shipOf(id).map(Ship::getShipAABB).orElse(new AABBi(0, 0, 0, 0, 0, 0));
    }

    @Override
    public @NotNull CompoundTag serializePacket() {
        return SerializeUtils.AABB_I.serialize(new AABBi(get()));
    }

    @Override
    public void deserializePacket(@NotNull CompoundTag tag) {
        clientView = SerializeUtils.AABB_I.deserialize(tag);
    }

    @Override
    public boolean shouldSave() {
        return false;
    }
}
