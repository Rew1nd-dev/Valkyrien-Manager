package com.verr1.valkyrienmanager.manager.vcommand.all.tag_registry;

import com.verr1.valkyrienmanager.VManagerServer;
import com.verr1.valkyrienmanager.manager.vcommand.all.VStringCommand;

public class VAddTag implements VStringCommand {
    @Override
    public void execute(String context) {
        VManagerServer.DATA_BASE.register(context);
    }
}
