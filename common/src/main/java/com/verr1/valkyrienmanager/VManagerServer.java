package com.verr1.valkyrienmanager;

import com.verr1.valkyrienmanager.manager.VManager;
import com.verr1.valkyrienmanager.manager.db.VDataBase;
import com.verr1.valkyrienmanager.manager.events.VSMEvents;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;
import org.valkyrienskies.core.impl.hooks.VSEvents;

import java.util.Optional;

public class VManagerServer {

    public static MinecraftServer INSTANCE;
    private static VManager VS_MANAGER;
    public static VDataBase DATA_BASE = new VDataBase();

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
        VS_MANAGER = new VManager(server);
        DATA_BASE = VDataBase.load(server);
        VSEvents.INSTANCE.getShipLoadEvent().on(
                e -> VSMEvents.OnShipLoad(e.getShip())
        );
    }
}
