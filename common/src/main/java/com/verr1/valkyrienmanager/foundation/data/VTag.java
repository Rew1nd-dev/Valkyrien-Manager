package com.verr1.valkyrienmanager.foundation.data;

import com.verr1.valkyrienmanager.VManagerServer;
import com.verr1.valkyrienmanager.util.CompoundTagBuilder;
import net.minecraft.nbt.CompoundTag;

import java.util.HashMap;

public class VTag {
    public static final VTag INVALID = new VTag("INVALID");


    private final String name;

    public VTag(String name) {
        this.name = name;
    }

    public static VTag of(String name){
        return VManagerServer.DATA_BASE.of(name);
    }

    public String name() {
        return name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof VTag vTag){
            return name.equals(vTag.name);
        }
        return false;
    }

    public CompoundTag serialize(){
        return CompoundTagBuilder.create().withString("name", name).build();
    }

    public static VTag deserialize(CompoundTag tag){
        return new VTag(tag.getString("name"));
    }

    @Override
    public String toString() {
        return "VTag{" +
                "name='" + name + '\'' +
                '}';
    }
}
