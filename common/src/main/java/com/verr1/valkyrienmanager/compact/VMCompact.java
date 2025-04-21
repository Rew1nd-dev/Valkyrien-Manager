package com.verr1.valkyrienmanager.compact;

import com.verr1.valkyrienmanager.VManagerMod;
import com.verr1.valkyrienmanager.compact.cctweaked.ICCApiInitializer;
import dev.architectury.platform.Platform;

public class VMCompact {

    public static void init(){
        if (Platform.isModLoaded("computercraft")) {
            try {
                // 动态加载 CC 兼容类
                ICCApiInitializer initializer = (ICCApiInitializer) Class.forName("com.verr1.valkyrienmanager.compact.cctweaked.impl.CCApiInitializerImpl")
                        .getDeclaredConstructor()
                        .newInstance();
                initializer.registerApis();
            } catch (Exception e) {
                VManagerMod.LOGGER.error("Failed to load CC compatibility class", e);
            }
        }
    }

}
