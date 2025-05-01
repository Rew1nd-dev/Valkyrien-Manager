package com.verr1.valkyrienmanager.manager.vcommand.all.tag;

import com.verr1.valkyrienmanager.VManagerServer;
import com.verr1.valkyrienmanager.foundation.data.VTag;
import com.verr1.valkyrienmanager.manager.db.general.item.NetworkKey;
import com.verr1.valkyrienmanager.manager.vcommand.VCommand;
import net.minecraft.nbt.CompoundTag;

import java.util.Set;

public class VModifyTag implements VCommand<VModifyTagContext> {
    @Override
    public Class<VModifyTagContext> type() {
        return VModifyTagContext.class;
    }

    @Override
    public VModifyTagContext deserialize(CompoundTag contextTag) {
        return VModifyTagContext.deserialize(contextTag);
    }

    @Override
    public CompoundTag serialize(VModifyTagContext contextTag) {
        return contextTag.serialize();
    }

    @Override
    public void execute(VModifyTagContext context) {
        if(context.tagName().isEmpty())return;
        if(!context.modifyCluster()){
            modify(context.add(), context.id(), context.tagName());
        }else{
            VManagerServer.manager().clusterOf(context.id()).ids.forEach(
                    id -> modify(context.add(), id, context.tagName())
            );
        }
    }

    private void modify(boolean add, long id, String name){
        Set<VTag> set = VManagerServer.DATA_BASE.get(id).get(NetworkKey.VTAG).get().raw;
        if(add){
            set.add(VTag.of(name));
        }else{
            set.remove(VTag.of(name));
        }
    }

}
