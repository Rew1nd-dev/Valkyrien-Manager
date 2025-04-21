package com.verr1.valkyrienmanager.compact.cctweaked.impl;

import com.verr1.valkyrienmanager.VManagerServer;
import com.verr1.valkyrienmanager.compact.cctweaked.ICCApiInitializer;
import com.verr1.valkyrienmanager.manager.events.VSMEvents;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.core.ServerContext;
import it.unimi.dsi.fastutil.ints.IntArraySet;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class CCApiInitializerImpl implements ICCApiInitializer {

    @Override
    public void registerApis() {
        ComputerCraftAPI.registerAPIFactory(
            computer -> new VMApi()
        );
        VSMEvents.CREATE_EVENT_LISTENERS.put("create_ship_cc_listener", $ -> queueVSMEvent());
    }

    public static void queueVSMEvent(){
        ServerContext
                .get(VManagerServer.INSTANCE)
                .registry()
                .getComputers()
                .stream()
                .filter(c -> c.getFamily() == ComputerFamily.COMMAND)
                .forEach(
                        serverComputer -> serverComputer.queueEvent("test")

                );
    }



}
