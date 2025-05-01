package com.verr1.valkyrienmanager.forge;

import com.verr1.valkyrienmanager.VManagerMod;
import com.verr1.valkyrienmanager.foundation.command.VMClientCommands;
import com.verr1.valkyrienmanager.foundation.command.VMServerCommands;
import dev.architectury.platform.forge.EventBuses;
import net.minecraft.commands.Commands;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static com.verr1.valkyrienmanager.VManagerMod.MOD_ID;

@Mod(MOD_ID)
@SuppressWarnings("removal")
public final class ValkyrienManagerModForge {
    public ValkyrienManagerModForge() {
        // Submit our event bus to let Architectury API register our content on the right time.
        // EventBuses.registerModEventBus(VManagerMod.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        // Run our common setup.
        EventBuses.registerModEventBus(MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        VManagerMod.init();

        //

        /*
        *
        * MinecraftForge.EVENT_BUS.<RegisterClientCommandsEvent>addListener(
                e -> VMClientCommands.registerClientCommands(e.getDispatcher())
        );

        MinecraftForge.EVENT_BUS.<RegisterCommandsEvent>addListener(
                e -> VMServerCommands.registerServerCommands(e.getDispatcher())
        );
        * */

    }
}
