package com.verr1.valkyrienmanager;

import com.verr1.valkyrienmanager.foundation.executor.Executor;
import com.verr1.valkyrienmanager.manager.VManager;
import com.verr1.valkyrienmanager.manager.db.general.VDataBase;
import com.verr1.valkyrienmanager.manager.db.snapshot.SnapshotDataBase;
import com.verr1.valkyrienmanager.manager.db.snapshot.item.VSnapshot;
import com.verr1.valkyrienmanager.vs.attachments.LazyTracker;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;
import org.valkyrienskies.core.impl.hooks.VSEvents;

import java.util.Optional;

public class VManagerServer {

    public static MinecraftServer INSTANCE;
    public static Executor SERVER_EXECUTOR = new Executor();

    private static VManager VS_MANAGER;
    public static VDataBase DATA_BASE = new VDataBase();
    public static SnapshotDataBase SNAP_DATA_BASE = new SnapshotDataBase();

    public static Optional<MinecraftServer> getServer(){
        return Optional.ofNullable(INSTANCE);
    }

    public static Optional<VManager> getManagerSafe(){
        return Optional.ofNullable(VS_MANAGER);
    }

    public static @NotNull VManager manager(){
        return getManagerSafe().orElseThrow(() -> new IllegalStateException("Valkyrien Skies Manager Is Not Present!"));
    }

    public static void init(MinecraftServer server){
        INSTANCE = server;
        VS_MANAGER = new VManager(server);
        DATA_BASE = VDataBase.load(server);
        SNAP_DATA_BASE = SnapshotDataBase.load(server);
        VSEvents.INSTANCE.getShipLoadEvent().on(
                e -> e.getShip().saveAttachment(LazyTracker.class, null)
        );



    }
}
