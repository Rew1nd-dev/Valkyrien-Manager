package com.verr1.valkyrienmanager.mixin;


import com.verr1.valkyrienmanager.manager.events.VSMEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.core.apigame.world.IPlayer;
import org.valkyrienskies.core.apigame.world.chunks.ChunkWatchTasks;
import org.valkyrienskies.core.impl.chunk_tracking.h;
import org.valkyrienskies.core.impl.shadow.zB;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Mixin(h.class)
public class MixinShipUnload {


    @Inject(
            at = @At("HEAD"),
            method = "a(Ljava/util/Set;Ljava/util/Set;Ljava/lang/Iterable;Ljava/lang/Iterable;)Lorg/valkyrienskies/core/apigame/world/chunks/ChunkWatchTasks;",
            remap = false
    )
    public void onShipLoadStateChange(Set<? extends IPlayer> par1, Set<? extends IPlayer> par2, Iterable<? extends zB> par3, Iterable<? extends zB> unloadedShip, CallbackInfoReturnable<ChunkWatchTasks> cir) {

        unloadedShip.forEach(VSMEvents::OnShipUnload);

    }

}
