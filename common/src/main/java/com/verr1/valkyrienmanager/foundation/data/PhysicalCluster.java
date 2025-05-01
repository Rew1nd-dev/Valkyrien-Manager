package com.verr1.valkyrienmanager.foundation.data;

import com.verr1.valkyrienmanager.VManagerServer;
import org.valkyrienskies.core.api.ships.ServerShip;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class PhysicalCluster {
    public static PhysicalCluster EMPTY = new PhysicalCluster();

    public final Set<Long> ids = new HashSet<>();

    public PhysicalCluster() {

    }

    public PhysicalCluster add(long id) {
        ids.add(id);
        return this;
    }

    public PhysicalCluster addAll(Collection<Long> ids) {
        this.ids.addAll(ids);
        return this;
    }

    public long heuristicID(){
        AtomicLong hash = new AtomicLong(0);
        ids.forEach(id -> hash.set(hash.get() ^ id.hashCode()));
        return hash.get();
    }

    public boolean contains(long id) {
        return ids.contains(id);
    }

    public String toString(){
        return "Clustered Ships: " + ids;
    }

    public void forEachShip(Consumer<ServerShip> consumer) {
        ids.forEach(id -> VManagerServer.manager().shipOf(id).ifPresent(consumer));

    }

}
