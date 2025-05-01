package com.verr1.valkyrienmanager.compact.vmod.impl.create;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.spaceeye.vmod.compat.schem.SchemCompatItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.valkyrienskies.core.api.ships.ServerShip;

import java.util.List;
import java.util.Map;

public class VschemCreateCompact implements SchemCompatItem {
    @Override
    public void onCopy(
            @NotNull ServerLevel serverLevel,
            @NotNull BlockPos blockPos,
            @NotNull BlockState blockState,
            @NotNull List<? extends ServerShip> list,
            @Nullable BlockEntity blockEntity,
            @Nullable CompoundTag compoundTag,
            @NotNull Function0<Unit> function0
    ) {

    }

    @Override
    public void onPaste(
            @NotNull ServerLevel serverLevel,
            @NotNull Map<Long, Long> map,
            @NotNull CompoundTag compoundTag,
            @NotNull BlockPos blockPos,
            @NotNull BlockState blockState,
            @NotNull Function2<? super Boolean, ? super Function1<? super CompoundTag, ? extends CompoundTag>, Unit> delayLoading,
            @NotNull Function1<? super Function1<? super BlockEntity, Unit>, Unit> finalAdd
    ) {

    }
}
