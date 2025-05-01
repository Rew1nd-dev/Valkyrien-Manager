package com.verr1.valkyrienmanager.compact.cctweaked.impl;

import com.verr1.valkyrienmanager.VManagerServer;
import com.verr1.valkyrienmanager.compact.cctweaked.ICCApiInitializer;
import com.verr1.valkyrienmanager.manager.events.VSMEvents;
import com.verr1.valkyrienmanager.manager.events.vevents.VCreateEvent;
import com.verr1.valkyrienmanager.manager.events.vevents.VLoadEvent;
import com.verr1.valkyrienmanager.manager.events.vevents.VRemoveEvent;
import com.verr1.valkyrienmanager.manager.events.vevents.VUnloadEvent;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.core.ServerContext;

public class CCApiInitializerImpl implements ICCApiInitializer {

    @Override
    public void registerApis() {
        ComputerCraftAPI.registerAPIFactory(
            computer -> new VMApi()
        );
        VSMEvents.CREATE_EVENT_LISTENERS.put("cc_listener", CCApiInitializerImpl::queueVSMCreateEvent);
        VSMEvents.REMOVE_EVENT_LISTENERS.put("cc_listener", CCApiInitializerImpl::queueVSMRemoveEvent);
        VSMEvents.LOAD_EVENT_LISTENERS.put("cc_listener", CCApiInitializerImpl::queueVSMLoadEvent);
        VSMEvents.UNLOAD_EVENT_LISTENERS.put("cc_listener", CCApiInitializerImpl::queueVSMUnloadEvent);
    }

    public static void queueVSMCreateEvent(VCreateEvent event) {
        ServerContext
                .get(VManagerServer.INSTANCE)
                .registry()
                .getComputers()
                .stream()
                .filter(c -> c.getFamily() == ComputerFamily.COMMAND)
                .forEach(
                        $ -> $.queueEvent("VSM::OnShipCreate", new Object[]{event.toLua()})
                );
    }

    public static void queueVSMLoadEvent(VLoadEvent event) {
        ServerContext
                .get(VManagerServer.INSTANCE)
                .registry()
                .getComputers()
                .stream()
                .filter(c -> c.getFamily() == ComputerFamily.COMMAND)
                .forEach(
                        $ -> $.queueEvent("VSM::OnShipLoad", new Object[]{event.toLua()})
                );
    }

    public static void queueVSMUnloadEvent(VUnloadEvent event) {
        ServerContext
                .get(VManagerServer.INSTANCE)
                .registry()
                .getComputers()
                .stream()
                .filter(c -> c.getFamily() == ComputerFamily.COMMAND)
                .forEach(
                        $ -> $.queueEvent("VSM::OnShipUnload", new Object[]{event.toLua()})
                );
    }

    public static void queueVSMRemoveEvent(VRemoveEvent event) {
        ServerContext
                .get(VManagerServer.INSTANCE)
                .registry()
                .getComputers()
                .stream()
                .filter(c -> c.getFamily() == ComputerFamily.COMMAND)
                .forEach(
                        $ -> $.queueEvent("VSM::OnShipRemove", new Object[]{event.toLua()})
                );
    }



}
