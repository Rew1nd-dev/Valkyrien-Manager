package com.verr1.valkyrienmanager;

import com.mojang.logging.LogUtils;
import com.verr1.valkyrienmanager.compact.VMCompact;
import com.verr1.valkyrienmanager.content.blocks.ExplosionAlarmBlock;
import com.verr1.valkyrienmanager.registry.VMBlocks;
import com.verr1.valkyrienmanager.registry.VMItems;
import com.verr1.valkyrienmanager.registry.VMPackets;
import com.verr1.valkyrienmanager.vs.attachments.LazyTracker;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.event.events.common.*;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.slf4j.Logger;


public final class VManagerMod {
    public static final String MOD_ID = "valkyrienmanager";

    public static final Logger LOGGER = LogUtils.getLogger();


    public static void init() {
        // Write common init code here.

        LifecycleEvent.SERVER_STARTED.register(VManagerServer::init);


        TickEvent.SERVER_POST.register(server -> {
            VManagerServer.manager().tick();
            VManagerServer.SERVER_EXECUTOR.tick();
        });

        EnvExecutor.runInEnv(Env.CLIENT, () -> () -> {

            ClientTickEvent.CLIENT_POST.register(
                    client -> {
                        VManagerClient.CLIENT_EXECUTOR.tick();
                    }
            );




        });

        ExplosionEvent.DETONATE.register((world, explosion, entities) -> {
            ExplosionAlarmBlock.onExplosionDenote(world, explosion, entities);
        });



        VMBlocks.register();
        VMItems.register();

        VMPackets.register();
        VMCompact.init();

    }
}
