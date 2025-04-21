package com.verr1.valkyrienmanager.manager;

import com.verr1.valkyrienmanager.VManagerClient;
import com.verr1.valkyrienmanager.gui.screens.VMScreen;
import com.verr1.valkyrienmanager.manager.db.VDataBase;
import com.verr1.valkyrienmanager.manager.db.item.VItem;
import com.verr1.valkyrienmanager.manager.vcommand.Commands;
import com.verr1.valkyrienmanager.manager.vcommand.all.teleport.VTeleportContext;
import com.verr1.valkyrienmanager.manager.vcommand.all.toggle_own.VToggleOwnContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaterniond;
import org.valkyrienskies.core.api.ships.ClientShip;
import org.valkyrienskies.core.apigame.world.ClientShipWorldCore;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.List;
import java.util.Optional;

import static org.valkyrienskies.mod.common.util.VectorConversionsMCKt.toJOML;

public class VClientManager {

    private final Minecraft mc;
    private final ClientShipWorldCore vs_world;

    public VClientManager(Minecraft mc){
        this.mc = mc;
        vs_world = VSGameUtilsKt.getShipObjectWorld(this.mc);
    }

    private VDataBase db(){
        return VManagerClient.CLIENT_VIEW;
    }

    public @NotNull List<ClientShip> allShips(){
        return vs_world.getAllShips().stream().toList();
    }


    public void requestAll(){
        db().requestSyncKeys();
        db().request(VManager.REQUEST_ALL_ID, VItem.used);
        // allShips().stream().map(Ship::getId).forEach(this::requestAll);
    }

    public void requestAll(long id){
        db().request(id, VItem.used);
    }


    public void openGUI(){
        openScreen(new VMScreen());
    }

    private static void openScreen(Screen screen) {
        Minecraft.getInstance()
                .tell(() -> {
                    Minecraft.getInstance().setScreen(screen);
                });
    }

    public void teleportToLocal(long id){
        Optional.ofNullable(mc.player).map(
                p -> toJOML(p.getPosition(0))
        ).ifPresent(
                v -> Commands.TP.execute(new VTeleportContext(id, v, new Quaterniond()))
        );
    }

    public void toggleStatic(long id){
        Commands.TOGGLE_STATIC.execute(id);
    }

    public void toggleOwn(long id){
        assert mc.player != null;
        Commands.TOGGLE_OWN.execute(new VToggleOwnContext(id, mc.player.getName().getString()));
    }

}
