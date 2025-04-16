package com.verr1.valkyrienmanager;

import com.verr1.valkyrienmanager.foundation.executor.DeferralExecutor;
import com.verr1.valkyrienmanager.foundation.executor.IntervalExecutor;
import com.verr1.valkyrienmanager.manager.VManager;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ValkyrienManagerServer {

    public static MinecraftServer INSTANCE;
    private static VManager VS_MANAGER;
    public static final DeferralExecutor SERVER_DEFERRAL_EXECUTOR = new DeferralExecutor();
    public static final IntervalExecutor SERVER_INTERVAL_EXECUTOR = new IntervalExecutor();

    public static Optional<MinecraftServer> getServer(){
        return Optional.ofNullable(INSTANCE);
    }

    public static Optional<VManager> getManagerSafe(){
        return Optional.ofNullable(VS_MANAGER);
    }

    public static @NotNull VManager manager(){
        return getManagerSafe().orElseThrow(() -> new IllegalStateException("Valkyrien Skies Manager is not initialized!"));
    }

    public static void init(MinecraftServer server){
        VS_MANAGER = new VManager(server);
    }
}
