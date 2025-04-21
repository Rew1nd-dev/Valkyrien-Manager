package com.verr1.valkyrienmanager;

import com.verr1.valkyrienmanager.manager.VClientManager;
import com.verr1.valkyrienmanager.manager.db.VDataBase;
import net.minecraft.client.Minecraft;

public class VManagerClient {

    public static final VDataBase CLIENT_VIEW = new VDataBase();
    public static final VClientManager manager = new VClientManager(Minecraft.getInstance());

    public static VClientManager manager(){
        return manager;
    }

    public static void init(){

    }
}
