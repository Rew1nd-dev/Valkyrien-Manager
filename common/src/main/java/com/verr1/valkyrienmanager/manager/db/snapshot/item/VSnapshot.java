package com.verr1.valkyrienmanager.manager.db.snapshot.item;

import com.verr1.valkyrienmanager.VManagerMod;
import com.verr1.valkyrienmanager.VManagerServer;
import com.verr1.valkyrienmanager.foundation.data.WorldBlockPos;
import kotlin.Pair;
import kotlin.Unit;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.spaceeye.valkyrien_ship_schematics.containers.v1.BlockItem;
import net.spaceeye.valkyrien_ship_schematics.containers.v1.BlockPaletteHashMapV1;
import net.spaceeye.valkyrien_ship_schematics.containers.v1.ChunkyBlockData;
import org.jetbrains.annotations.NotNull;
import org.joml.primitives.AABBi;
import org.joml.primitives.AABBic;
import org.valkyrienskies.core.api.ships.ServerShip;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.valkyrienskies.mod.common.util.VectorConversionsMCKt.toMinecraft;

public class VSnapshot {




    private final BlockPaletteHashMapV1 palette;
    private final List<CompoundTag> savedBeTags;
    private final ChunkyBlockData<BlockItem> blockData;
    private final WorldBlockPos identifier;
    private final Pair<Integer, Integer> yBound;



    VSnapshot(
            BlockPaletteHashMapV1 palette,
            List<CompoundTag> savedBeTags,
            ChunkyBlockData<BlockItem> blockData,
            WorldBlockPos shipPos,
            Pair<Integer, Integer> yBound) {
        this.palette = palette;
        this.savedBeTags = savedBeTags;
        this.blockData = blockData;
        this.identifier = shipPos;
        this.yBound = yBound;

        VManagerMod.LOGGER.info("Snapshot created for ship: " + shipPos + " with extra be tag size: " + size());

    }


    public boolean isValid(){
        return VManagerServer.manager().shipAt(identifier).isPresent();
    }

    private Unit repairAt(ServerLevel blockPlacer, BlockItem item,  int x, int y, int z){
        BlockPos blockPos = new BlockPos(x, y, z);

        return null;
    }

    public static SnapShotConfig config(){
        return VManagerServer.SNAP_DATA_BASE.snapConfig();
    }

    private void repairChunk(ServerLevel blockPlacer, LevelChunk chunk, Map<BlockPos, BlockItem> map){
        map.forEach((s_pos, saved) -> {
            BlockPos pos = new BlockPos(
                    chunk.getPos().getMinBlockX() + s_pos.getX(),
                    s_pos.getY(),
                    chunk.getPos().getMinBlockZ() + s_pos.getZ()
            );
            // VManagerMod.LOGGER.info("Repairing block at: " + pos + " " + blockPlacer.getBlockState(pos).getBlock().getDescriptionId());

            BlockState current = blockPlacer.getBlockState(pos);
            if(config().shouldNeglect(current))return;  // only repair air block

            int paletteId = saved.getPaletteId();
            int extraId = saved.getExtraDataId();
            BlockState state = palette.fromId(paletteId);
            if (state == null)return;

            if(!config().shouldPlace(state))return;



            VManagerMod.LOGGER.info("repairing block: " + state.getBlock().getDescriptionId() + " at: " + pos);

            CompoundTag beTag = Optional.of(extraId).filter(id -> id >= 0 && id < savedBeTags.size())
                    .map(savedBeTags::get)
                    .orElse(new CompoundTag());

            if(!beTag.isEmpty()){
                VManagerMod.LOGGER.info("repairing block entity with size: " + beTag.sizeInBytes());
            }



            blockPlacer.setBlock(pos, state, 3);
            Optional.ofNullable(blockPlacer.getBlockEntity(pos)).ifPresent(be -> be.load(beTag));
        });
    }

