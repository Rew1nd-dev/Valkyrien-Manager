package com.verr1.valkyrienmanager.manager.db.general;

import com.verr1.valkyrienmanager.VManagerMod;
import com.verr1.valkyrienmanager.foundation.data.VTag;
import com.verr1.valkyrienmanager.manager.VManager;
import com.verr1.valkyrienmanager.manager.db.general.item.NetworkKey;
import com.verr1.valkyrienmanager.manager.db.general.item.VItem;
import com.verr1.valkyrienmanager.network.*;
import com.verr1.valkyrienmanager.registry.VMPackets;
import com.verr1.valkyrienmanager.util.CompoundTagBuilder;
import it.unimi.dsi.fastutil.longs.Long2ObjectRBTreeMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.verr1.valkyrienmanager.foundation.data.VTag.INVALID;

public class VDataBase extends SavedData {

    public static final String DATA_NAME = VManagerMod.MOD_ID + "_vdatabase";

    private final Map<Long, VItem> V_DATA = new Long2ObjectRBTreeMap<>();

    private final HashMap<String, VTag> ALL_V_TAGS = new HashMap<>();

    public VTag of(String name){
        if(ALL_V_TAGS.containsKey(name)){
            return ALL_V_TAGS.get(name);
        }
        return INVALID;
    }

    public HashMap<String, VTag> vtags(){
        return ALL_V_TAGS;
    }

    public void register(String name){
        ALL_V_TAGS.computeIfAbsent(name, VTag::new);
        setDirty();
    }

    public void unregister(String name){
        ALL_V_TAGS.remove(name);
        setDirty();
    }

    @NotNull
    public VItem get(long id){
        return V_DATA.getOrDefault(id, VItem.INVALID);
    }

    /*
    * public List<List<Long>> query(
            @NotNull Comparator<Map.Entry<Long, VItem>> comparator,
            @NotNull Predicate<Map.Entry<Long, VItem>> filter,
            @NotNull  groupBy
    ){

    }
    * */


    @NotNull
    public Optional<VItem> getOptional(long id){
        return Optional.ofNullable(V_DATA.get(id));
    }

    public void put(long id, VItem item){
        V_DATA.put(id, item);
        setDirty();
    }

    public void remove(long id){
        V_DATA.remove(id);
        setDirty();
    }

    public Map<Long, VItem> data(){
        setDirty();
        return V_DATA;
    }

    public void drop(){
        V_DATA.clear();
        setDirty();
    }

    //should only send to client
    public CompoundTag send(long id, NetworkKey<?>... keys){

        CompoundTag tag = V_DATA.computeIfAbsent(id, $ -> VItem.INVALID).send(keys);

        return CompoundTagBuilder.create().withLong("id", id)
                .withCompound("data", tag)
                .build();

    }

    // should only in client side
    public void receive(CompoundTag content){
        if(!content.contains("id"))return;
        if(!content.contains("data"))return;

        long id = content.getLong("id");

        if(id == VManager.INVALID_ID){
            V_DATA.remove(id);
            return;
        }

        CompoundTag data = content.getCompound("data");
        V_DATA.computeIfAbsent(id, VItem::createEmpty).receive(data);
    }

    // should only call in client
    public void request(long id, NetworkKey<?>... keys){
        VMPackets.CHANNEL.sendToServer(new ServerBoundRequestVDataPacket(id, keys));
    }

    // should only call in server
    public void response(long id, @Nullable Player sender, NetworkKey<?>... keys){
        if(!(sender instanceof ServerPlayer serverPlayer)){
            throw new IllegalStateException("Sender is not a server player");
        }
        if(id == VManager.REQUEST_ALL_ID){
            V_DATA.keySet().forEach(
                    k -> VMPackets.CHANNEL.sendToPlayer(serverPlayer, new ClientBoundVDataSyncPacket(send(k, keys)))
            );
        }else{
            VMPackets.CHANNEL.sendToPlayer(serverPlayer, new ClientBoundVDataSyncPacket(send(id, keys)));
        }

    }


