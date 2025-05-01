package com.verr1.valkyrienmanager.compact.kubejs.impl.event.types;

import com.verr1.valkyrienmanager.manager.events.vevents.VCreateEvent;
import com.verr1.valkyrienmanager.manager.events.vevents.VDetonateEvent;
import dev.latvian.mods.kubejs.event.EventJS;

public class JSOnDetonateAlarm extends EventJS {

    public final VDetonateEvent vDetonateEvent;


    public JSOnDetonateAlarm(VDetonateEvent vDetonateEvent) {
        this.vDetonateEvent = vDetonateEvent;
    }
}