    public void repair(ServerLevel blockPlacer){
        blockData.getBlocks().forEach(
            (chunkBlockPos, map) -> {
                ChunkPos cp = new ChunkPos(chunkBlockPos.getX(), chunkBlockPos.getZ()); // that is what defined in spaceeye's code
                LevelChunk chunk = blockPlacer.getChunk(cp.x, cp.z);
                repairChunk(blockPlacer, chunk, map);
            }
        );
    }

    public static @NotNull VSnapshot create(ServerShip ship, ServerLevel level){
        BlockPaletteHashMapV1 palette = new BlockPaletteHashMapV1();
        List<CompoundTag> savedBeTags = new ArrayList<>();
        ChunkyBlockData<BlockItem> blockData = new ChunkyBlockData<>();

        AABBi shipBound = createBound(ship);
        Pair<Integer, Integer> yBound = new Pair<>(
            shipBound.minY(),
            shipBound.maxY()
        );
        List<ChunkPos> allChunkPos = createToIterate(shipBound);
        allChunkPos.forEach(
            chunkPos -> {
                LevelChunk chunkToSave = level.getChunk(chunkPos.x, chunkPos.z);
                for(int ix = 0; ix < 16; ix++){
                for(int iy = yBound.getFirst(); iy < yBound.getSecond(); iy++){
                for(int iz = 0; iz < 16; iz++){
                    BlockPos pos = new BlockPos(
                        chunkPos.getMinBlockX() + ix,
                        iy,
                        chunkPos.getMinBlockZ() + iz
                    );
                    BlockState state = chunkToSave.getBlockState(pos);
                    if(state.isAir())continue;

                    state.getBlock().getDescriptionId();


                    int paletteId = palette.toId(state);

                    // VManagerMod.LOGGER.info("Saving block at: " + pos + " " + chunkToSave.getBlockState(pos).getBlock().getDescriptionId());

                    CompoundTag nullableTag = Optional.ofNullable(chunkToSave.getBlockEntity(pos)).map(BlockEntity::saveWithFullMetadata).orElse(null);



                    int beTagId = Optional
                                    .ofNullable(nullableTag)
                            // not very functional oriented, but I don't care
                                    .map(tag -> {

                                        // VManagerMod.LOGGER.info("Saving block entity with size: " + tag.sizeInBytes());

                                        savedBeTags.add(tag);
                                        return savedBeTags.size() - 1;
                                    })
                                    .orElse(-1);

                    BlockItem item = new BlockItem(paletteId, beTagId);
                    blockData.add(pos.getX(), pos.getY(), pos.getZ(), item);

                }}}
            }
        );
        // just leave a block pos for future checking whether this ship still exists
        BlockPos shipCenter = BlockPos.containing(toMinecraft(ship.getTransform().getPositionInShip()));
        return new VSnapshot(palette, savedBeTags, blockData, WorldBlockPos.of(level, shipCenter), yBound);
    }

    public int size(){
        AtomicInteger sizeOfBytes = new AtomicInteger(0);
        savedBeTags.forEach(
            tag -> {
                sizeOfBytes.addAndGet(tag.sizeInBytes());
        });
        return sizeOfBytes.get();
    }

    public static List<ChunkPos> createToIterate(@NotNull AABBi shipBound){
        BlockPos min = new BlockPos(shipBound.minX(), shipBound.minY(), shipBound.minZ());
        BlockPos max = new BlockPos(shipBound.maxX(), shipBound.maxY(), shipBound.maxZ());
        List<ChunkPos> allChunkPos = new ArrayList<>();
        for (int x = min.getX(); x <= max.getX() + 16; x += 16) {
            for (int z = min.getZ(); z <= max.getZ() + 16; z += 16) {
                allChunkPos.add(new ChunkPos(x >> 4, z >> 4));
            }
        }
        return allChunkPos;
    }

    public static @NotNull AABBi createBound(ServerShip ship){
        AABBic ic = Optional.ofNullable(ship.getShipAABB()).map(AABBi::new).orElse(new AABBi());
        return new AABBi(
            ic.minX() - 1,
            ic.minY() - 1,
            ic.minZ() - 1,
            ic.maxX() + 1,
            ic.maxY() + 1,
            ic.maxZ() + 1
        );
    }

}
