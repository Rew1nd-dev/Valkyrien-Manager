package com.verr1.valkyrienmanager.manager.db.snapshot;

import com.verr1.valkyrienmanager.manager.db.snapshot.item.SnapShotConfig;
import com.verr1.valkyrienmanager.network.ServerBoundRequestSyncSnapConfigPacket;
import com.verr1.valkyrienmanager.registry.VMPackets;
import net.minecraft.nbt.CompoundTag;

public class SnapShotClientView {



    private final SnapShotConfig snapConfig = new SnapShotConfig();


    public void requestConfig(){
        VMPackets.CHANNEL.sendToServer(new ServerBoundRequestSyncSnapConfigPacket());
    }

    public void receiveSyncedConfig(CompoundTag serializedSnap) {
        snapConfig.deserialize(serializedSnap);
    }

    public SnapShotConfig snapConfig() {
        return snapConfig;
    }

}
