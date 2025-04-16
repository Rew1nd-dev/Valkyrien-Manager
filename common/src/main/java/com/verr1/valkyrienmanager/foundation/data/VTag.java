package com.verr1.valkyrienmanager.foundation.data;

public class VTag {

    public String name() {
        return name;
    }

    public String name = "";

    public static VTag of(String name){
        VTag tag = new VTag();
        tag.name = name;
        return tag;
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

    @Override
    public String toString() {
        return "VTag{" +
                "name='" + name + '\'' +
                '}';
    }
}
