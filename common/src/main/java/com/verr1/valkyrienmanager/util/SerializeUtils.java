package com.verr1.valkyrienmanager.util;

import com.verr1.valkyrienmanager.VManagerMod;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.primitives.AABBi;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collector;

public class SerializeUtils {
    public static HashMap<Class<?>, Serializer<?>> EnumSerializerCache = new HashMap<>();

    public static Serializer<Double> DOUBLE = of(SerializeUtils::ofDouble, tag -> tag.getDouble("value"));
    public static Serializer<Float> FLOAT = of(SerializeUtils::ofFloat, tag -> tag.getFloat("value"));
    public static Serializer<Integer> INT = of(SerializeUtils::ofInt, tag -> tag.getInt("value"));
    public static Serializer<Long> LONG = of(SerializeUtils::ofLong, tag -> tag.getLong("value"));
    public static Serializer<Boolean> BOOLEAN = of(SerializeUtils::ofBoolean, tag -> tag.getBoolean("value"));
    public static Serializer<String> STRING = of(SerializeUtils::ofString, tag -> tag.getString("value"));
    public static Serializer<CompoundTag> UNIT = of(tag -> tag, tag -> tag);



    public static Serializer<Vector3dc> VECTOR3DC = of(
            vec -> {
                CompoundTag tag = new CompoundTag();
                tag.putDouble("x", vec.x());
                tag.putDouble("y", vec.y());
                tag.putDouble("z", vec.z());
                return tag;
            },
            tag -> new Vector3d(tag.getDouble("x"), tag.getDouble("y"), tag.getDouble("z"))
    );
    public static Serializer<Quaterniondc> QUATERNION4DC = of(
            quat -> {
                CompoundTag tag = new CompoundTag();
                tag.putDouble("x", quat.x());
                tag.putDouble("y", quat.y());
                tag.putDouble("z", quat.z());
                tag.putDouble("w", quat.w());
                return tag;
            },
            tag -> new Quaterniond(tag.getDouble("x"), tag.getDouble("y"), tag.getDouble("z"), tag.getDouble("w"))
    );

    public static Serializer<Vector3d> VECTOR3D = of(
            vec -> {
                CompoundTag tag = new CompoundTag();
                tag.putDouble("x", vec.x());
                tag.putDouble("y", vec.y());
                tag.putDouble("z", vec.z());
                return tag;
            },
            tag -> new Vector3d(tag.getDouble("x"), tag.getDouble("y"), tag.getDouble("z"))
    );
    public static Serializer<Quaterniond> QUATERNION4D = of(
            quat -> {
                CompoundTag tag = new CompoundTag();
                tag.putDouble("x", quat.x());
                tag.putDouble("y", quat.y());
                tag.putDouble("z", quat.z());
                tag.putDouble("w", quat.w());
                return tag;
            },
            tag -> new Quaterniond(tag.getDouble("x"), tag.getDouble("y"), tag.getDouble("z"), tag.getDouble("w"))
    );


    public static Serializer<AABBi> AABB_I = of(
            aabb -> {
                CompoundTag tag = new CompoundTag();
                tag.putInt("minX", aabb.minX());
                tag.putInt("minY", aabb.minY());
                tag.putInt("minZ", aabb.minZ());
                tag.putInt("maxX", aabb.maxX());
                tag.putInt("maxY", aabb.maxY());
                tag.putInt("maxZ", aabb.maxZ());
                return tag;
            },
            tag -> new AABBi(
                    tag.getInt("minX"),
                    tag.getInt("minY"),
                    tag.getInt("minZ"),
                    tag.getInt("maxX"),
                    tag.getInt("maxY"),
                    tag.getInt("maxZ")
            )
    );

