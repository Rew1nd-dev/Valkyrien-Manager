package com.verr1.valkyrienmanager.manager.events;

import com.verr1.valkyrienmanager.VManagerMod;
import com.verr1.valkyrienmanager.VManagerServer;
import com.verr1.valkyrienmanager.manager.db.general.entry.PlayerSetEntry;
import com.verr1.valkyrienmanager.manager.db.general.item.NetworkKey;
import com.verr1.valkyrienmanager.manager.db.general.item.VItem;
import com.verr1.valkyrienmanager.manager.events.vevents.*;
import net.minecraft.world.phys.Vec3;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;

import java.util.HashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.verr1.valkyrienmanager.foundation.utils.MinecraftServerUtils.getPlayerNearBy;
import static com.verr1.valkyrienmanager.foundation.utils.MinecraftServerUtils.getServerLevel;
import static org.valkyrienskies.mod.common.util.VectorConversionsMCKt.toMinecraft;

public class VSMEvents {


    public static HashMap<String, Consumer<VCreateEvent>>   CREATE_EVENT_LISTENERS         = new HashMap<>();
    public static HashMap<String, Consumer<VRemoveEvent>>   REMOVE_EVENT_LISTENERS         = new HashMap<>();
    public static HashMap<String, Consumer<VLoadEvent>>     LOAD_EVENT_LISTENERS           = new HashMap<>();
    public static HashMap<String, Consumer<VUnloadEvent>>   UNLOAD_EVENT_LISTENERS         = new HashMap<>();
    public static HashMap<String, Consumer<VDetonateEvent>> DETONATE_ALARM_EVENT_LISTENERS = new HashMap<>();




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

    public static void OnShipUnload(ServerShip ship){
        UNLOAD_EVENT_LISTENERS.values().forEach(
            it -> it.accept(new VUnloadEvent(ship.getId()))
        );
    }

    public static void OnDetonateAlarm(VDetonateEvent event){
        DETONATE_ALARM_EVENT_LISTENERS.values().forEach(
                it -> it.accept(event)
        );
    }

    private static void addShipDataExtension(Ship ship){
        String dimensionID = ship.getChunkClaimDimension();
        Vec3 shipPosition = toMinecraft(ship.getTransform().getPositionInWorld());
        // currently just make the closest player to the set
        getServerLevel(dimensionID)
            .map(lvl -> getPlayerNearBy(lvl, shipPosition, 16))
            .map(p -> p
                    .stream()
                    .sorted(
                    (p1, p2) -> {
                        double d1 = p1.distanceToSqr(shipPosition);
                        double d2 = p2.distanceToSqr(shipPosition);
                        return Double.compare(d1, d2);
                    }
            ).limit(1).collect(Collectors.toSet()))
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


}
