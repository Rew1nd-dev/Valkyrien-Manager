package com.verr1.valkyrienmanager.compact.kubejs.impl;

import com.verr1.valkyrienmanager.VManagerServer;
import com.verr1.valkyrienmanager.compact.kubejs.impl.event.VMEventGroup;
import com.verr1.valkyrienmanager.compact.kubejs.impl.event.types.*;
import com.verr1.valkyrienmanager.manager.events.VSMEvents;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.util.AttachedData;
import net.minecraft.server.MinecraftServer;



public class KubeVM extends KubeJSPlugin {

    @Override
    public void init() {

        VSMEvents.CREATE_EVENT_LISTENERS.put("kjs", e -> {
            VMEventGroup.ON_SHIP_CREATE.post(new JSOnShipCreate(e));
        });

        VSMEvents.REMOVE_EVENT_LISTENERS.put("kjs", e -> {
            VMEventGroup.ON_SHIP_REMOVE.post(new JSOnShipRemove(e));
        });

        VSMEvents.UNLOAD_EVENT_LISTENERS.put("kjs", e -> {
            VMEventGroup.ON_SHIP_UNLOAD.post(new JSOnShipUnload(e));
        });

        VSMEvents.LOAD_EVENT_LISTENERS.put("kjs", e -> {
            VMEventGroup.ON_SHIP_LOAD.post(new JSOnShipLoad(e));
        });

        VSMEvents.DETONATE_ALARM_EVENT_LISTENERS.put("kjs", e -> {
            VMEventGroup.ON_DETONATE_ALARM.post(new JSOnDetonateAlarm(e));
        });

    }

    @Override
    public void initStartup() {
    }

    @Override
    public void clientInit() {
    }

    @Override
    public void registerEvents() {
        VMEventGroup.VM_EVENT_GROUP.register();
        super.registerEvents();
    }
    @Override
    public void afterInit() {
    }
    @Override
    public void registerBindings(BindingsEvent event) {
    }

    @Override
    public void attachServerData(AttachedData<MinecraftServer> event) {
        event.add("vmdb", VManagerServer.DATA_BASE);

    }
}
