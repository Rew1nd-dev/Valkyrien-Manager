package com.verr1.valkyrienmanager.manager.vcommand.all.teleport;

import com.verr1.valkyrienmanager.VManagerServer;
import com.verr1.valkyrienmanager.manager.vcommand.VCommand;
import net.minecraft.nbt.CompoundTag;

public class VTeleport implements VCommand<VTeleportContext> {


    @Override
    public Class<VTeleportContext> type() {
        return VTeleportContext.class;
    }

    @Override
    public VTeleportContext deserialize(CompoundTag contextTag) {
        return VTeleportContext.deserialize(contextTag);
    }

    @Override
    public CompoundTag serialize(VTeleportContext context) {
        return context.serialize();
    }

    @Override
    public void execute(VTeleportContext context) {
        VManagerServer.manager().teleportWithCluster(context.id(), context.destination(), context.rotation());
    }


}
