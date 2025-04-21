package com.verr1.valkyrienmanager.compact.cctweaked.impl;

import com.verr1.valkyrienmanager.VManagerServer;
import com.verr1.valkyrienmanager.foundation.data.VTag;
import com.verr1.valkyrienmanager.manager.VManager;
import com.verr1.valkyrienmanager.manager.db.VDataBase;
import com.verr1.valkyrienmanager.manager.db.item.NetworkKey;
import com.verr1.valkyrienmanager.manager.db.item.VItem;
import dan200.computercraft.api.lua.ILuaAPI;
import dan200.computercraft.api.lua.LuaFunction;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;


public class VMApi implements ILuaAPI {


    /**
     * Get the globals this API will be assigned to.
     * <p>
     * This will override any other global, so you should be careful to pick a unique name. Alternatively, you may
     * return the empty array here, and instead override {@link #getModuleName()}.
     *
     * @return A list of globals this API will be assigned to.
     */
    @Override
    public String[] getNames() {
        return new String[]{"VMApi"};
    }


    private VManager manager(){
        return VManagerServer.manager();
    }

    private VDataBase db(){
        return VManagerServer.DATA_BASE;
    }

    @LuaFunction
    public final List<String> QueryTagsOf(long id){
        Optional<VItem> query = db().getOptional(id);
        return query.map(it -> it.get(NetworkKey.VTAG).get().raw.stream().map(VTag::name).toList()).orElse(List.of("Item not found"));
    }

    @LuaFunction
    public final Map<String, Object> Query(long id){
        Optional<VItem> query = db().getOptional(id);
        return query.map(it -> new Converter().toLua(it)).orElse(Map.of("Item not found", "Item not found"));
    }

    @LuaFunction
    public final List<Long> QueryAllKeys(){
        return db().data().keySet().stream().toList();
    }

    @LuaFunction
    public final String AddTagTo(long id, String tag){
        Optional<VItem> query = db().getOptional(id);
        query.ifPresent(it -> it.get(NetworkKey.VTAG).get().raw.add(VTag.of(tag)));
        return query.isEmpty() ? "Item not found" : "Tag added";
    }

    @LuaFunction
    public final String AddTagToCluster(long id, String tag){
        AtomicInteger success = new AtomicInteger(0);
        AtomicInteger fail = new AtomicInteger(0);
        manager().clusterOf(id).ids.forEach(
            it -> {

                Optional<VItem> query = db().getOptional(id);
                query.ifPresent(jt -> jt.get(NetworkKey.VTAG).get().raw.add(VTag.of(tag)));

                if(query.isEmpty()){
                    success.getAndIncrement();
                }else{
                    fail.getAndIncrement();
                }
            }
        );
        return "Tag added to " + success.get() + " items, failed to add to " + fail.get() + " items";
    }

    @LuaFunction
    public final String RemoveTagFrom(long id, String tag){
        Optional<VItem> query = db().getOptional(id);
        query.ifPresent(it -> it.get(NetworkKey.VTAG).get().raw.remove(VTag.of(tag)));
        return query.isEmpty() ? "Item not found" : "Tag removed";
    }


}
