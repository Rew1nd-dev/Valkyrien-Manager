package com.verr1.valkyrienmanager.manager;

import com.verr1.valkyrienmanager.VManagerMod;
import com.verr1.valkyrienmanager.VManagerServer;
import com.verr1.valkyrienmanager.foundation.data.PhysicalCluster;
import com.verr1.valkyrienmanager.foundation.data.VOwnerData;
import com.verr1.valkyrienmanager.foundation.data.Timer;
import com.verr1.valkyrienmanager.foundation.data.WorldBlockPos;
import com.verr1.valkyrienmanager.manager.db.general.VDataBase;
import com.verr1.valkyrienmanager.manager.db.general.item.NetworkKey;
import com.verr1.valkyrienmanager.manager.db.general.item.VItem;
import com.verr1.valkyrienmanager.manager.db.snapshot.SnapshotDataBase;
import com.verr1.valkyrienmanager.manager.db.snapshot.item.VSnapshot;
import com.verr1.valkyrienmanager.mixin.accessor.ShipObjectServerWorldAccessor;
import com.verr1.valkyrienmanager.vs.VSTracker;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.apigame.constraints.VSConstraint;
import org.valkyrienskies.core.apigame.world.ServerShipWorldCore;
import org.valkyrienskies.core.impl.game.ShipTeleportDataImpl;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.*;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class VManager {
    public static final long INVALID_ID = -1L;
    public static final long REQUEST_ALL_ID = -114514L;

    private final MinecraftServer server;
    private final ServerShipWorldCore vs_world;

    private final VSTracker tracker = new VSTracker();
    public Timer timer = new Timer();

    private VDataBase db(){
        return VManagerServer.DATA_BASE;
    }

    private SnapshotDataBase sdb(){return VManagerServer.SNAP_DATA_BASE;}

    public VManager(MinecraftServer server){
        this.server = server;
        vs_world = VSGameUtilsKt.getShipObjectWorld(server);
    }


    public @NotNull List<ServerShip> allShips(){
        return vs_world.getAllShips().stream().toList();
    }

    public @NotNull List<LoadedServerShip> allLoadedShips(){
        return vs_world.getLoadedShips().stream().toList();
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
            if(max_depth-- == 1){
                VManagerMod.LOGGER.warn("Cluster search depth exceeded !");
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
        clusterOf(id)
                .ids
                .stream()
                .map(tid -> db().data().get(tid))
                .filter(Objects::nonNull)
                .map(vItem -> vItem.get(NetworkKey.OWNER).get().raw)
                .forEach(
                        r -> r.add(VOwnerData.of(owner))
                );

    }

    public Optional<ServerLevel> levelOf(long id){
        return Optional.ofNullable(VSGameUtilsKt.getLevelFromDimensionId(VManagerServer.INSTANCE, dimensionOf(id)));
    }

    public void snap(long id){
        var levelOpt = levelOf(id);
        if(levelOpt.isEmpty())return;
        shipOf(id).ifPresent(
                s -> sdb().put(id, VSnapshot.create(s, levelOpt.get()))
        );
    }

    public void repair(long id){
        var levelOpt = levelOf(id);
        if(levelOpt.isEmpty())return;
        Optional.ofNullable(sdb().get(id)).ifPresent(
                s -> s.repair(levelOpt.get())
        );
    }

    public void abandonCluster(Player owner, long id){
        clusterOf(id)
                .ids
                .stream()
                .map(tid -> db().data().get(tid))
                .filter(Objects::nonNull)
                .map(vItem -> vItem.get(NetworkKey.OWNER).get().raw)
                .forEach(
                        r -> r.remove(VOwnerData.of(owner))
                );
    }

    public void createForAbsence(){
        allShips()
                .stream()
                .map(Ship::getId)
                .filter(id -> db().getOptional(id).isEmpty())
                .forEach(
                        id -> db().put(id, VItem.createEmpty(id))
                );
    }

    public void dropInvalid(){
        Set<Long> present = allShips().stream().map(Ship::getId).collect(Collectors.toSet());
        db().data().keySet().stream().filter(present::contains).forEach(i -> db().remove(i));
    }

    public long pick(@NotNull ServerPlayer player){
        HitResult hit = player.pick(10, 1, false);
        if(hit instanceof BlockHitResult blockHitResult){
            WorldBlockPos pos = WorldBlockPos.of(player.level(), blockHitResult.getBlockPos());
            return shipAt(pos).map(ServerShip::getId).orElse(INVALID_ID);
        }
        return INVALID_ID;
    }

    public @NotNull BlockState pickBlock(@NotNull ServerPlayer player){
        HitResult hit = player.pick(10, 1, false);
        if(hit instanceof BlockHitResult blockHitResult){
            return player.level().getBlockState(blockHitResult.getBlockPos());
        }
        return Blocks.AIR.defaultBlockState();
    }




    public void tick(){
        timer.tick();
        tracker.tick();
    }

}
