package com.verr1.valkyrienmanager.forge;

import com.verr1.valkyrienmanager.VManagerMod;
import com.verr1.valkyrienmanager.manager.VMCommands;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(VManagerMod.MOD_ID)
@SuppressWarnings("removal")
public final class ValkyrienManagerModForge {
    public ValkyrienManagerModForge() {
        // Submit our event bus to let Architectury API register our content on the right time.
        // EventBuses.registerModEventBus(VManagerMod.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        // Run our common setup.
        VManagerMod.init();
        // MinecraftForge.EVENT_BUS.<RegisterCommandsEvent>addListener(e -> VMCommands.registerServerCommands(e.getDispatcher()));

    }
}
