package com.verr1.valkyrienmanager.gui.widgets;

import com.verr1.valkyrienmanager.gui.screens.VMScreen;
import com.verr1.valkyrienmanager.registry.VMGuiTextures;
import gg.essential.elementa.UIComponent;
import gg.essential.elementa.components.UIImage;
import gg.essential.elementa.components.UIText;
import gg.essential.elementa.components.UIWrappedText;
import gg.essential.elementa.constraints.AdditiveConstraint;
import gg.essential.elementa.constraints.CenterConstraint;
import gg.essential.elementa.constraints.ConstantColorConstraint;
import gg.essential.elementa.constraints.PixelConstraint;
import gg.essential.elementa.constraints.animation.Animations;
import gg.essential.elementa.constraints.animation.ColorAnimationComponent;
import kotlin.Pair;

import java.awt.*;
import java.util.Optional;

public class UIButton extends UIToolTip {

    protected UIImage icon;

    protected UIWrappedText text;

    protected Runnable onMouseReleaseAt = () -> {};

    public Color baseColor() {
        return baseColor;
    }

    private Color baseColor = Color.LIGHT_GRAY;

    public UIButton(VMGuiTextures img, UIComponent root) {
        this(root);

        try{
            icon = (UIImage)UIImage.ofResource(img.location.getPath())
                    .setWidth(new PixelConstraint(img.width))
                    .setHeight(new PixelConstraint(img.height))
                    .setX(new CenterConstraint())
                    .setY(new CenterConstraint())
                    .setChildOf(this);
        }catch (Exception ignored){

        }


        this
                .setWidth(new PixelConstraint(img.width))
                .setHeight(new PixelConstraint(img.height));
    }

    public UIButton(String label, UIComponent root) {
        this(root);

        text = (UIWrappedText) new UIWrappedText()
                .setText(label)
                .setColor(Color.WHITE)
                .setX(new CenterConstraint())
                .setY(new CenterConstraint())
                .setChildOf(this);


    }

    public UIButton(UIComponent root) {
        super(root);

        onMouseEnter(
            component -> {
                component.setColor(new ColorAnimationComponent(
                        Animations.LINEAR,
                        10,
                        new ConstantColorConstraint(component.getColor()),
                        new ConstantColorConstraint(baseColor().brighter()),
                        0
                ));
                return null;
            }
        ).onMouseClick(
                (component, e) -> {
                    component.setColor(new ColorAnimationComponent(
                            Animations.IN_BOUNCE,
                            10,
                            new ConstantColorConstraint(component.getColor()),
                            new ConstantColorConstraint(baseColor().darker()),
                            0
                    ));
                    return null;
                }
        ).onMouseLeave(
                component -> {
                    component.setColor(new ColorAnimationComponent(
                            Animations.LINEAR,
                            10,
                            new ConstantColorConstraint(component.getColor()),
                            new ConstantColorConstraint(baseColor()),
                            0
                    ));
                    return null;
                }
        ).onMouseRelease(
                component -> {
                    if(!isHovered())return null;
                    onMouseReleaseAt.run();
                    component.setColor(new ColorAnimationComponent(
                            Animations.LINEAR,
                            10,
                            new ConstantColorConstraint(component.getColor()),
                            new ConstantColorConstraint(baseColor().brighter()),
                            0
                    ));
                    return null;
                }
        );//.enableEffect(VMScreen.create(Color.YELLOW));
    }

    public UIButton withOnMouseRelease(Runnable onMouseRelease){
        this.onMouseReleaseAt = onMouseRelease;
        return this;
    }

    public boolean isHovered() {
        Pair<Float, Float> m = getMousePosition();
        return m.getFirst() > getLeft() && m.getFirst() < getRight() && m.getSecond() > getTop() && m.getSecond() < getBottom();
    }

    @Override
    public void afterInitialization() {
        this.baseColor = getColor();
        Optional
            .ofNullable(text)
            .ifPresent(
                text -> text
                    .setWidth(new PixelConstraint(getWidth()))
                    .setHeight(new PixelConstraint(getHeight()))
            );
    }



}
