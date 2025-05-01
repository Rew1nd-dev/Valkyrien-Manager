package com.verr1.valkyrienmanager.manager.db.snapshot.item;

import com.verr1.valkyrienmanager.util.CompoundTagBuilder;
import com.verr1.valkyrienmanager.util.SerializeUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collection;
import java.util.HashSet;

public class SnapShotConfig{
    private static final SerializeUtils.Serializer<Collection<String>> b_serializer = SerializeUtils.ofCollection(SerializeUtils.STRING);
    private static final SerializeUtils.Serializer<ReplaceMode> m_serializer = SerializeUtils.ofEnum(ReplaceMode.class);
    private static final SerializeUtils.Serializer<PlaceMode> p_serializer = SerializeUtils.ofEnum(PlaceMode.class);


    public ReplaceMode replaceMode = ReplaceMode.NO_NEGLECTED;
    public PlaceMode placeMode = PlaceMode.NO_NEGLECTED;
    public final HashSet<String> replaceBlackList = new HashSet<>();
    public final HashSet<String> placeBlackList = new HashSet<>();


    public CompoundTag serialize(){
        return CompoundTagBuilder
                .create()
                .withCompound("neglect_mode", m_serializer.serialize(replaceMode))
                .withCompound("place_mode", p_serializer.serialize(placeMode))
                .withCompound("black_list", b_serializer.serialize(replaceBlackList))
                .withCompound("place_black_list", b_serializer.serialize(placeBlackList))
                .build();
    }

    public void deserialize(CompoundTag tag){
        replaceMode = m_serializer.deserialize(tag.getCompound("neglect_mode"));
        placeMode = p_serializer.deserialize(tag.getCompound("place_mode"));
        replaceBlackList.clear();
        replaceBlackList.addAll(b_serializer.deserialize(tag.getCompound("black_list")));
        placeBlackList.clear();
        placeBlackList.addAll(b_serializer.deserialize(tag.getCompound("place_black_list")));
    }

    public boolean shouldNeglect(BlockState currentState){ // return "should neglect"
        return switch (replaceMode) {
            case ALL -> false;
            case NO_NEGLECTED -> replaceBlackList.contains(currentState.getBlock().getDescriptionId());
            case AIR -> !currentState.isAir();
        };
    }

    public boolean shouldPlace(BlockState currentState){ // return "should place"
        return switch (placeMode) {
            case ALL -> false;
            case NO_NEGLECTED -> placeBlackList.contains(currentState.getBlock().getDescriptionId());
        };
    }

    public void neglect(Block block){
        if(block.getDescriptionId().equals(Blocks.AIR.getDescriptionId()))return;
        replaceBlackList.add(block.getDescriptionId());
    }

    public void forget(Block block){
        replaceBlackList.remove(block.getDescriptionId());
    }

    public void forbid(Block block){
        if(block.getDescriptionId().equals(Blocks.AIR.getDescriptionId()))return;
        placeBlackList.add(block.getDescriptionId());
    }

    public void allow(Block block){
        placeBlackList.remove(block.getDescriptionId());
    }





    public void neglect(String block){
        if(block.equals(Blocks.AIR.getDescriptionId()))return;
        replaceBlackList.add(block);
    }

    public void forget(String block){
        replaceBlackList.remove(block);
    }

    public void forbid(String block){
        if(block.equals(Blocks.AIR.getDescriptionId()))return;
        placeBlackList.add(block);
    }

    public void allow(String block){
        placeBlackList.remove(block);
    }




    public enum ReplaceMode{
        ALL,
        NO_NEGLECTED,
        AIR,
        ;
    }

    public enum PlaceMode{
        ALL,
        NO_NEGLECTED,
        ;
    }


}
