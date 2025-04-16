package com.verr1.valkyrienmanager.manager.db;

import com.verr1.valkyrienmanager.ValkyrienManager;
import com.verr1.valkyrienmanager.foundation.data.VTag;
import it.unimi.dsi.fastutil.longs.Long2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectRBTreeMap;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class VSMDataBase {

    public static final Map<Long, VDataExtension> V_DATA = new Long2ObjectRBTreeMap<>();

    public static final Set<VTag> REGISTERED_TAGS = new HashSet<>();

    public static @NotNull VDataExtension get(long shipID){
        return V_DATA.computeIfAbsent(shipID, id -> VDataExtension.EMPTY);
    }

    public static void put(VDataExtension shipData){
        ValkyrienManager.LOGGER.info("Adding ship data for shipID: {}", shipData.getId());
        V_DATA.put(shipData.getId(), shipData);
    }

    public static void remove(long shipID){
        ValkyrienManager.LOGGER.info("Removing ship data for shipID: {}", shipID);
        V_DATA.remove(shipID);
    }

    public static boolean has(long id){
        return V_DATA.containsKey(id);
    }


    public static void createTag(VTag tag){
        REGISTERED_TAGS.add(tag);
    }

    public static void removeTag(VTag tag){
        REGISTERED_TAGS.remove(tag);
    }

    public static Set<Long> query(Predicate<VDataExtension> filter){
        return V_DATA.values().stream()
                .filter(filter)
                .map(VDataExtension::getId)
                .collect(HashSet::new, HashSet::add, HashSet::addAll);
    }

}
