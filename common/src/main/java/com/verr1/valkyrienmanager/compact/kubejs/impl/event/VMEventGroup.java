package com.verr1.valkyrienmanager.compact.kubejs.impl.event;

import com.verr1.valkyrienmanager.compact.kubejs.impl.event.types.*;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;

public class VMEventGroup {

    public static final EventGroup VM_EVENT_GROUP = EventGroup.of("VEvents");

    public static final EventHandler ON_SHIP_CREATE = VM_EVENT_GROUP.server("onShipCreate", () -> JSOnShipCreate.class);
    public static final EventHandler ON_SHIP_REMOVE = VM_EVENT_GROUP.server("onShipRemove", () -> JSOnShipRemove.class);
    public static final EventHandler ON_SHIP_UNLOAD = VM_EVENT_GROUP.server("onShipUnload", () -> JSOnShipUnload.class);
    public static final EventHandler ON_SHIP_LOAD = VM_EVENT_GROUP.server("onShipLoad", () -> JSOnShipLoad.class);
    public static final EventHandler ON_DETONATE_ALARM = VM_EVENT_GROUP.server("onDetonate", () -> JSOnDetonateAlarm.class);

}
