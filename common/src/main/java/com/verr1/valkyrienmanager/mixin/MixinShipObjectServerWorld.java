package com.verr1.valkyrienmanager.mixin;

import org.joml.Vector3ic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.impl.game.ships.ShipObjectServerWorld;

import static com.verr1.valkyrienmanager.manager.events.VSMEvents.OnShipCreated;
import static com.verr1.valkyrienmanager.manager.events.VSMEvents.OnShipDeleted;

@Mixin(ShipObjectServerWorld.class)
public class MixinShipObjectServerWorld {

    @Inject(method = "createNewShipAtBlock(Lorg/joml/Vector3ic;ZDLjava/lang/String;)Lorg/valkyrienskies/core/api/ships/ServerShip;", at = @At("RETURN"), remap = false)
    void onShipCreated(
            Vector3ic blockPosInWorldCoordinates,
            boolean createShipObjectImmediately,
            double scaling,
            String dimensionId,
            CallbackInfoReturnable<ServerShip> cir
    ){
        OnShipCreated(cir.getReturnValue());
    }

    @Inject(method = "deleteShip(Lorg/valkyrienskies/core/api/ships/ServerShip;)V", at = @At("RETURN"), remap = false)
    void onShipDeleted(ServerShip ship, CallbackInfo ci){
        OnShipDeleted(ship);
    }




}
