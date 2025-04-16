package com.verr1.valkyrienmanager.foundation.utils;

import com.verr1.valkyrienmanager.ValkyrienManager;
import com.verr1.valkyrienmanager.ValkyrienManagerServer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class MinecraftServerUtils {

    public static @NotNull List<ServerPlayer> getPlayerNearBy(ServerLevel level, Vec3 pos, double radius){
        return level.getPlayers(player -> player.distanceToSqr(pos) < radius * radius);
    }


    public static Optional<ServerLevel> getServerLevel(String dimensionID){
        return ValkyrienManagerServer.getServer().map(
                server -> server.getLevel(VSGameUtilsKt.getResourceKey(dimensionID))
        );
    }
}
