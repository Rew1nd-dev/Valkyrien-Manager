package com.verr1.valkyrienmanager.manager.vcommand.all;

import com.verr1.valkyrienmanager.manager.vcommand.VCommand;
import com.verr1.valkyrienmanager.util.SerializeUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

import java.util.function.BiFunction;
import java.util.function.Consumer;

public interface VStringCommand extends VCommand<String> {
    @Override
    default Class<String> type() {
        return String.class;
    }

    @Override
    default String deserialize(CompoundTag contextTag) {
        return SerializeUtils.STRING.deserialize(contextTag);
    }

    @Override
    default CompoundTag serialize(String contextTag) {
        return SerializeUtils.STRING.serialize(contextTag);
    }


    static VCommand<String> with(BiFunction<Player, String, QualifyResult> condition, Consumer<String> execution){
        return new VStringCommand() {
            @Override
            public QualifyResult qualify(Player executor, String context) {
                return condition.apply(executor, context);
            }

            @Override
            public void execute(String context) {
                execution.accept(context);
            }
        };
    }
}
