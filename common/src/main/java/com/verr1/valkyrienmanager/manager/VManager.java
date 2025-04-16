package com.verr1.valkyrienmanager.manager;

import com.verr1.valkyrienmanager.ValkyrienManager;
import com.verr1.valkyrienmanager.foundation.data.PhysicalCluster;
import com.verr1.valkyrienmanager.foundation.data.ShipOwnerData;
import com.verr1.valkyrienmanager.foundation.data.Timer;
import com.verr1.valkyrienmanager.foundation.data.WorldBlockPos;
import com.verr1.valkyrienmanager.manager.db.VSMDataBase;
import com.verr1.valkyrienmanager.mixin.accessor.ShipObjectServerWorldAccessor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.apigame.constraints.VSConstraint;
import org.valkyrienskies.core.apigame.world.ServerShipWorldCore;
import org.valkyrienskies.core.impl.game.ShipTeleportDataImpl;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.*;
import java.util.List;
import java.util.function.Predicate;

public class VManager {
    public static final long INVALID_ID = -1L;

    private final MinecraftServer server;
    private final ServerShipWorldCore vs_world;

    public Timer timer = new Timer();



    public VManager(MinecraftServer server){
        this.server = server;
        vs_world = VSGameUtilsKt.getShipObjectWorld(server);
    }


    public @NotNull List<ServerShip> allShips(){
        return vs_world.getAllShips().stream().toList();
    }

    public Optional<ServerShip> shipOf(long id){
        return Optional.ofNullable(vs_world.getAllShips().getById(id));
    }

    private Optional<ShipObjectServerWorldAccessor> safeCast(){
        return Optional
                .ofNullable(vs_world)
                //.filter(ShipObjectServerWorldAccessor.class::isInstance)
                .map(ShipObjectServerWorldAccessor.class::cast);
    }


    private Optional<ShipObjectServerWorldAccessor> safeCast(String dimensionID){
        return Optional
                .ofNullable(vs_world)
                .filter(ShipObjectServerWorldAccessor.class::isInstance)
                .map(ShipObjectServerWorldAccessor.class::cast);
    }

    public List<VSConstraint> constraintsOf(long id, Predicate<VSConstraint> filter){
        return
            safeCast()
            .map(
                accessor -> {
                    var constraints = accessor.controlCraft$getConstraints();
                    var shipIdToConstraints = accessor.controlCraft$getShipIdToConstraints();
                    return Optional.ofNullable(shipIdToConstraints.get(id))
                            .map(
                            set -> set
                                    .stream()
                                    .map(constraints::get)
                                    .filter(filter)
                                    .toList()
                            ).orElse(List.of());

            })
            .orElse(List.of());
    }

    public List<Long> connectedOf(long id, Predicate<Long> filter){
        return constraintsOf(id, constraint -> true)
                .stream()
                .map(
                    constraint -> {
                        long id_0 = constraint.getShipId0();
                        long id_1 = constraint.getShipId1();
                        return id == id_0 ? id_1 : id_0;
                    })
                .filter(filter)
                .distinct()
                .toList();
    }

    public boolean connectedToGround(long id){
        return connectedOf(id, id_ -> Objects.equals(id_, groundIdOf(dimensionOf(id_)))).isEmpty();
    }

    public long groundIdOf(String dimension){
        return vs_world.getDimensionToGroundBodyIdImmutable().getOrDefault(dimension, INVALID_ID);
    }

    public @NotNull String dimensionOf(long id){
        return shipOf(id).map(ServerShip::getChunkClaimDimension).orElse("null");
    }

    public @NotNull PhysicalCluster clusterOf(long id){
        if(shipOf(id).isEmpty())return PhysicalCluster.EMPTY;

        int max_depth = 1024;
        Long GROUND_BODY_ID = vs_world.getDimensionToGroundBodyIdImmutable().get(dimensionOf(id));
        HashSet<Long> clusterSet = new HashSet<>();
        Queue<Long> unvisited = new ArrayDeque<>(List.of(id));
        while (!unvisited.isEmpty() && max_depth > 0){
            long current = unvisited.poll();
            clusterSet.add(current);
            connectedOf(
                        current,
                        id_ -> !clusterSet.contains(id_) && !Objects.equals(id_, GROUND_BODY_ID)
                    )
                    .forEach(unvisited::offer);
            if(max_depth-- <= 1){
                ValkyrienManager.LOGGER.warn("Cluster search depth exceeded !");
            }
        }
        return new PhysicalCluster().addAll(clusterSet);
    }


    public Optional<ServerShip> shipAt(WorldBlockPos pos){
        return Optional
                .ofNullable(
                        VSGameUtilsKt
                                .getShipManagingPos(
                                        pos.level(server),
                                        pos.pos()
                                )
                );
    }

    public void teleportWithCluster(long id, Vector3dc n_pos, Quaterniondc n_rot){
        shipOf(id).ifPresent(
            ship -> {
                PhysicalCluster cluster = clusterOf(id);
                Vector3dc o_pos = ship.getTransform().getPositionInWorld();
                Quaterniondc o_rot = ship.getTransform().getShipToWorldRotation();
                cluster.ids.forEach(
                    c ->{
                        ServerShip s = shipOf(c).orElse(null);
                        if(s == null)return;
                        Vector3dc s_o_p = s.getTransform().getPositionInWorld();
                        Vector3dc s_r = s_o_p.sub(o_pos, new Vector3d());
                        Quaterniondc s_o_q = s.getTransform().getShipToWorldRotation();
                        Vector3dc s_n_r = n_rot
                                .transform(
                                        o_rot.conjugate(new Quaterniond())
                                .transform(
                                        s_r,
                                        new Vector3d()
                                ));
                        Vector3dc s_n_p = n_pos.add(s_n_r, new Vector3d());
                        Quaterniondc s_n_q = o_rot.conjugate(new Quaterniond()).mul(s_o_q, new Quaterniond()).mul(n_rot);
                        VSGameUtilsKt.getVsCore().teleportShip(
                                vs_world,
                                s,
                                new ShipTeleportDataImpl(
                                        s_n_p,
                                        s_n_q,
                                        new Vector3d(),
                                        new Vector3d(),
                                        s.getChunkClaimDimension(),
                                        s.getTransform().getShipToWorldScaling().get(0)
                                )
                        );
                    }
                );

            }
        );

    }

    public void ownCluster(Player owner, long id){
        clusterOf(id).ids.stream().map(VSMDataBase::get).forEach(vData -> {
            vData.getPlayersAroundOnCreation().clear();
            vData.getPlayersAroundOnCreation().add(ShipOwnerData.of(owner));
        });
    }

    public long pick(ServerPlayer player){
        HitResult hit = player.pick(10, 1, false);
        if(hit instanceof BlockHitResult blockHitResult){
            WorldBlockPos pos = WorldBlockPos.of(player.level(), blockHitResult.getBlockPos());
            return shipAt(pos).map(ServerShip::getId).orElse(INVALID_ID);
        }
        return INVALID_ID;
    }


    public void tick(){
        timer.tick();
    }

}
