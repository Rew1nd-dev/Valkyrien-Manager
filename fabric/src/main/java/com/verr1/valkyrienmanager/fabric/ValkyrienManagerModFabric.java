package com.verr1.valkyrienmanager.fabric;

import com.verr1.valkyrienmanager.manager.VMCommands;
import net.fabricmc.api.ModInitializer;

import com.verr1.valkyrienmanager.VManagerMod;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public final class ValkyrienManagerModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        VManagerMod.init();

        // CommandRegistrationCallback.EVENT.register((dispatcher, access, env) -> VMCommands.registerServerCommands(dispatcher));
    }
}
