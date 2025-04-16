package com.verr1.valkyrienmanager.foundation.data;

import java.util.*;

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

    public boolean contains(long id) {
        return ids.contains(id);
    }

    public String toString(){
        return "Clustered Ships: " + ids;
    }

}