    @SuppressWarnings("unchecked") // It's checked
    public static<T extends Enum<?>> Serializer<T> ofEnum(Class<T> enumClazz){
        return (Serializer<T>)EnumSerializerCache.computeIfAbsent(
                enumClazz,
                clazz_ ->
                        new Serializer<T>() {
                            final Class<T> clazz = enumClazz;

                            @Override
                            public CompoundTag serialize(@NotNull T obj) {
                                return INT.serialize(obj.ordinal());
                            }

                            @Override
                            public @NotNull T deserialize(CompoundTag tag) {
                                int ordinal = INT.deserialize(tag);
                                T validValue = clazz.getEnumConstants()[0];
                                try{
                                    validValue = clazz.getEnumConstants()[ordinal];
                                }catch (IndexOutOfBoundsException e){
                                    VManagerMod.LOGGER.error("receive ordinal: {}, but class {} does not contain that much elements", ordinal, clazz);
                                }
                                return validValue;
                            }
                        }
        );

    }



    public static<T, A, C extends Collection<T>> Serializer<Collection<T>> ofCollection(Serializer<T> elementSerializer, Collector<T, A, C> collector){
        return new Serializer<>() {
            @Override
            public CompoundTag serialize(@NotNull Collection<T> obj) {

                AtomicInteger count = new AtomicInteger();
                CompoundTagBuilder builder = new CompoundTagBuilder().withLong("count", (long) obj.size());
                obj.forEach(d -> builder.withCompound("element_" + count.getAndIncrement(), elementSerializer.serialize(d)));
                return builder.build();

            }

            @Override
            public @NotNull Collection<T> deserialize(CompoundTag tag) {
                A container = collector.supplier().get();
                long count = tag.getLong("count");
                for (long i = 0; i < count; i++) {
                    CompoundTag elementTag = tag.getCompound("element_" + i);
                    T element = elementSerializer.deserialize(elementTag);
                    collector.accumulator().accept(container, element);
                }
                return collector.finisher().apply(container);
            }
        };
    }

    public static<T> Serializer<Collection<T>> ofCollection(Serializer<T> elementSerializer){
        return new Serializer<>() {
            @Override
            public CompoundTag serialize(@NotNull Collection<T> obj) {

                AtomicInteger count = new AtomicInteger();
                CompoundTagBuilder builder = new CompoundTagBuilder().withLong("count", (long) obj.size());
                obj.forEach(d -> builder.withCompound("element_" + count.getAndIncrement(), elementSerializer.serialize(d)));
                return builder.build();

            }

            @Override
            public @NotNull Collection<T> deserialize(CompoundTag tag) {
                long count = tag.getLong("count");
                List<T> list = new ArrayList<>((int) count); // 固定使用ArrayList
                for (int i = 0; i < count; i++) {
                    CompoundTag elementTag = tag.getCompound("element_" + i);
                    T element = elementSerializer.deserialize(elementTag);
                    list.add(element);
                }
                return list; // 返回Collection<T>接口类型
            }
        };
    }

    public static <T> Serializer<T> of(Function<T, CompoundTag> serializer, Function<CompoundTag, T> deserializer){
        return new Serializer<>() {
            @Override
            public CompoundTag serialize(@NotNull T obj) {
                return serializer.apply(obj);
            }

            @Override
            public @NotNull T deserialize(CompoundTag tag) {
                return deserializer.apply(tag);
            }
        };
    }

    public static CompoundTag ofDouble(double d){
        CompoundTag tag = new CompoundTag();
        tag.putDouble("value", d);
        return tag;
    }

    public static CompoundTag ofFloat(float f){
        CompoundTag tag = new CompoundTag();
        tag.putFloat("value", f);
        return tag;
    }

    public static CompoundTag ofInt(int i){
        CompoundTag tag = new CompoundTag();
        tag.putInt("value", i);
        return tag;
    }

    public static CompoundTag ofBoolean(boolean b){
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("value", b);
        return tag;
    }

    public static CompoundTag ofString(String s){
        CompoundTag tag = new CompoundTag();
        tag.putString("value", s);
        return tag;
    }

    public static CompoundTag ofLong(long l){
        CompoundTag tag = new CompoundTag();
        tag.putLong("value", l);
        return tag;
    }

    public interface Serializer<T>{


        default CompoundTag serializeNullable(@Nullable T obj){
            return obj == null ? new CompoundTag() : serialize(obj);
        }

        default @Nullable T deserializeNullable(CompoundTag tag){
            return tag.isEmpty() ? null : deserialize(tag);
        }

        CompoundTag serialize(@NotNull T obj);

        @NotNull T deserialize(CompoundTag tag);
    }

}
