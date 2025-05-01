package com.verr1.valkyrienmanager.manager.vcommand.all.tag_registry;

import com.verr1.valkyrienmanager.VManagerServer;
import com.verr1.valkyrienmanager.manager.vcommand.all.VStringCommand;

public class VRemoveTag implements VStringCommand {
    @Override
    public void execute(String context) {
        VManagerServer.DATA_BASE.unregister(context);
    }
}
