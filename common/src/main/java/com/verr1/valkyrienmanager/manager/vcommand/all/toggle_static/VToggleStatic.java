package com.verr1.valkyrienmanager.manager.vcommand.all.toggle_static;

import com.verr1.valkyrienmanager.VManagerServer;
import com.verr1.valkyrienmanager.manager.vcommand.all.VUnaryIDOperator;
import org.valkyrienskies.core.api.ships.ServerShip;

public class VToggleStatic extends VUnaryIDOperator {



    @Override
    public void execute(Long context) {
        boolean isStatic = VManagerServer.manager().shipOf(context).map(ServerShip::isStatic).orElse(false);
        VManagerServer.manager().clusterOf(context).forEachShip(s -> s.setStatic(!isStatic));
    }
}
