package com.verr1.valkyrienmanager.compact.kubejs.impl.event.types;

import com.verr1.valkyrienmanager.manager.events.vevents.VLoadEvent;
import dev.latvian.mods.kubejs.event.EventJS;

public class JSOnShipLoad extends EventJS {

    public final VLoadEvent vLoadEvent;

    public JSOnShipLoad(VLoadEvent vLoadEvent) {
        this.vLoadEvent = vLoadEvent;
    }

}
