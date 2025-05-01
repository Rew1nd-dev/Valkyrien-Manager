package com.verr1.valkyrienmanager.manager.events.vevents;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.ServerShip;

import java.util.List;
import java.util.Optional;

public record VDetonateEvent(
        Explosion explosion,
        List<Entity> entities,
        Vector3d worldPosition,
        BlockPos pos,
        Optional<ServerShip> alarmOnShip
) {
}
