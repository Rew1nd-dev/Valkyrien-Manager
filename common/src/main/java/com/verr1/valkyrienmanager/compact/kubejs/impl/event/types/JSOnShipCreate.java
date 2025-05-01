package com.verr1.valkyrienmanager.compact.kubejs.impl.event.types;

import com.verr1.valkyrienmanager.manager.events.vevents.VCreateEvent;
import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.rhino.util.HideFromJS;

public class JSOnShipCreate extends EventJS {

    public final VCreateEvent vCreateEvent;

    public JSOnShipCreate(VCreateEvent vCreateEvent) {
        this.vCreateEvent = vCreateEvent;
    }


}
