package com.verr1.valkyrienmanager.manager.db.entry;

import com.verr1.valkyrienmanager.VManagerServer;
import com.verr1.valkyrienmanager.foundation.data.PhysicalCluster;
import com.verr1.valkyrienmanager.util.SerializeUtils;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class ClusterEntry implements IEntry<PhysicalCluster>{

    private final long id;

    private final PhysicalCluster clientView = new PhysicalCluster();

    private static final SerializeUtils.Serializer<Collection<Long>> serializer =
            SerializeUtils.ofCollection(SerializeUtils.LONG);

    public ClusterEntry(long id) {
        this.id = id;
    }



    @Override
    public PhysicalCluster get() {
        return VManagerServer.manager().clusterOf(id);
    }

    @Override
    public Class<PhysicalCluster> getType() {
        return PhysicalCluster.class;
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
}
