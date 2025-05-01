package com.verr1.valkyrienmanager.compact.cctweaked.impl;

import com.verr1.valkyrienmanager.VManagerServer;
import com.verr1.valkyrienmanager.foundation.data.VTag;
import com.verr1.valkyrienmanager.manager.VManager;
import com.verr1.valkyrienmanager.manager.db.general.VDataBase;
import com.verr1.valkyrienmanager.manager.db.general.item.NetworkKey;
import com.verr1.valkyrienmanager.manager.db.general.item.VItem;
import com.verr1.valkyrienmanager.util.Converter;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaAPI;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;

import java.util.*;
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

    @LuaFunction(mainThread = true)
    public final List<String> QueryTagsOf(long id){
        Optional<VItem> query = db().getOptional(id);
        return query.map(it -> it.get(NetworkKey.VTAG).get().raw.stream().map(VTag::name).toList()).orElse(List.of("Item not found"));
    }

    @LuaFunction(mainThread = true)
    public final Map<String, Object> Query(long id){
        Optional<VItem> query = db().getOptional(id);
        return query.map(Converter::toLua).orElse(Map.of("Item not found", "Item not found"));
    }

    @LuaFunction(mainThread = true)
    public final List<Long> QueryAllKeys(){
        return db().data().keySet().stream().toList();
    }

    @LuaFunction(mainThread = true)
    public final String AddTagsTo(IArguments tagsIA) throws LuaException {
        long id = tagsIA.getLong(0);
        String[] tag = convert(tagsIA.drop(1));

        Optional<VItem> query = db().getOptional(id);
        query.ifPresent(it -> it.get(NetworkKey.VTAG).get().raw.addAll(Arrays.stream(tag).map(n -> db().of(n)).toList()));
        return query.isEmpty() ? "Item not found" : "Tag added";
    }

    @LuaFunction(mainThread = true)
    public final String RemoveTagsFrom(IArguments tagsIA) throws LuaException {

        long id = tagsIA.getLong(0);
        String[] tag = convert(tagsIA.drop(1));

        Optional<VItem> query = db().getOptional(id);
        query.ifPresent(it -> {
            var set = it.get(NetworkKey.VTAG).get().raw;
            Arrays.stream(tag).map(n -> db().of(n)).forEach(set::remove);
        });
        return query.isEmpty() ? "Item not found" : "Tag removed";
    }

    @LuaFunction(mainThread = true)
    public final String AddTagsToCluster(IArguments tagsIA) throws LuaException {

        long id = tagsIA.getLong(0);
        String[] tag = convert(tagsIA.drop(1));

        AtomicInteger success = new AtomicInteger(0);
        AtomicInteger fail = new AtomicInteger(0);
        manager().clusterOf(id).ids.forEach(
            it -> {

                Optional<VItem> query = db().getOptional(id);
                query.ifPresent(jt -> jt.get(NetworkKey.VTAG).get().raw.addAll(Arrays.stream(tag).map(n -> db().of(n)).toList()));

                if(query.isEmpty()){
                    success.getAndIncrement();
                }else{
                    fail.getAndIncrement();
                }
            }
        );
        return "Tag added to " + success.get() + " items, failed to add to " + fail.get() + " items";
    }

    @LuaFunction(mainThread = true)
    public final String RemoveTagsFromCluster(IArguments tagsIA) throws LuaException {

        long id = tagsIA.getLong(0);
        String[] tag = convert(tagsIA.drop(1));

        AtomicInteger success = new AtomicInteger(0);
        AtomicInteger fail = new AtomicInteger(0);
        manager().clusterOf(id).ids.forEach(
                it -> {

                    Optional<VItem> query = db().getOptional(id);

                    query.ifPresent(jt -> {
                        var set = jt.get(NetworkKey.VTAG).get().raw;
                        Arrays.stream(tag).map(n -> db().of(n)).forEach(set::remove);
                    });

                    if(query.isEmpty()){
                        success.getAndIncrement();
                    }else{
                        fail.getAndIncrement();
                    }
                }
        );
        return "Tag added to " + success.get() + " items, failed to add to " + fail.get() + " items";
    }


    private static String[] convert(IArguments ia){
        List<String> result = new ArrayList<>();
        for(int i = 0; i < ia.count(); i++){
            try{
                ia.optString(i).ifPresent(result::add);
            }catch (LuaException ignored){

            }
        }
        return result.toArray(new String[0]);
    }


}
