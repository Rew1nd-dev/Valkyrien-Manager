package com.verr1.valkyrienmanager.compact.cctweaked.impl;

import com.verr1.valkyrienmanager.foundation.data.VOwnerData;
import com.verr1.valkyrienmanager.foundation.data.VTag;
import com.verr1.valkyrienmanager.manager.db.entry.*;
import com.verr1.valkyrienmanager.manager.db.item.NetworkKey;
import com.verr1.valkyrienmanager.manager.db.item.VItem;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Converter {



    public Map<String, Object> toLua(VItem item){
        Map<String, Object> result = new HashMap<>();
        result.put(NetworkKey.ID.getSerializedName(), parse((IDEntry) item.get(NetworkKey.ID)));
        result.put(NetworkKey.SLUG.getSerializedName(), parse((SlugEntry) item.get(NetworkKey.SLUG)));
        result.put(NetworkKey.CLUSTER.getSerializedName(), parse((ClusterEntry) item.get(NetworkKey.CLUSTER)));
        result.put(NetworkKey.OWNER.getSerializedName(), parse((PlayerSetEntry) item.get(NetworkKey.OWNER)));
        result.put(NetworkKey.BORN_AROUND.getSerializedName(), parse((PlayerSetEntry) item.get(NetworkKey.BORN_AROUND)));
        result.put(NetworkKey.BIRTH.getSerializedName(), parse((BirthEntry) item.get(NetworkKey.BIRTH)));
        result.put(NetworkKey.VTAG.getSerializedName(), parse((VTagEntry) item.get(NetworkKey.VTAG)));
        return result;
    }




    public Object parse(IDEntry entry){
        return entry.get();
    }

    public Object parse(BirthEntry entry){
        return entry.get();
    }

    public Object parse(ClusterEntry entry){
        return entry.get().ids.toArray();
    }

    public Object parse(PlayerSetEntry entry){
        return entry.get().raw.stream().map(VOwnerData::playerName).toArray();
    }

    public Object parse(SlugEntry entry){
        return entry.get();
    }

    public Object parse(VTagEntry entry){
        return entry.get().raw.stream().map(VTag::name).toArray();
    }
}
