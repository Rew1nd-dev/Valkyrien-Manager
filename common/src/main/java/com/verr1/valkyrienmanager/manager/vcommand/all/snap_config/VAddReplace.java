package com.verr1.valkyrienmanager.manager.vcommand.all.snap_config;

import com.verr1.valkyrienmanager.VManagerServer;
import com.verr1.valkyrienmanager.manager.vcommand.all.VStringCommand;

public class VAddReplace implements VStringCommand {
    @Override
    public void execute(String context) {
        VManagerServer.SNAP_DATA_BASE.snapConfig().neglect(context);
    }
}
