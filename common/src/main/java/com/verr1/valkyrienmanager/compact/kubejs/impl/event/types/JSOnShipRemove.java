package com.verr1.valkyrienmanager.compact.kubejs.impl.event.types;

import com.verr1.valkyrienmanager.manager.events.vevents.VRemoveEvent;
import dev.latvian.mods.kubejs.event.EventJS;

public class JSOnShipRemove extends EventJS {

    public final VRemoveEvent vRemoveEvent;
    public JSOnShipRemove(VRemoveEvent vRemoveEvent) {
        this.vRemoveEvent = vRemoveEvent;
    }

}
