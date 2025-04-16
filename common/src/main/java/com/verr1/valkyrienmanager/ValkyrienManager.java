package com.verr1.valkyrienmanager;

import com.mojang.logging.LogUtils;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.*;
import org.slf4j.Logger;

import static com.verr1.valkyrienmanager.Debug.*;
import static org.valkyrienskies.mod.common.util.VectorConversionsMCKt.toJOML;

public final class ValkyrienManager {
    public static final String MOD_ID = "valkyrienmanager";

    public static final Logger LOGGER = LogUtils.getLogger();

    public static void init() {
        // Write common init code here.

        LifecycleEvent.SERVER_STARTED.register(server -> {
            ValkyrienManagerServer.INSTANCE = server;
            ValkyrienManagerServer.init(server);
        });

        InteractionEvent.RIGHT_CLICK_BLOCK.register(
            (player, hand, pos, direction) -> {
                onInteractDebugCluster(player, hand, pos, direction);
                onInteractTeleportCluster(player, hand, pos, direction);
                onInteractListShipDataExtension(player, hand, pos, direction);
                return EventResult.interruptDefault();
            }
        );

        TickEvent.SERVER_POST.register(server -> {
            ValkyrienManagerServer.INSTANCE = server;
            ValkyrienManagerServer.manager().tick();
        });

    }
}
