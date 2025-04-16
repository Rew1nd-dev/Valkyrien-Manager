package com.verr1.valkyrienmanager;

import com.verr1.valkyrienmanager.foundation.data.PhysicalCluster;
import com.verr1.valkyrienmanager.foundation.data.WorldBlockPos;
import com.verr1.valkyrienmanager.manager.VManager;
import com.verr1.valkyrienmanager.manager.db.VSMDataBase;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaterniond;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.Ship;

import static org.valkyrienskies.mod.common.util.VectorConversionsMCKt.toJOML;

public class Debug {

    public static boolean DEBUG_ENABLE_CUSTOM_TELEPORT = true;


    public static void onInteractDebugCluster(Player player, InteractionHand hand, BlockPos pos, Direction direction){
        if(player.isLocalPlayer())return ;
        if(hand.equals(InteractionHand.OFF_HAND))return;
        if(!player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty())return;
        VManager manager = ValkyrienManagerServer.manager();

        manager.shipAt(WorldBlockPos.of(player.level(), pos))
                .map(Ship::getId)
                .map(manager::clusterOf)
                .map(PhysicalCluster::toString)
                .map(Component::literal)
                .ifPresent(
                        player::sendSystemMessage
                );
    }

    public static void onInteractTeleportCluster(Player player, InteractionHand hand, BlockPos pos, Direction direction){
        if(player.isLocalPlayer())return ;
        if(hand.equals(InteractionHand.OFF_HAND))return;
        if(!player.getItemInHand(InteractionHand.MAIN_HAND).is(Items.STICK))return;
        VManager manager = ValkyrienManagerServer.manager();

        Vector3dc p = toJOML(pos.getCenter().add(new Vec3(4, 4, 4)));
        ValkyrienManagerServer.manager()
                .allShips().stream().findAny()
                .ifPresent(
                        ship -> ValkyrienManagerServer.manager().teleportWithCluster(ship.getId(), p, new Quaterniond())
                );
    }


    public static void onInteractListShipDataExtension(Player player, InteractionHand hand, BlockPos pos, Direction direction){
        if(player.isLocalPlayer())return ;
        if(hand.equals(InteractionHand.OFF_HAND))return;
        if(!player.getItemInHand(InteractionHand.MAIN_HAND).is(Items.WOODEN_SWORD))return;

        ValkyrienManagerServer.manager().shipAt(WorldBlockPos.of(player.level(), pos))
                .map(Ship::getId)
                .map(VSMDataBase::get)
                .ifPresent(
                    d -> ValkyrienManagerServer.getServer().ifPresent(
                        server -> d.toComponent().forEach(
                                player::sendSystemMessage
                        )
                    )
                );


    }

}
