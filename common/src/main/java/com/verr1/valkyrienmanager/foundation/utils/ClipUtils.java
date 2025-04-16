package com.verr1.valkyrienmanager.foundation.utils;


import com.verr1.valkyrienmanager.foundation.data.ShipHitResult;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.valkyrienskies.core.api.ships.LoadedShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class ClipUtils {

    public static @Nullable EntityHitResult clipEntity(
            Vec3 from,
            Vec3 to,
            AABB aabb,
            double coneRatio,
            Level level,
            Predicate<Entity> filter
    ){
        double maxDistance = to.subtract(from).lengthSqr();
        List<Entity> list = level.getEntities(null, aabb).stream().filter(filter).toList();
        for(var candidate : list){
            double d = from.distanceTo(new Vec3(candidate.getX(), candidate.getY(), candidate.getZ()));
            Optional<Vec3> clip0 = candidate.getBoundingBox().inflate(Math.min(d, 1) * coneRatio).clip(from, to);
            if(clip0.isEmpty())continue;
            Vec3 clip = clip0.get();
            if(clip.distanceTo(from) > maxDistance)continue;
            return new EntityHitResult(candidate, clip);
        }
        return null;
    }


    public static @Nullable EntityHitResult clipEntityInView(
            Vec3 from,
            Vec3 to,
            AABB aabb,
            double coneAngle,
            Level level,
            Predicate<Entity> filter
    ){
        double maxDistance = to.subtract(from).lengthSqr();
        Vec3 r_v = to.subtract(from);
        Function<Entity, Double> calcAngle = e -> {
            Vec3 r_e = e.position().subtract(from);
            return Math.acos((r_e.normalize()).dot(r_v.normalize()));
        };
        Entity result = level
                        .getEntities(null, aabb)
                        .stream()
                        .filter(filter)
                        .filter(e -> calcAngle.apply(e) < coneAngle)
                        .min((a, b) -> {
                            double d_a = a.position().distanceTo(from);
                            // double a_a = calcAngle.apply(a) / coneAngle;
                            double d_b = b.position().distanceTo(from);
                            // double a_b = calcAngle.apply(b) / coneAngle;
                            return Double.compare(d_a , d_b);
                        }).orElse(null);
        if(result == null)return null;
        return new EntityHitResult(result, result.position());
    }

    public static @NotNull List<Entity> clipAllEntityInView(
            Vec3 from,
            Vec3 to,
            AABB aabb,
            double coneAngle,
            Level level,
            Predicate<Entity> filter
    ){
        double maxDistance = to.subtract(from).lengthSqr();
        return level
                .getEntities(null, aabb)
                .stream()
                .filter(filter)
                .filter(e -> {
                    Vec3 r_e = e.position().subtract(from);
                    Vec3 r_v = to.subtract(from);
                    double acos = Math.acos((r_e.normalize()).dot(r_v.normalize()));
                    return acos < coneAngle;
                }).toList();
    }


    public static @Nullable ShipHitResult clipShip(
            Vec3 from,
            Vec3 to,
            AABB aabb,
            double coneRatio,
            Level level,
            Predicate<Ship> filter
    ){
        double maxDistance = to.subtract(from).lengthSqr();
        List<LoadedShip> list = VSGameUtilsKt
                .getShipObjectWorld(level)
                .getLoadedShips()
                .stream()
                .filter(filter)
                .filter(s -> s.getWorldAABB().intersectsAABB(VectorConversionsMCKt.toJOML(aabb)))
                .toList();

        for(var candidate : list){
            double d = from.distanceTo(VectorConversionsMCKt.toMinecraft(candidate.getTransform().getPositionInWorld()));
            Optional<Vec3> clip0 = VectorConversionsMCKt.toMinecraft(candidate.getWorldAABB()).inflate(Math.min(d, 1) * coneRatio).clip(from, to);
            if(clip0.isEmpty())continue;
            Vec3 clip = clip0.get();
            if(clip.distanceTo(from) > maxDistance)continue;
            return new ShipHitResult(clip, candidate);
        }
        return null;
    }

    public static @Nullable EntityHitResult clipClientPlayer(
            Vec3 from,
            Vec3 to,
            AABB aabb,
            double coneRatio,
            ClientLevel level,
            Predicate<AbstractClientPlayer> filter
    ){
        double maxDistance = to.subtract(from).lengthSqr();
        List<AbstractClientPlayer> list = level.players();
        // Not implemented
        return null;
    }

    public static @Nullable EntityHitResult clipServerPlayer(
            Vec3 from,
            Vec3 to,
            AABB aabb,
            double coneRatio,
            ServerLevel level,
            Predicate<ServerPlayer> filter
    ){
        double maxDistance = to.subtract(from).lengthSqr();
        List<ServerPlayer> list = level
                .players()
                .stream()
                .filter(filter)
                .filter(p -> p.getBoundingBox().intersects(aabb))
                .toList();
        for(var candidate : list){
            double d = from.distanceTo(new Vec3(candidate.getX(), candidate.getY(), candidate.getZ()));
            Optional<Vec3> clip0 = candidate.getBoundingBox().inflate(Math.min(d, 1) * coneRatio).clip(from, to);
            if(clip0.isEmpty())continue;
            Vec3 clip = clip0.get();
            if(clip.distanceTo(from) > maxDistance)continue;
            return new EntityHitResult(candidate, clip);
        }
        return null;
    }


}
