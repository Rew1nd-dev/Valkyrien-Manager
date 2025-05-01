package com.verr1.valkyrienmanager.forge;


import com.verr1.valkyrienmanager.VManagerMod;
import com.verr1.valkyrienmanager.foundation.command.VMClientCommands;
import com.verr1.valkyrienmanager.foundation.command.VMServerCommands;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = VManagerMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class VMForgeServerEvent {

    @SubscribeEvent
    public static void onServerCommandRegister(RegisterCommandsEvent event) {
        VMServerCommands.registerServerCommands(event.getDispatcher());
    }
}
