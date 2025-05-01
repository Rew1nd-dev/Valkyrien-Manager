package com.verr1.valkyrienmanager.manager.vcommand.all.rename;

import com.verr1.valkyrienmanager.VManagerServer;
import com.verr1.valkyrienmanager.manager.vcommand.VCommand;
import net.minecraft.nbt.CompoundTag;

import java.util.concurrent.atomic.AtomicInteger;

public class VRename implements VCommand<VRenameContext> {
    @Override
    public Class<VRenameContext> type() {
        return VRenameContext.class;
    }

    @Override
    public VRenameContext deserialize(CompoundTag contextTag) {
        return VRenameContext.deserialize(contextTag);
    }

    @Override
    public CompoundTag serialize(VRenameContext contextTag) {
        return contextTag.serialize();
    }

    @Override
    public void execute(VRenameContext context) {
        if (context.newName().isEmpty()) return;
        if (!context.modifyCluster()) {
            modify(context.id(), context.newName());
        } else {
            AtomicInteger i = new AtomicInteger(0);
            VManagerServer.manager().clusterOf(context.id()).ids.forEach(
                    id -> modify(id, context.newName() + "_" + i.getAndIncrement())
            );
        }
    }

    private void modify(long id, String name){
        VManagerServer.manager().shipOf(id).ifPresent(ship -> ship.setSlug(name));
    }
}
