package com.verr1.valkyrienmanager.manager.events;

import com.verr1.valkyrienmanager.VManagerMod;
import com.verr1.valkyrienmanager.VManagerServer;
import com.verr1.valkyrienmanager.manager.db.entry.PlayerSetEntry;
import com.verr1.valkyrienmanager.manager.db.item.NetworkKey;
import com.verr1.valkyrienmanager.manager.db.item.VItem;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;

import java.util.HashMap;
import java.util.function.Consumer;

import static com.verr1.valkyrienmanager.foundation.utils.MinecraftServerUtils.getPlayerNearBy;
import static com.verr1.valkyrienmanager.foundation.utils.MinecraftServerUtils.getServerLevel;
import static org.valkyrienskies.mod.common.util.VectorConversionsMCKt.toMinecraft;

public class VSMEvents {


    public static HashMap<String, Consumer<VCreateEvent>> CREATE_EVENT_LISTENERS = new HashMap<>();
    public static HashMap<String, Consumer<VRemoveEvent>> REMOVE_EVENT_LISTENERS = new HashMap<>();
    public static HashMap<String, Consumer<VLoadEvent>> LOAD_EVENT_LISTENERS = new HashMap<>();

    public static void OnShipCreated(ServerShip ship){
        addShipDataExtension(ship);
        CREATE_EVENT_LISTENERS.values().forEach(
                it -> it.accept(new VCreateEvent(ship.getId()))
        );
    }

    public static void OnShipDeleted(ServerShip ship){
        REMOVE_EVENT_LISTENERS.values().forEach(
            it -> it.accept(new VRemoveEvent(ship.getId(), VManagerServer.DATA_BASE.getOptional(ship.getId()).orElse(null)))
        );
        removeShipDataExtension(ship);
    }

    public static void OnShipLoad(ServerShip ship){
        LOAD_EVENT_LISTENERS.values().forEach(
            it -> it.accept(new VLoadEvent(ship.getId()))
        );
    }

    private static void addShipDataExtension(Ship ship){
        String dimensionID = ship.getChunkClaimDimension();
        Vec3 shipPosition = toMinecraft(ship.getTransform().getPositionInWorld());
        getServerLevel(dimensionID)
            .map(lvl -> getPlayerNearBy(lvl, shipPosition, 16))
            .ifPresentOrElse(
                players -> {
                    long id = ship.getId();
                    VItem item = new VItem.builder(id).with(NetworkKey.BORN_AROUND, PlayerSetEntry.Wrapper.of(players)).build();
                    VManagerServer.DATA_BASE.put(id, item);
                },
                () -> VManagerMod.LOGGER.error("Error During Creating ShipDataExtension, Failed to get level for dimension: " + dimensionID)
            );
    }

    private static void removeShipDataExtension(Ship ship){
        VManagerServer.DATA_BASE.remove(ship.getId());
    }



    public record VCreateEvent(Long id){

    }

    public record VRemoveEvent(Long id, @Nullable VItem data){

    }

    public record VLoadEvent(Long id){

    }
}
