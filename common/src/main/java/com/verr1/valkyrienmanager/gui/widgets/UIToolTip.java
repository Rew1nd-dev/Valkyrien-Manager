package com.verr1.valkyrienmanager.gui.widgets;

import gg.essential.elementa.UIComponent;
import gg.essential.elementa.components.UIBlock;
import gg.essential.elementa.components.UIWrappedText;
import gg.essential.elementa.constraints.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.verr1.valkyrienmanager.gui.screens.VMScreen.executeLater;

public abstract class UIToolTip extends UIBlock {

    private final List<String> tooltips = new ArrayList<>();

    private final UIBlock tooltipContainer = (UIBlock)new UIBlock()
            .setWidth(new AdditiveConstraint(new ChildBasedSizeConstraint(4f), new PixelConstraint(4f)))
            .setHeight(new ChildBasedSizeConstraint(4f))
            .setColor(Color.BLACK);

    private final UIWrappedText tooltipText = (UIWrappedText)new UIWrappedText()
            .setX(new PixelConstraint(4f))
            .setY(new PixelConstraint(4f))
            .setChildOf(tooltipContainer);



    public List<String> tooltips() {
        return tooltips;
    }

    protected float evaluate(String text){
        return getFontProvider().getStringWidth(text, 0.5f);
    }

    public UIToolTip setTooltips(List<String> tooltips) {
        this.tooltips.clear();
        this.tooltips.addAll(tooltips);
        return this;
    }

    private String createText(){
        StringBuilder sb = new StringBuilder();
        for (String s : tooltips) {
            sb.append(s).append("\n");
        }
        return sb.toString();
    }


    public UIToolTip(UIComponent root) {
        super();
        onMouseEnter(
                (component) ->{
                String text = createText();
                if(text.isEmpty())return null;
                tooltipText
                        .setText(text)
                        .setWidth(new PixelConstraint(Math.min(evaluate(text), 80)))
                        .setTextScale(new PixelConstraint(0.8f))
                        ;


                var x = getLeft(); // - tooltipText.getWidth() / 2; // + getWidth() / 2;
                var y = getBottom();

                tooltipContainer
                        .setX(new PixelConstraint(x))
                        .setY(new PixelConstraint(y));

                executeLater(() -> tooltipContainer.setChildOf(root), 1);


                return null;
            }
        ).onMouseLeave(
                component -> {
                    executeLater(() -> Optional.ofNullable(tooltipContainer.parent).ifPresent(
                            parent -> parent.removeChild(tooltipContainer)
                    ), 1);
                    return null;
                }
        );

    }



}
