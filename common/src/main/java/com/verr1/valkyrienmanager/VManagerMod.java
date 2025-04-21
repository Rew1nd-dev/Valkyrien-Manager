package com.verr1.valkyrienmanager;

import com.mojang.logging.LogUtils;
import com.verr1.valkyrienmanager.compact.VMCompact;
import com.verr1.valkyrienmanager.manager.VMCommands;
import com.verr1.valkyrienmanager.manager.vcommand.Commands;
import com.verr1.valkyrienmanager.registry.VMPackets;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.*;
import org.slf4j.Logger;

import static com.verr1.valkyrienmanager.Debug.*;
import static org.valkyrienskies.mod.common.util.VectorConversionsMCKt.toJOML;

public final class VManagerMod {
    public static final String MOD_ID = "valkyrienmanager";

    public static final Logger LOGGER = LogUtils.getLogger();

    public static void init() {
        // Write common init code here.

        LifecycleEvent.SERVER_STARTED.register(server -> {
            VManagerServer.INSTANCE = server;
            VManagerServer.init(server);
        });

        /*

        InteractionEvent.RIGHT_CLICK_BLOCK.register(
            (player, hand, pos, direction) -> {
                onInteractDebugCluster(player, hand, pos, direction);
                onInteractTeleportCluster(player, hand, pos, direction);
                onInteractListShipDataExtension(player, hand, pos, direction);
                onInteractTestDBSync(player, hand, pos, direction);
                onInteractTestGUI(player, hand, pos, direction);
                return EventResult.interruptDefault();
            }
        );

        * */

        TickEvent.SERVER_POST.register(server -> {
            VManagerServer.INSTANCE = server;
            VManagerServer.manager().tick();
        });

        CommandRegistrationEvent.EVENT.register(
                (dispatcher, registryAccess, selection) -> {
                    if(selection.includeDedicated){
                        VMCommands.registerServerCommands(dispatcher);
                    }
                    if (selection.includeIntegrated){
                        VMCommands.registerServerCommands(dispatcher);
                        VMCommands.registerClientCommands(dispatcher);
                    }
                }
        );

        VMPackets.register();

        // VManagerClient.init();
        // Commands.register();

        VMCompact.init();

    }
}
