package com.verr1.valkyrienmanager.mixin;


import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.util.datastructures.DenseBlockPosSet;
import org.valkyrienskies.mod.common.assembly.ShipAssemblyKt;

import java.lang.annotation.Target;

import static com.verr1.valkyrienmanager.manager.events.VSMEvents.OnShipCreated;

@Mixin(ShipAssemblyKt.class)
public class MixinShipAssembly {


    @Inject(method = "createNewShipWithBlocks", at = @At("RETURN"))
    private static void addShipAssemblyContext(BlockPos centerBlock, DenseBlockPosSet blocks, ServerLevel level, CallbackInfoReturnable<ServerShip> cir){
        OnShipCreated(cir.getReturnValue());
    }

}