    public void requestSyncKeys(){
        VMPackets.CHANNEL.sendToServer(new ServerBoundRequestSyncKeysPacket());
    }

    public void responseSyncKeys(Player target){
        if(!(target instanceof ServerPlayer serverPlayer)){
            throw new IllegalStateException("Sender is not a server player");
        }
        VMPackets.CHANNEL.sendToPlayer(serverPlayer, new ClientBoundSyncKeysPacket(V_DATA.keySet().stream().toList()));
    }

    public void receiveSyncedKey(Set<Long> validKeys){
        V_DATA.keySet().removeIf(k -> !validKeys.contains(k));
    }


    public void requestSyncTags(){
        VMPackets.CHANNEL.sendToServer(new ServerBoundRequestSyncTagsPacket());
    }

    public void responseSyncTags(Player target){
        if(!(target instanceof ServerPlayer serverPlayer)){
            throw new IllegalStateException("Sender is not a server player");
        }
        VMPackets.CHANNEL.sendToPlayer(serverPlayer, new ClientBoundSyncTagsPacket(ALL_V_TAGS.values().stream().toList()));
    }

    public void receiveSyncedTag(List<VTag> vTags){
        ALL_V_TAGS.clear();
        vTags.forEach(vTag -> ALL_V_TAGS.put(vTag.name(), vTag));
    }


    public CompoundTag serialize(){
        CompoundTag tag = new CompoundTag();
        CompoundTag dataTag = new CompoundTag();
        CompoundTag registeredTag = new CompoundTag();
        int saveCounter = 0;
        for(var entry : V_DATA.entrySet()){
            try{
                dataTag.put(String.valueOf(saveCounter), entry.getValue().serialize());
                saveCounter++;
            }catch (Exception e){
                throw new RuntimeException(e);
            }
        }

        int vtag_saveCounter = 0;
        for (VTag vtag : ALL_V_TAGS.values()) {
            try {
                registeredTag.put(String.valueOf(vtag_saveCounter), vtag.serialize());
                vtag_saveCounter++;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        tag.put("data", dataTag);
        tag.putInt("size", saveCounter);

        tag.putInt("vtags_size", vtag_saveCounter);
        tag.put("vtags", registeredTag);
        return tag;
    }

    public void deserialize(CompoundTag tag){
        if(tag.contains("data")){
            CompoundTag dataTag = tag.getCompound("data");
            int size = tag.getInt("size");
            for(int i = 0; i < size; i++){
                try {
                    var savedData = VItem.deserialize(dataTag.getCompound(String.valueOf(i)));
                    V_DATA.put(savedData.get(NetworkKey.ID).get(), savedData);
                } catch (Exception e){
                    VManagerMod.LOGGER.error("Failed to load VDataBase of Entry: ", e);
                }
            }
        }
        if(tag.contains("vtags")){
            CompoundTag vtagTag = tag.getCompound("vtags");
            int size = tag.getInt("vtags_size");
            for(int i = 0; i < size; i++){
                VTag vTag = VTag.deserialize(vtagTag.getCompound(String.valueOf(i)));
                register(vTag.name());
            }
        }
    }




    @Override
    public @NotNull CompoundTag save(CompoundTag compoundTag) {
        compoundTag.put("database", serialize());
        return compoundTag;
    }

    public static VDataBase create(){
        return new VDataBase();
    }

    private static VDataBase load(@NotNull CompoundTag tag) {
        VDataBase saved = new VDataBase();
        saved.deserialize(tag.getCompound("database"));
        return saved;
    }


    public static VDataBase load(MinecraftServer server){
        return server.overworld().getDataStorage().computeIfAbsent(VDataBase::load, VDataBase::create, DATA_NAME);
    }

    /*
    *   If Client View Request An ID Which Does Not Exist on Server, It receives a INVALID_ID and will remove VItem
    *   If Client View Request An ID Which Does Not Exist on Client, It creates an Empty One and deserialize it
    *   Otherwise Client View just deserialize it and override
    *
    * */

}
