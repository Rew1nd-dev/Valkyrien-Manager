package com.verr1.valkyrienmanager.compact.kubejs.impl.event.types;

import com.verr1.valkyrienmanager.manager.events.vevents.VUnloadEvent;
import dev.latvian.mods.kubejs.event.EventJS;

public class JSOnShipUnload extends EventJS {

    public final VUnloadEvent vUnloadEvent;
    public JSOnShipUnload(VUnloadEvent vUnloadEvent) {
        this.vUnloadEvent = vUnloadEvent;
    }
}
