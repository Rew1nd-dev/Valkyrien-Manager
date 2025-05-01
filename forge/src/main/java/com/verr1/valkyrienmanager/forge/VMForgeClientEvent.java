package com.verr1.valkyrienmanager.forge;


import com.verr1.valkyrienmanager.VManagerMod;
import com.verr1.valkyrienmanager.foundation.command.VMClientCommands;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = VManagerMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class VMForgeClientEvent {

    @SubscribeEvent
    public static void onClientCommandRegister(RegisterClientCommandsEvent event) {
        VMClientCommands.registerClientCommands(event.getDispatcher());
    }
}
