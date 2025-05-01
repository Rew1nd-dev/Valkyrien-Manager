package com.verr1.valkyrienmanager.manager.db.general.entry;

import com.verr1.valkyrienmanager.foundation.data.VTag;
import com.verr1.valkyrienmanager.util.SerializeUtils;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class VTagEntry implements IEntry<VTagEntry.VTags>{


    private static final SerializeUtils.Serializer<Collection<VTag>> serializer =
            SerializeUtils.ofCollection(
                    SerializeUtils.of(
                            VTag::serialize,
                            VTag::deserialize
                    )
            );

    private final VTags tags = new VTags();

    @Override
    public VTags get() {
        return tags;
    }

    @Override
    public Class<VTags> getType() {
        return VTags.class;
    }

    /**
     * Retrieves the value of the entry for client-side read operations.
     * This method should only be called on the client side.
     *
     * @return The value of type V.
     */
    @Override
    public VTags view() {
        return tags;
    }

    @Override
    public void set(VTags value) {
        if(value == tags)return; // does nothing if the reference is the same
        tags.raw.clear();
        tags.raw.addAll(value.raw);
    }


    @Override
    public @NotNull CompoundTag serialize() {
        return serializer.serialize(tags.raw);
    }

    @Override
    public void deserialize(@NotNull CompoundTag tag) {
        tags.raw.clear();
        tags.raw.addAll(serializer.deserialize(tag));
    }

    public static class VTags{
        public final Set<VTag> raw = new HashSet<>();
    }
}
