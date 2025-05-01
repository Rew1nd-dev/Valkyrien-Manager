package com.verr1.valkyrienmanager.manager.db.general.entry;

import com.verr1.valkyrienmanager.VManagerServer;
import com.verr1.valkyrienmanager.util.SerializeUtils;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class CoordinateEntry extends IDOperatorEntry<Vector3dc>{




    private long id;
    private Vector3dc clientView = new Vector3d();

    public CoordinateEntry(long id) {
        super(id);
    }


    @Override
    Vector3dc data(long id) {
        return VManagerServer.manager().shipOf(id).map(s -> s.getTransform().getPositionInWorld()).orElse(new Vector3d());
    }

    /**
     * Retrieves the type of the value stored in the entry.
     *
     * @return The class type of the value.
     */
    @Override
    public Class<Vector3dc> getType() {
        return Vector3dc.class;
    }

    /**
     * Retrieves the value of the entry for client-side read operations.
     * This method should only be called on the client side.
     *
     * @return The value of type V.
     */
    @Override
    public Vector3dc view() {
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
    public void set(Vector3dc value) {
        throw new UnsupportedOperationException("CoordinateEntry is immutable");
    }

    @Override
    public @NotNull CompoundTag serializePacket() {
        return SerializeUtils.VECTOR3DC.serialize(get());
    }

    @Override
    public void deserializePacket(@NotNull CompoundTag tag) {
        clientView = SerializeUtils.VECTOR3DC.deserialize(tag);
    }

    @Override
    public boolean shouldSave() {
        return false;
    }
}
