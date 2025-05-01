package com.verr1.valkyrienmanager.manager;

import com.verr1.valkyrienmanager.VManagerClient;
import com.verr1.valkyrienmanager.gui.screens.VMScreen;
import com.verr1.valkyrienmanager.manager.db.general.VDataBase;
import com.verr1.valkyrienmanager.manager.db.general.item.VItem;
import com.verr1.valkyrienmanager.manager.db.snapshot.SnapShotClientView;
import com.verr1.valkyrienmanager.manager.db.snapshot.item.SnapShotConfig;
import com.verr1.valkyrienmanager.manager.vcommand.Commands;
import com.verr1.valkyrienmanager.manager.vcommand.all.rename.VRenameContext;
import com.verr1.valkyrienmanager.manager.vcommand.all.tag.VModifyTagContext;
import com.verr1.valkyrienmanager.manager.vcommand.all.teleport.VTeleportContext;
import com.verr1.valkyrienmanager.manager.vcommand.all.toggle_own.VToggleOwnContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.ClientShip;
import org.valkyrienskies.core.apigame.world.ClientShipWorldCore;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.List;
import java.util.Optional;

import static org.valkyrienskies.mod.common.util.VectorConversionsMCKt.toJOML;

public class VClientManager {

    private final Minecraft mc;
    private final ClientShipWorldCore vs_world;

    private final VMScreen screen = new VMScreen();

    public VClientManager(Minecraft mc){
        this.mc = mc;
        vs_world = VSGameUtilsKt.getShipObjectWorld(this.mc);
    }

    private VDataBase db(){
        return VManagerClient.CLIENT_VIEW;
    }

    private SnapShotClientView sdb(){
        return VManagerClient.SNAP_CLIENT_VIEW;
    }

    public @NotNull List<ClientShip> allShips(){
        return vs_world.getAllShips().stream().toList();
    }


    public void requestAll(){
        db().requestSyncKeys();
        db().requestSyncTags();
        db().request(VManager.REQUEST_ALL_ID, VItem.used);
    }

    public void requestAll(long id){
        db().request(id, VItem.used);
    }

    public void requestSnapConfig(){
        sdb().requestConfig();
    }


    public void openGUI(){
        try{
            openScreen(screen);
        }catch (Exception ignored){
        }

    }

    private static void openScreen(Screen screen) {
        Minecraft.getInstance()
                .tell(() -> {
                    Minecraft.getInstance().setScreen(screen);
                });
    }

    public void teleportToLocal(long id){
        Optional.ofNullable(mc.player).map(
                p -> {

                    Vector3dc view = toJOML(p.getViewVector(0));
                    Vector3dc pos = toJOML(p.getEyePosition());


                    return pos.fma(5, view, new Vector3d());
                }
        ).ifPresent(
                v -> Commands.TP.execute(new VTeleportContext(id, v, new Quaterniond()))
        );
    }

    public void toggleStatic(long id){
        Commands.TOGGLE_STATIC.execute(id);
    }

    public Vector3d playerPos(){
        return Optional.ofNullable(mc.player).map(p -> toJOML(p.getEyePosition())).orElse(new Vector3d());
    }

    public Player player(){
        return mc.player;
    }

    public void toggleOwn(long id){
        assert mc.player != null;
        Commands.TOGGLE_OWN.execute(new VToggleOwnContext(id, mc.player.getName().getString()));
    }

    public void rename(long id, String newName, boolean modifyCluster){
        Commands.RENAME.execute(new VRenameContext(id, newName, modifyCluster));
    }

    public void handleTag(long id, String tag, boolean add, boolean modifyCluster){
        Commands.TAG.execute(new VModifyTagContext(id, tag, add, modifyCluster));
    }

    public void handleTag(String tag, boolean add){
        if(add) Commands.ADD_TAG.execute(tag);
        else Commands.REMOVE_TAG.execute(tag);
    }

    public void handleReplaceConfig(String content, boolean add){
        if (add)Commands.ADD_REPLACE.execute(content);
        else Commands.REMOVE_REPLACE.execute(content);
    }

    public void handlePlaceConfig(String content, boolean add){
        if (add)Commands.ADD_PLACE.execute(content);
        else Commands.REMOVE_PLACE.execute(content);
    }

    public void handleReplaceConfig(SnapShotConfig.ReplaceMode mode){
        Commands.REPLACE_MODE.execute(mode);
    }

    public void handlePlaceConfig(SnapShotConfig.PlaceMode mode){
        Commands.PLACE_MODE.execute(mode);
    }

}
