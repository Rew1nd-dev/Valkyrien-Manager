package com.verr1.valkyrienmanager.manager.events;

import com.verr1.valkyrienmanager.ValkyrienManager;
import com.verr1.valkyrienmanager.ValkyrienManagerServer;
import com.verr1.valkyrienmanager.manager.db.VDataExtension;
import com.verr1.valkyrienmanager.manager.db.VSMDataBase;
import net.minecraft.world.phys.Vec3;
import org.valkyrienskies.core.api.ships.Ship;

import static com.verr1.valkyrienmanager.foundation.utils.MinecraftServerUtils.getPlayerNearBy;
import static com.verr1.valkyrienmanager.foundation.utils.MinecraftServerUtils.getServerLevel;
import static org.valkyrienskies.mod.common.util.VectorConversionsMCKt.toMinecraft;

public class VSMEvents {




    public static void OnShipCreated(Ship ship){
        addShipDataExtension(ship);
    }

    public static void OnShipDeleted(Ship ship){
        removeShipDataExtension(ship);
    }

    private static void addShipDataExtension(Ship ship){
        String dimensionID = ship.getChunkClaimDimension();
        Vec3 shipPosition = toMinecraft(ship.getTransform().getPositionInWorld());
        getServerLevel(dimensionID)
            .map(lvl -> getPlayerNearBy(lvl, shipPosition, 16))
            .ifPresentOrElse(
                players -> {
                    VDataExtension extension = new VDataExtension
                        .builder()
                        .withID(ship.getId())
                        .withPlayersAroundOnCreation(players)
                        .withCreatedTimeStamp((long)ValkyrienManagerServer.manager().timer.getSeconds())
                        .build();

                    VSMDataBase.put(extension);
                },
                () -> ValkyrienManager.LOGGER.error("Error During Creating ShipDataExtension, Failed to get level for dimension: " + dimensionID)
            );
    }

    private static void removeShipDataExtension(Ship ship){
        VSMDataBase.remove(ship.getId());
    }

}
