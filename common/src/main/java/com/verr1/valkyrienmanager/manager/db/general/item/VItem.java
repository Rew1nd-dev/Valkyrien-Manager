package com.verr1.valkyrienmanager.manager.db.general.item;

import com.verr1.valkyrienmanager.manager.VManager;
import com.verr1.valkyrienmanager.manager.db.general.entry.*;
import com.verr1.valkyrienmanager.util.CompoundTagBuilder;
import net.minecraft.nbt.CompoundTag;

import java.util.*;

public class VItem {

    public static VItem INVALID = new VItem(VManager.INVALID_ID);

    private final long id;

    private final Map<NetworkKey<?>, IEntry<?>> entries = new HashMap<>();

    public static VItem createEmpty(long id){
        var toBuild = new VItem(id);
        toBuild.get(NetworkKey.BIRTH).set(System.currentTimeMillis());
        return toBuild;
    }

    public static NetworkKey<?>[] used = {
            NetworkKey.ID,
            NetworkKey.SLUG,
            NetworkKey.CLUSTER,
            NetworkKey.OWNER,
            NetworkKey.BORN_AROUND,
            NetworkKey.BIRTH,
            NetworkKey.VTAG,
            NetworkKey.AABB,
            NetworkKey.COORDINATE
    };

    VItem(long id){
        this.id = id;

        entries.put(NetworkKey.ID, new IDEntry(id));
        entries.put(NetworkKey.SLUG, new SlugEntry(id));
        entries.put(NetworkKey.CLUSTER,  new ClusterEntry(id));
        entries.put(NetworkKey.OWNER, new PlayerSetEntry());
        entries.put(NetworkKey.BORN_AROUND, new PlayerSetEntry());
        entries.put(NetworkKey.BIRTH, new BirthEntry());
        entries.put(NetworkKey.VTAG, new VTagEntry());
        entries.put(NetworkKey.AABB, new AABBEntry(id));
        entries.put(NetworkKey.COORDINATE, new CoordinateEntry(id));
    }

    @SuppressWarnings("unchecked") // It's CHECKED
    public<T> IEntry<T> get(NetworkKey<T> key){
        if(!entries.containsKey(key)){
            throw new IllegalArgumentException("VItem does not contain key: " + key);
        }
        IEntry<?> entry = entries.get(key);
        if(!entry.getType().equals(key.clazz())){
            throw new IllegalArgumentException("VItem entry type mismatch: " + key + " is not of type " + key.clazz());
        }
        return (IEntry<T>) entry;
    }

    @SuppressWarnings("unchecked") // It's CHECKED
    public<T, E extends IEntry<T>> E get(NetworkKey<T> key, Class<E> entryClass){
        if(!entries.containsKey(key)){
            throw new IllegalArgumentException("VItem does not contain key: " + key);
        }
        IEntry<?> entry = entries.get(key);
        if(!entry.getType().equals(key.clazz())){
            throw new IllegalArgumentException("VItem entry type mismatch: " + key + " is not of type " + key.clazz());
        }

        if(!entryClass.isAssignableFrom(entry.getClass())){
            throw new IllegalArgumentException("VItem entry class mismatch: " + key + " is not of type " + entryClass);
        }

        return (E) entry;
    }

    public boolean has(NetworkKey<?> key){
        return entries.containsKey(key);
    }

    public CompoundTag serialize(){
        CompoundTagBuilder entriesTag = new CompoundTagBuilder();
        entries.forEach((key, value) -> {
            if(!value.shouldSave())return;
            entriesTag.withCompound(key.getSerializedName(), value.serialize());
        });

        return new CompoundTagBuilder()
                .withLong("id", id)
                .withCompound("entries", entriesTag.build())
                .build();
    }

    public static VItem deserialize(CompoundTag tag){
        long id = tag.getLong("id");
        CompoundTag entryTag = tag.getCompound("entries");
        VItem item = new VItem(id);
        item.entries.forEach((key, value) -> {
            if(!entryTag.contains(key.getSerializedName()))return;
            value.deserialize(entryTag.getCompound(key.getSerializedName()));
        });
        return item;
    }


    public void receive(CompoundTag content){
        if(id == VManager.INVALID_ID){
            throw new IllegalStateException("VItem is invalid, cannot receive data");
        }
        entries
            .keySet()
            .stream().filter(k -> content.contains(k.getSerializedName()))
            .forEach(k -> entries.get(k).deserializePacket(content.getCompound(k.getSerializedName())));
    }

    public CompoundTag send(NetworkKey<?>... keys){
        CompoundTagBuilder builder = new CompoundTagBuilder();
        Arrays
            .stream(keys)
            .forEach(
                k -> builder
                        .withCompound(
                                k.getSerializedName(),
                                entries.get(k).serializePacket()
                        )
            );
        return builder.build();
    }



    public static class builder{

        VItem toBuild;

        public builder(long id){
            toBuild = new VItem(id);
            toBuild.get(NetworkKey.BIRTH).set(System.currentTimeMillis());

        }

        /*
        public builder withPlayersAround(Collection<ServerPlayer> around){
            toBuild.bornAround.set(around.stream().map(VOwnerData::of).collect(Collectors.toSet()));
            return this;
        }

        public builder withOwner(Collection<Player> owners){
            toBuild.owners.set(owners.stream().map(VOwnerData::of).collect(Collectors.toSet()));
            return this;
        }

        * */




        public<T> builder with(NetworkKey<T> key, T object){
            if(!toBuild.entries.containsKey(key)){
                throw new IllegalArgumentException("VItem does not contain key: " + key);
            }
            IEntry<T> entry = toBuild.get(key);
            entry.set(object);
            return this;
        }


        public VItem build(){
            return toBuild;
        }



    }

}
