package com.verr1.valkyrienmanager.content.blocks;

import com.verr1.valkyrienmanager.VManagerMod;
import com.verr1.valkyrienmanager.VManagerServer;
import com.verr1.valkyrienmanager.foundation.data.WorldBlockPos;
import com.verr1.valkyrienmanager.manager.events.VSMEvents;
import com.verr1.valkyrienmanager.manager.events.vevents.VDetonateEvent;
import com.verr1.valkyrienmanager.registry.VMBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.ServerShip;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.valkyrienskies.mod.common.util.VectorConversionsMCKt.toJOML;

public class ExplosionAlarmBlock extends Block {

    public static final String ID = "explosion_alarm";

    public static void onExplosionDenote(Level world, Explosion explosion, List<Entity> entities){
        List<BlockPos> affectedBlocks = explosion.getToBlow();
        List<BlockPos> toRemove = new ArrayList<>();
        for (BlockPos pos : affectedBlocks) {
            BlockState state = world.getBlockState(pos);
            if (state.getBlock() == VMBlocks.EXPLOSION_ALARM.get()) {
                ((ExplosionAlarmBlock)state.getBlock())

                        .onExplosionHit(world, explosion, entities, pos); // maybe this can be static


                toRemove.add(pos);
            }
        }


        affectedBlocks.removeAll(toRemove);

    }


    public ExplosionAlarmBlock(Properties properties) {
        super(properties);
    }

    public void onExplosionHit(@NotNull Level world, Explosion explosion, List<Entity> entities, BlockPos pos){
        if(world.isClientSide)return;

        VManagerMod.LOGGER.info("Explosion alarm triggered at: " + pos);

        Vector3d wsc = toJOML(pos.getCenter());
        Optional<ServerShip> shipOptional = VManagerServer.manager().shipAt(WorldBlockPos.of(world, pos));

        VSMEvents.OnDetonateAlarm(new VDetonateEvent(
                explosion,
                entities,
                shipOptional
                        .map(s -> s.getTransform().getShipToWorld().transformPosition(wsc))
                        .orElse(wsc),
                pos,
                shipOptional
        ));

    }
}
