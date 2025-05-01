package com.verr1.valkyrienmanager.gui.factory;

import com.verr1.valkyrienmanager.VManagerClient;
import com.verr1.valkyrienmanager.gui.widgets.TextTreeView;
import com.verr1.valkyrienmanager.manager.db.general.item.NetworkKey;
import com.verr1.valkyrienmanager.manager.db.general.item.VItem;
import com.verr1.valkyrienmanager.util.TimeUtil;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3i;
import org.joml.primitives.AABBic;

public class UIFactory {


    public static Vector3i xyz(AABBic aabb) {
        return new Vector3i(aabb.maxX() - aabb.minX(), aabb.maxY() - aabb.minY(), aabb.maxZ() - aabb.minZ());
    }

    public static Vector3d playerPosition(){
        return VManagerClient.manager().playerPos();
    }

    public static int volumeOf(AABBic aabb) {
        return (aabb.maxX() - aabb.minX()) * (aabb.maxY() - aabb.minY()) * (aabb.maxZ() - aabb.minZ());
    }

    public static double clamp(double d, int digits){
        return Math.round(d * Math.pow(10, digits)) / Math.pow(10, digits);
    }

    public static TextTreeView create(VItem item) {

        var clusterTextView = new TextTreeView("cluster: size[ " + item.get(NetworkKey.CLUSTER).view().ids.size() + " ]");
        var ownerTextView = new TextTreeView("owner: size[ " + item.get(NetworkKey.OWNER).view().raw.size() + " ]");
        var bornAroundTextView = new TextTreeView("bornAround: size[ " + item.get(NetworkKey.BORN_AROUND).view().raw.size() + " ]");
        var vTagsTextView = new TextTreeView("vtags: size[ " + item.get(NetworkKey.VTAG).view().raw.size() + " ]");


        AABBic aabb = item.get(NetworkKey.AABB).view();
        var aabbTextView = new TextTreeView("aabb size: " + volumeOf(aabb) + " m^3", true)
                .addChild(
                        new TextTreeView("xyz: ", true)
                                .addChild(
                                        new TextTreeView("x: " + clamp(xyz(aabb).x(), 2), false)
                                ).addChild(
                                        new TextTreeView("y: " + clamp(xyz(aabb).y(), 2), false)
                                ).addChild(
                                        new TextTreeView("z: " + clamp(xyz(aabb).z(), 2), false)
                                )
                );

        Vector3dc coordinate = item.get(NetworkKey.COORDINATE).view();
        var coordinateTextView = new TextTreeView("position: " + clamp(coordinate.distance(playerPosition()), 2) + " m away")
                .addChild(new TextTreeView("value: ", true)
                        .addChild(
                                new TextTreeView("x: " + coordinate.x(), false)
                        ).addChild(
                                new TextTreeView("y: " + coordinate.y(), false)
                        ).addChild(
                                new TextTreeView("z: " + coordinate.z(), false)
                        ));

        item.get(NetworkKey.CLUSTER).view().ids.forEach(
                id -> clusterTextView.addChild(new TextTreeView("id: " + id, false))
        );

        item.get(NetworkKey.OWNER).view().raw.forEach(
                owner -> ownerTextView.addChild(new TextTreeView(owner.playerName(), false))
        );

        item.get(NetworkKey.BORN_AROUND).view().raw.forEach(
                owner -> bornAroundTextView.addChild(new TextTreeView(owner.playerName(), false))
        );

        item.get(NetworkKey.VTAG).view().raw.forEach(
                tag -> vTagsTextView.addChild(new TextTreeView(tag.name(), false))
        );


        return (TextTreeView)
                new TextTreeView("id: " + item.get(NetworkKey.ID).view() + " slug: " + item.get(NetworkKey.SLUG).view(), true)
                .addChild(
                        new TextTreeView("slug: " + item.get(NetworkKey.SLUG).view(), false)
                ).addChild(
                        new TextTreeView("birth: " + TimeUtil.Date_Formatter.format(item.get(NetworkKey.BIRTH).view()), false)
                ).addChild(
                        clusterTextView
                ).addChild(
                        ownerTextView
                ).addChild(
                        bornAroundTextView
                ).addChild(
                        vTagsTextView
                ).addChild(
                        aabbTextView
                ).addChild(
                        coordinateTextView
                );

    }

}
