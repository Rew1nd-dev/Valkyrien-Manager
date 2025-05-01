package com.verr1.valkyrienmanager;

import com.verr1.valkyrienmanager.foundation.executor.Executor;
import com.verr1.valkyrienmanager.manager.VClientManager;
import com.verr1.valkyrienmanager.manager.db.general.VDataBase;
import com.verr1.valkyrienmanager.manager.db.snapshot.SnapShotClientView;
import net.minecraft.client.Minecraft;

public class VManagerClient {

    public static final VDataBase CLIENT_VIEW = new VDataBase();
    public static final SnapShotClientView SNAP_CLIENT_VIEW = new SnapShotClientView();
    public static final Executor CLIENT_EXECUTOR = new Executor();
    public static final VClientManager manager = new VClientManager(Minecraft.getInstance());

    public static VClientManager manager(){
        return manager;
    }

    public static void init(){


        /*

        * */


    }
}
