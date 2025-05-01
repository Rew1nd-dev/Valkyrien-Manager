package com.verr1.valkyrienmanager.manager.vcommand.all.toggle_own;

import com.verr1.valkyrienmanager.VManagerServer;
import com.verr1.valkyrienmanager.manager.db.general.item.NetworkKey;
import com.verr1.valkyrienmanager.manager.vcommand.VCommand;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.concurrent.atomic.AtomicReference;

public class VToggleOwn implements VCommand<VToggleOwnContext> {

    @Override
    public Class<VToggleOwnContext> type() {
        return VToggleOwnContext.class;
    }

    @Override
    public QualifyResult qualify(Player executor, VToggleOwnContext context) {
        return new QualifyResult(true, "no message");
    }

    @Override
    public VToggleOwnContext deserialize(CompoundTag contextTag) {
        return VToggleOwnContext.deserialize(contextTag);
    }

    @Override
    public CompoundTag serialize(VToggleOwnContext contextTag) {
        return contextTag.serialize();
    }

    @Override
    public void execute(VToggleOwnContext context) {

        boolean isOwned = VManagerServer.DATA_BASE
                        .getOptional(context.id())
                        .map(v -> v.get(NetworkKey.OWNER).get())
                        .map(w -> w.containsName(context.playerName()))
                        .orElse(false);

        AtomicReference<ServerPlayer> playerResult = new AtomicReference<>(null);
        VManagerServer.getServer().map(MinecraftServer::getAllLevels).map(Iterable::iterator).ifPresent(
                it -> {
                    while (it.hasNext()){
                        ServerLevel lvl = it.next();
                        ServerPlayer player = lvl.getPlayers(p -> p.getName().getString().equals(context.playerName())).stream().findAny().orElse(null);
                        if(player != null){
                            playerResult.set(player);
                            break;
                        }
                    }
                }
        );

        if(playerResult.get() == null)return;
        if(isOwned){
            VManagerServer.manager().abandonCluster(playerResult.get(), context.id());
        }else {
            VManagerServer.manager().ownCluster(playerResult.get(), context.id());
        }

    }


}
