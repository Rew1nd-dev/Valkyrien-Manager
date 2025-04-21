package com.verr1.valkyrienmanager.gui.factory;

import java.util.List;

public interface Formatter<T> {


    List<String> apply(T obj);

    Class<T> clazz();

    default List<String> applyUnsafe(Object obj){
        if(obj.getClass().isAssignableFrom(clazz())){
            return apply(clazz().cast(obj));
        } else {
            throw new IllegalArgumentException("Object is not of type " + clazz().getName());
        }
    }
}
