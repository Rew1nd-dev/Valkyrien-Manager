package com.verr1.valkyrienmanager.manager.db.general.item;

import com.verr1.valkyrienmanager.foundation.data.PhysicalCluster;
import com.verr1.valkyrienmanager.manager.db.general.entry.PlayerSetEntry;
import com.verr1.valkyrienmanager.manager.db.general.entry.VTagEntry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3dc;
import org.joml.primitives.AABBic;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;


/*
*   A NetworkKey is corresponded to exactly one entry of a ship Item in database
*   and exactly one Formatter to display entry information
*
*
* */

public class NetworkKey<T> implements StringRepresentable {

    public static final Map<String, NetworkKey<?>> REGISTRY = new HashMap<>();

    public static final SimpleDateFormat Date_Formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static final NetworkKey<Long> ID = create("id", Long.class);
    public static final NetworkKey<String> SLUG = create("slug", String.class);
    public static final NetworkKey<PhysicalCluster> CLUSTER = create("cluster", PhysicalCluster.class);
    public static final NetworkKey<PlayerSetEntry.Wrapper> OWNER = create("owner", PlayerSetEntry.Wrapper.class);
    public static final NetworkKey<PlayerSetEntry.Wrapper> BORN_AROUND = create("born_around", PlayerSetEntry.Wrapper.class);
    public static final NetworkKey<Long> BIRTH = create("birth", Long.class);
    public static final NetworkKey<VTagEntry.VTags> VTAG = create("vtag", VTagEntry.VTags.class);
    public static final NetworkKey<AABBic> AABB = create("aabb", AABBic.class);
    public static final NetworkKey<Vector3dc> COORDINATE = create("coordinate", Vector3dc.class);

    public static final NetworkKey<Void> EMPTY = create("empty", Void.class);

    private final String key;
    private final Class<T> clazz;

    public Class<T> clazz() {
        return clazz;
    }



    private NetworkKey(String key, Class<T> type){
        if(REGISTRY.containsKey(key)){
            throw new IllegalArgumentException("Key already exists: " + key);
        }
        this.key = key;
        this.clazz = type;
        REGISTRY.put(key, this);
    }

    public static<T> NetworkKey<T> create(String key, Class<T> type){
        return new NetworkKey<>(key, type);
    }

    public CompoundTag serialize(){
        CompoundTag t = new CompoundTag();
        t.putString("key", key);
        return t;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof NetworkKey<?> key_))return false;
        return key.equals(key_.key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    public static NetworkKey<?> deserialize(CompoundTag t){
        return REGISTRY.getOrDefault(t.getString("key"), EMPTY);
    }



    @Override
    public @NotNull String getSerializedName() {
        return key;
    }
}
