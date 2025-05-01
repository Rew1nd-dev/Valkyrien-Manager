package com.verr1.valkyrienmanager.gui.widgets;

import gg.essential.elementa.UIComponent;
import gg.essential.elementa.components.UIBlock;
import gg.essential.elementa.components.UIWrappedText;
import gg.essential.elementa.constraints.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.verr1.valkyrienmanager.gui.screens.VMScreen.create;
import static com.verr1.valkyrienmanager.gui.screens.VMScreen.executeLater;

/**
 *  T is Option Class
 */


public class UIOption<T> extends UIButton {

    private List<UIWrappedText> Options = new ArrayList<>();
    private final UIBlock OptionBlock = (UIBlock)new UIBlock()
            .setWidth(new ChildBasedSizeConstraint(4f))
            .setHeight(new ChildBasedSizeConstraint(0f));

    private final Consumer<T> onSelect;
    private List<T> scope;
    private final Function<T, String> formatter;
    private T currentSelection;
    private int currentState = 0;



    public UIOption(List<T> scope, Function<T, String> display, Consumer<T> onSelect, UIComponent root) {
        super(root);

        this.onSelect = onSelect;
        this.formatter = display;


        withScope(scope)
        .onMouseEnter(component -> {
            var x = getLeft(); // - tooltipText.getWidth() / 2; // + getWidth() / 2;
            var y = getHeight() + getTop();

            OptionBlock
                    .setX(new PixelConstraint(x))
                    .setY(new PixelConstraint(y))
                    .setColor(Color.BLACK);

            executeLater(() -> root.addChild(OptionBlock), 1);

            return null;
        }).onMouseLeave(component -> {

            executeLater(() -> Optional.ofNullable(OptionBlock.parent).ifPresent(
                    parent -> parent.removeChild(OptionBlock)
                ),
                1
            );
            return null;
        }).onMouseScroll((component, e) -> {
            int d = e.getDelta() > 0 ? -1 : 1;
            withGivenState(currentState + d);
            return null;
        });

        onChanged();
    }

    public UIOption<T> withScope(Collection<T> scope){

        // if(scope.isEmpty())throw new IllegalArgumentException("Scope cannot be empty");

        this.scope = scope.stream().toList();
        float maxLen = 0;

        Options.clear();
        OptionBlock.clearChildren();

        for (T ignored : this.scope) {
            UIWrappedText text = (UIWrappedText)new UIWrappedText()
                    .setX(new PixelConstraint(4f))
                    .setY(new SiblingConstraint(2f))
                    .setColor(Color.WHITE)
                    //.enableEffect(create(Color.RED))
                    .setChildOf(OptionBlock);

            Options.add(text);
            maxLen = Math.max(maxLen, evaluate(formatter.apply(ignored) + " -> "));

        }
        OptionBlock.setWidth(new PixelConstraint(maxLen + 10f));
        try{
            Options.get(0).setY(new PixelConstraint(0f));
        }catch (IndexOutOfBoundsException ignored){}

        setText();
        return this;
    }

    public T currentSelection() {
        return currentSelection;
    }

    public UIOption<T> onChanged(){
        currentSelection = scope.get(currentState);
        this.onSelect.accept(currentSelection);
        setText();
        return this;
    }



    public UIOption<T> withGivenState(int i){
        currentState = Math.floorMod(i, scope.size());
        onChanged();
        return this;
    }

    private void setText(){
        for (int i = 0; i < Options.size(); i++) {
            String text = formatter.apply(scope.get(i));
            String display = i == currentState ? "-> " + text : "  " + text;
            Options.get(i)
                    .setText(display)
                    .setWidth(new RelativeConstraint(1f))
                    .setTextScale(new PixelConstraint(0.8f));
        }
    }

}
