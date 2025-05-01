package com.verr1.valkyrienmanager.manager.vcommand.all;

import com.verr1.valkyrienmanager.manager.vcommand.VCommand;
import com.verr1.valkyrienmanager.util.SerializeUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

import java.util.function.BiFunction;
import java.util.function.Consumer;

public interface VEnumCommand<T extends Enum<?>> extends VCommand<T> {

    Class<T> type();

    @Override
    default CompoundTag serialize(T enumValue){
        return SerializeUtils.ofEnum(type()).serialize(enumValue);
    }

    @Override
    default T deserialize(CompoundTag tag){
        return SerializeUtils.ofEnum(type()).deserialize(tag);
    }

    static<E extends Enum<?>> VEnumCommand<E> of(
            Consumer<E> execution,
            Class<E> enumClass
    ){
        return new VEnumCommand<>() {

            @Override
            public Class<E> type() {
                return enumClass;
            }

            @Override
            public void execute(E context) {
                execution.accept(context);
            }
        };
    }


    static<E extends Enum<?>> VEnumCommand<E> with(
            BiFunction<Player, E, QualifyResult> condition,
            Consumer<E> execution,
            Class<E> enumClass
    ){
        return new VEnumCommand<>() {

            @Override
            public Class<E> type() {
                return enumClass;
            }

            @Override
            public QualifyResult qualify(Player executor, E context) {
                return condition.apply(executor, context);
            }

            @Override
            public void execute(E context) {
                execution.accept(context);
            }
        };
    }

}
