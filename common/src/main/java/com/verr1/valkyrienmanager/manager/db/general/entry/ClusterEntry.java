package com.verr1.valkyrienmanager.manager.db.general.entry;

import com.verr1.valkyrienmanager.VManagerServer;
import com.verr1.valkyrienmanager.foundation.data.PhysicalCluster;
import com.verr1.valkyrienmanager.util.SerializeUtils;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class ClusterEntry extends IDOperatorEntry<PhysicalCluster> {



    private final PhysicalCluster clientView = new PhysicalCluster();

    private static final SerializeUtils.Serializer<Collection<Long>> serializer =
            SerializeUtils.ofCollection(SerializeUtils.LONG);

    public ClusterEntry(long id) {
        super(id);
    }




    @Override
    public PhysicalCluster data(long id) {
        return VManagerServer.manager().clusterOf(id);
    }

    @Override
    public Class<PhysicalCluster> getType() {
        return PhysicalCluster.class;
    }

    /**
     * Retrieves the value of the entry for client-side read operations.
     * This method should only be called on the client side.
     *
     * @return The value of type V.
     */
    @Override
    public PhysicalCluster view() {
        return clientView;
    }

    @Override
    public void set(PhysicalCluster value) {
        throw new UnsupportedOperationException("ClusterEntry is immutable");
    }


    @Override
    public @NotNull CompoundTag serializePacket() {
        return serializer.serialize(get().ids);
    }


    @Override
    public void deserializePacket(@NotNull CompoundTag tag) {
        clientView.ids.clear();
        clientView.ids.addAll(serializer.deserialize(tag));
    }

    @Override
    public boolean shouldSave() {
        return false;
    }
}
