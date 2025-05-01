package com.verr1.valkyrienmanager.registry;

import com.google.common.base.Suppliers;
import com.simibubi.create.AllBlocks;
import com.tterrag.registrate.fabric.RegistryObject;
import com.verr1.valkyrienmanager.content.blocks.ExplosionAlarmBlock;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;

import static com.verr1.valkyrienmanager.VManagerMod.MOD_ID;
import static com.verr1.valkyrienmanager.registry.VMItems.TAB;

public class VMBlocks {


    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(MOD_ID, Registries.BLOCK);


    public static final RegistrySupplier<ExplosionAlarmBlock> EXPLOSION_ALARM = BLOCKS.register(
            ExplosionAlarmBlock.ID,
            () -> new ExplosionAlarmBlock(BlockBehaviour.Properties.copy(Blocks.ANDESITE))
    );




    public static void register(){
        BLOCKS.register();
    }

    public static void registerItems(DeferredRegister<Item> itemRegister){

        for(RegistrySupplier<Block> block : BLOCKS){
            itemRegister.register(block.getId(), () -> new BlockItem(block.get(), new Item.Properties().arch$tab(TAB)));
        }


    }

}
