package com.verr1.valkyrienmanager.registry;

import com.verr1.valkyrienmanager.VManagerMod;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

import static com.verr1.valkyrienmanager.VManagerMod.MOD_ID;

public class VMItems {

    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(MOD_ID, Registries.CREATIVE_MODE_TAB);

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(MOD_ID, Registries.ITEM);

    public static final RegistrySupplier<CreativeModeTab> TAB = TABS.register(
            "vm_main",
            () -> CreativeTabRegistry.create(
                    Component.translatable("vm_main"),
                    () -> VMBlocks.EXPLOSION_ALARM.get().asItem().getDefaultInstance()
    ));

    public static void register(){
        VMBlocks.registerItems(ITEMS);
        ITEMS.register();
        TABS.register();
    }

}
