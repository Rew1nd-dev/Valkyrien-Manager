package com.verr1.valkyrienmanager.vs.attachments;

import com.verr1.valkyrienmanager.VManagerServer;
import com.verr1.valkyrienmanager.manager.events.VSMEvents;
import org.jetbrains.annotations.NotNull;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.ShipForcesInducer;

import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("deprecated")
public class LazyTracker implements ShipForcesInducer {
    public static final int MAX_LIVE = 120; // 3 seconds
    public static ConcurrentHashMap<Long, Integer> TRACKED_ID_LIVE = new ConcurrentHashMap<>();

    public static void tick(){
        TRACKED_ID_LIVE.entrySet().forEach(
            entry -> {
                entry.setValue(entry.getValue() - 1);
                if (entry.getValue() <= 0) {
                    VManagerServer
                        .manager()
                        .shipOf(entry.getKey())
                        .filter(s -> !s.isStatic())
                        .ifPresent(
                            VSMEvents::OnShipUnload
                    );
                }
            }
        );

        TRACKED_ID_LIVE.entrySet().removeIf(
                entry -> entry.getValue() <= 0
        );

    }

    public static void alive(long id){
        // Be Aware That alive() gets called from phys thread, so schedule it to the next main tick
        if(!TRACKED_ID_LIVE.containsKey(id)){
            VManagerServer.SERVER_EXECUTOR.executeLater(
                    () -> VManagerServer.manager().shipOf(id).ifPresent(
                            VSMEvents::OnShipLoad
                    )
                    ,
                    0
            );
        }
        TRACKED_ID_LIVE.put(id, MAX_LIVE);
    }


    private static final int LAZY_TICK_RATE = 120; // 2 second
    private int counter = 0;

    public static LazyTracker getOrCreate(ServerShip ship){
        var obj = ship.getAttachment(LazyTracker.class);
        if(obj == null){
            obj = new LazyTracker();
            ship.saveAttachment(LazyTracker.class, obj);
        }
        return obj;
    }


    private void lazyTick(@NotNull PhysShip physShip){
        if(--counter > 0)return;
        counter = LAZY_TICK_RATE;
        alive(physShip.getId());
    }

    @Override
    public void applyForces(@NotNull PhysShip physShip) {
        // lazyTick(physShip);
    }
}
