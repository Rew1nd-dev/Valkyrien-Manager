package com.verr1.valkyrienmanager.gui.factory;

import com.verr1.valkyrienmanager.gui.widgets.TextTreeView;
import com.verr1.valkyrienmanager.manager.db.entry.IDEntry;
import com.verr1.valkyrienmanager.manager.db.item.NetworkKey;
import com.verr1.valkyrienmanager.manager.db.item.VItem;
import com.verr1.valkyrienmanager.util.TimeUtil;

public class UIFactory {

    public static TextTreeView create(VItem item) {

        var clusterTextView = new TextTreeView("cluster: size[ " + item.get(NetworkKey.CLUSTER).get().ids.size() + " ]");
        var ownerTextView = new TextTreeView("owner: size[ " + item.get(NetworkKey.OWNER).get().raw.size() + " ]");
        var bornAroundTextView = new TextTreeView("bornAround: size[ " + item.get(NetworkKey.BORN_AROUND).get().raw.size() + " ]");
        var vTagsTextView = new TextTreeView("vtags: size[ " + item.get(NetworkKey.VTAG).get().raw.size() + " ]");
        item.get(NetworkKey.CLUSTER).get().ids.forEach(
                id -> clusterTextView.addChild(new TextTreeView("id: " + id, false))
        );

        item.get(NetworkKey.OWNER).get().raw.forEach(
                owner -> ownerTextView.addChild(new TextTreeView(owner.playerName(), false))
        );

        item.get(NetworkKey.BORN_AROUND).get().raw.forEach(
                owner -> bornAroundTextView.addChild(new TextTreeView(owner.playerName(), false))
        );

        item.get(NetworkKey.VTAG).get().raw.forEach(
                tag -> vTagsTextView.addChild(new TextTreeView(tag.name(), false))
        );


        return (TextTreeView)
                new TextTreeView("id: " + item.get(NetworkKey.ID).get())
                .addChild(
                        new TextTreeView("slug: " + item.get(NetworkKey.SLUG).get(), false)
                ).addChild(
                        new TextTreeView("birth: " + TimeUtil.Date_Formatter.format(item.get(NetworkKey.BIRTH).get()), false)
                ).addChild(
                        clusterTextView
                ).addChild(
                        ownerTextView
                ).addChild(
                        bornAroundTextView
                ).addChild(
                        vTagsTextView
                );

    }

}
