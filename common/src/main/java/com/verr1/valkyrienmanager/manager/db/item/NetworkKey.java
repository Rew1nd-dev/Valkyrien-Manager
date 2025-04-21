package com.verr1.valkyrienmanager.manager.db.item;

import com.verr1.valkyrienmanager.foundation.data.PhysicalCluster;
import com.verr1.valkyrienmanager.gui.factory.Formatter;
import com.verr1.valkyrienmanager.manager.db.entry.PlayerSetEntry;
import com.verr1.valkyrienmanager.manager.db.entry.VTagEntry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;


/*
*   A NetworkKey is corresponded to exactly one entry of a ship Item in database
*   and exactly one Formatter to display entry information
*
*
* */

public class NetworkKey<T> implements StringRepresentable {

    public static final Map<String, NetworkKey<?>> REGISTRY = new HashMap<>();

    public static final SimpleDateFormat Date_Formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static final NetworkKey<Long> ID = create("id", Long.class, l -> List.of("id: " + l));
    public static final NetworkKey<String> SLUG = create("slug", String.class, s -> List.of("slug: " + s));
    public static final NetworkKey<PhysicalCluster> CLUSTER = create("cluster", PhysicalCluster.class, p -> List.of("cluster: " + p.ids.toString()));
    public static final NetworkKey<PlayerSetEntry.Wrapper> OWNER = create("owner", PlayerSetEntry.Wrapper.class, p -> List.of("owner: " + p.raw.toString()));
    public static final NetworkKey<PlayerSetEntry.Wrapper> BORN_AROUND = create("born_around", PlayerSetEntry.Wrapper.class, p -> List.of("Born Around: " + p.raw.toString()));
    public static final NetworkKey<Long> BIRTH = create("birth", Long.class, p -> List.of("birth: " + Date_Formatter.format(p)));
    public static final NetworkKey<VTagEntry.VTags> VTAG = create("vtag", VTagEntry.VTags.class, p -> List.of("tags: " + p.raw.toString()));
    public static final NetworkKey<Void> EMPTY = create("empty", Void.class, $ -> List.of("empty"));

    private final String key;
    private final Class<T> clazz;
    private final Formatter<T> formatter;

    public Class<T> clazz() {
        return clazz;
    }

    public Formatter<T> formatter() {
        return formatter;
    }


    private NetworkKey(String key, Class<T> type, Formatter<T> formatter){
        if(REGISTRY.containsKey(key)){
            throw new IllegalArgumentException("Key already exists: " + key);
        }
        this.key = key;
        this.clazz = type;
        this.formatter = formatter;
        REGISTRY.put(key, this);
    }

    public static<T> NetworkKey<T> create(String key, Class<T> type, Function<T, List<String>> formatter){
        return new NetworkKey<>(key, type, new Formatter<>() {
            @Override
            public List<String> apply(T obj) {
                return formatter.apply(obj);
            }

            @Override
            public Class<T> clazz() {
                return type;
            }
        });
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
