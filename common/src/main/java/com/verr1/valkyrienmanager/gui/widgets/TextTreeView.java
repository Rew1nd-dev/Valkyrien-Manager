package com.verr1.valkyrienmanager.gui.widgets;

import gg.essential.elementa.UIComponent;
import gg.essential.elementa.components.TreeArrowComponent;
import gg.essential.elementa.components.TreeNode;
import gg.essential.elementa.components.UIText;
import gg.essential.elementa.components.inspector.ArrowComponent;
import gg.essential.elementa.constraints.SiblingConstraint;
import org.jetbrains.annotations.NotNull;

public class TextTreeView extends TreeNode {

    private final String text;

    private final boolean renderArrow;

    public TextTreeView(String text, boolean renderArrow) {
        this.text = text;
        this.renderArrow = renderArrow;
    }

    public TextTreeView(String text) {
        this.text = text;
        this.renderArrow = true;
    }

    @NotNull
    @Override
    public TreeArrowComponent getArrowComponent() {
        return new ArrowComponent(!renderArrow);
    }

    @NotNull
    @Override
    public UIComponent getPrimaryComponent() {
        return new UIText(text).setX(new SiblingConstraint());
    }
}
