package com.verr1.valkyrienmanager.foundation.data;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public record WorldBlockPos(GlobalPos globalPos) {
    public static WorldBlockPos of(Level level, BlockPos pos){
        return new WorldBlockPos(GlobalPos.of(level.dimension(), pos));
    }

    public @Nullable ServerLevel level(MinecraftServer server){
        return server.getLevel(globalPos.dimension());
    }


    public BlockPos pos(){
        return globalPos.pos();
    }
}

