package com.verr1.valkyrienmanager.manager.db.entry;

import com.verr1.valkyrienmanager.foundation.data.VOwnerData;
import com.verr1.valkyrienmanager.util.SerializeUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class PlayerSetEntry implements IEntry<PlayerSetEntry.Wrapper>{

    private final Wrapper ownership = new Wrapper();

    private static final SerializeUtils.Serializer<Collection<VOwnerData>> serializer =
            SerializeUtils.ofCollection(
                    SerializeUtils.of(
                            VOwnerData::serialize,
                            VOwnerData::deserialize
                    )
            );

    @Override
    public Wrapper get() {
        return ownership;
    }

    @Override
    public Class<Wrapper> getType() {
        return Wrapper.class;
    }

    @Override
    public void set(Wrapper value) {
        if(value == ownership)return; // does nothing if the reference is the same
        ownership.raw.clear();
        ownership.raw.addAll(value.raw);
    }

    public void set(Set<VOwnerData> value) {
        ownership.raw.clear();
        ownership.raw.addAll(value);
    }

    @Override
    public @NotNull CompoundTag serialize() {
        /*
        *
        AtomicInteger count = new AtomicInteger();
        CompoundTagBuilder builder = new CompoundTagBuilder().withLong("count", (long)OnCreation.size());
        OnCreation.forEach(d -> builder.withCompound("element_" + count.getAndIncrement(), d.serialize()));
        return builder.build();
        * */
        return serializer.serialize(ownership.raw);
    }

    @Override
    public void deserialize(@NotNull CompoundTag tag) {
        /*
        long count = tag.getLong("count");
        LongStream.range(0, count).forEach(i -> OnCreation.add(VOwnerData.deserialize(tag.getCompound("element_" + i))));
        * */
        ownership.raw.clear();
        ownership.raw.addAll(serializer.deserialize(tag));
    }


    public static class Wrapper{

        public static Wrapper of(Collection<ServerPlayer> players){
            Wrapper wrapper = new Wrapper();
            players.forEach(player -> wrapper.raw.add(VOwnerData.of(player)));
            return wrapper;
        }

        public final Set<VOwnerData> raw = new HashSet<>();

        public boolean containsName(String name){
            return raw.stream().anyMatch(data -> data.playerName().equals(name));
        }

    }
}
