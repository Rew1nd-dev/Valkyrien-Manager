package com.verr1.valkyrienmanager.manager.db.snapshot;

import com.verr1.valkyrienmanager.manager.db.snapshot.item.SnapShotConfig;
import com.verr1.valkyrienmanager.manager.db.snapshot.item.VSnapshot;
import com.verr1.valkyrienmanager.network.ClientBoundSyncSnapConfigPacket;
import com.verr1.valkyrienmanager.registry.VMPackets;
import com.verr1.valkyrienmanager.util.CompoundTagBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class SnapshotDataBase extends SavedData {

    public static final String SNAPSHOT_DATA_NAME = "valkyrienmanager_snapshot_data";

    public Map<Long, VSnapshot> SAVED_SNAP_SHOT = new HashMap<>();

    private final SnapShotConfig snapConfig = new SnapShotConfig();




    public void put(long id, VSnapshot snapshot) {
        SAVED_SNAP_SHOT.put(id, snapshot);
        setDirty();
    }

    public VSnapshot get(long id) {
        return SAVED_SNAP_SHOT.get(id);
    }

    public void remove(long id) {
        SAVED_SNAP_SHOT.remove(id);
        setDirty();
    }

    public void clear() {
        SAVED_SNAP_SHOT.clear();
        setDirty();
    }


    public SnapShotConfig snapConfig() {
        setDirty();
        return snapConfig;
    }

    public void neglect(Block block){
        snapConfig.neglect(block);
        setDirty();
    }

    public void forget(Block block){
        snapConfig.forget(block);
        setDirty();
    }

    public void forbid(Block block){
        snapConfig.forbid(block);
        setDirty();
    }

    public void allow(Block block){
        snapConfig.allow(block);
        setDirty();
    }

    public CompoundTag serialize(){
        return CompoundTagBuilder
                .create()
                .withCompound("snap_config", snapConfig.serialize())
                .build();
    }

    public void deserialize(CompoundTag tag){
        CompoundTag configTag = tag.getCompound("snap_config");
        snapConfig.deserialize(configTag);
    }


    @Override
    public @NotNull CompoundTag save(CompoundTag compoundTag) {
        compoundTag.put("database", serialize());
        return compoundTag;
    }

    public static SnapshotDataBase create(){
        return new SnapshotDataBase();
    }

    private static SnapshotDataBase load(@NotNull CompoundTag tag) {
        SnapshotDataBase saved = new SnapshotDataBase();
        saved.deserialize(tag.getCompound("database"));
        return saved;
    }


    public static SnapshotDataBase load(MinecraftServer server){
        return server.overworld().getDataStorage().computeIfAbsent(
                SnapshotDataBase::load,
                SnapshotDataBase::create,
                SNAPSHOT_DATA_NAME
        );
    }

    public void responseSyncSnapConfig(Player sender) {
        if(!(sender instanceof ServerPlayer serverPlayer)){
            throw new IllegalStateException("Sender is not a server player");
        }
        VMPackets.CHANNEL.sendToPlayer(serverPlayer, new ClientBoundSyncSnapConfigPacket(snapConfig.serialize()));
    }
}
