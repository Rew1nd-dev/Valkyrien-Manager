package com.verr1.valkyrienmanager.vs;

import com.verr1.valkyrienmanager.VManagerServer;
import com.verr1.valkyrienmanager.manager.events.VSMEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.valkyrienskies.mod.common.util.VectorConversionsMCKt.toMinecraft;

public class VSTracker {

    public static final int LAZY_TICK_RATE = 20; // 1 second

    public final HashMap<Long, Boolean> LastTimeInsideUnloadedChunk = new HashMap<>();

    private int counter = 0;

    public void lazyTick(){
        if (--counter > 0)return;
        counter = LAZY_TICK_RATE;

        VManagerServer.manager().allShips().forEach(
                s -> {
                    ServerLevel lvl = VSGameUtilsKt.getLevelFromDimensionId(VManagerServer.INSTANCE, s.getChunkClaimDimension());
                    Vector3dc v = s.getTransform().getPositionInWorld();
                    boolean currentInsideLoadedChunk = Optional.ofNullable(lvl).map(
                            level -> level
                                    .getChunkSource()
                                    .isPositionTicking(
                                            new ChunkPos(
                                                    BlockPos
                                                            .containing(toMinecraft(v))).toLong())
                    ).orElse(false);
                    boolean lastTimeInsideUnloadedChunk = LastTimeInsideUnloadedChunk.getOrDefault(s.getId(), false);
                    LastTimeInsideUnloadedChunk.put(s.getId(), currentInsideLoadedChunk);

                    if(currentInsideLoadedChunk != lastTimeInsideUnloadedChunk){
                        if(currentInsideLoadedChunk){
                            fireLoadEvent(s.getId());
                        }else{
                            fireUnloadEvent(s.getId());

                        }
                    }

                }
        );

    }

    public void fireLoadEvent(Long id){
        VManagerServer.manager().shipOf(id).ifPresent(
                VSMEvents::OnShipLoad
        );
    }

    public void fireUnloadEvent(Long id){
        VManagerServer.manager().shipOf(id).ifPresent(
                VSMEvents::OnShipUnload
        );
    }

    public void tick() {
        lazyTick();
    }

}
