package com.verr1.valkyrienmanager.gui.screens;

import com.verr1.valkyrienmanager.VManagerClient;
import com.verr1.valkyrienmanager.gui.factory.UIFactory;
import com.verr1.valkyrienmanager.manager.VClientManager;
import com.verr1.valkyrienmanager.manager.VManager;
import gg.essential.elementa.ElementaVersion;
import gg.essential.elementa.UIComponent;
import gg.essential.elementa.WindowScreen;
import gg.essential.elementa.components.*;
import gg.essential.elementa.components.input.UITextInput;
import gg.essential.elementa.constraints.*;
import gg.essential.elementa.effects.OutlineEffect;

import java.awt.*;
import java.util.*;
import java.util.List;

public class VMScreen extends WindowScreen {

    private final int MAX_BLOCK_PER_PAGE = 10;

    private static final Set<OutlineEffect.Side> ALL_SIDES = new HashSet<>(List.of(
            OutlineEffect.Side.Top,
            OutlineEffect.Side.Bottom,
            OutlineEffect.Side.Left,
            OutlineEffect.Side.Right
    ));


    private static VClientManager manager(){
        return VManagerClient.manager();
    }

    private static OutlineEffect create(Color color){
        return new OutlineEffect(color, 1, true, true, ALL_SIDES);
    }


    long currentSelection = -1;

    UIComponent main = new UIBlock()
            .setX(new CenterConstraint())
            .setY(new CenterConstraint())
            .setWidth(new RelativeConstraint(0.9f))
            .setHeight(new RelativeConstraint(0.8f))
            .setColor(Color.DARK_GRAY)
            .setChildOf(getWindow());
            //.enableEffect(create(Color.RED));

    UIComponent listBlock = new UIBlock()
            .setX(new RelativeConstraint(0.05f))
            .setY(new CenterConstraint())
            .setWidth(new RelativeConstraint(0.6f))
            .setHeight(new RelativeConstraint(0.9f))
            .setColor(Color.GRAY)
            .setChildOf(main);
            //.enableEffect(create(Color.GREEN));

    UIComponent buttonBlock = new UIBlock()
            .setX(new SiblingConstraint(10f))
            .setY(new CenterConstraint())
            .setWidth(new RelativeConstraint(0.3f))
            .setHeight(new RelativeConstraint(0.9f))
            .setColor(Color.LIGHT_GRAY)
            .setChildOf(main);
            //.enableEffect(create(Color.BLUE));

    UITextInput selectionField = (UITextInput) new UITextInput()
            .setX(new CenterConstraint())
            .setY(new RelativeConstraint(0.05f))
            .setWidth(new RelativeConstraint(0.8f))
            .setHeight(new PixelConstraint(20))
            .setColor(Color.WHITE)
            .setChildOf(buttonBlock)
            //.enableEffect(create(Color.YELLOW))
            .setTextScale(new PixelConstraint(2))
            .onMouseClick(
                    (component, mouseButton) -> {
                        component.grabWindowFocus();
                        return null;
                    }
            );

    UIComponent buttonLine_0 = new UIBlock()
            .setX(new CenterConstraint())
            .setY(new SiblingConstraint(10f))
            .setWidth(new RelativeConstraint(0.9f))
            .setHeight(new ChildBasedSizeConstraint(4f))
            .setColor(Color.DARK_GRAY)
            .setChildOf(buttonBlock);
            //.enableEffect(create(Color.YELLOW));

    UIComponent button_0 = new UIWrappedText()
            .setText("TP WITH CLUSTER")
            .setX(new CenterConstraint())
            .setY(new PixelConstraint(4f))
            .setWidth(new RelativeConstraint(0.9f))
            .setHeight(new PixelConstraint(10f))
            .setTextScale(new PixelConstraint(0.9f))
            .setChildOf(buttonLine_0)
            .onMouseClick(($, $$) -> {
                if(currentSelection != VManager.INVALID_ID){
                    manager().teleportToLocal(currentSelection);
                }
                return null;
            });


    UIComponent button_1 = new UIWrappedText()
            .setText("TOGGLE STATIC")
            .setX(new CenterConstraint())
            .setY(new SiblingConstraint(4f))
            .setWidth(new RelativeConstraint(0.9f))
            .setHeight(new PixelConstraint(10f))
            .setTextScale(new PixelConstraint(0.9f))
            .setChildOf(buttonLine_0)
            .onMouseClick(($, $$) -> {
                if(currentSelection != VManager.INVALID_ID){
                    manager().toggleStatic(currentSelection);
                }
                return null;
            });

    UIComponent button_2 = new UIWrappedText()
            .setText("TOGGLE OWN")
            .setX(new CenterConstraint())
            .setY(new SiblingConstraint(4f))
            .setWidth(new RelativeConstraint(0.9f))
            .setHeight(new PixelConstraint(10f))
            .setTextScale(new PixelConstraint(0.9f))
            .setChildOf(buttonLine_0)
            .onMouseClick(($, $$) -> {
                if(currentSelection != VManager.INVALID_ID){
                    manager().toggleOwn(currentSelection);
                }
                return null;
            });


    UIComponent buttonLine_1 = new UIBlock()
            .setX(new CenterConstraint())
            .setY(new SiblingConstraint(10f))
            .setWidth(new RelativeConstraint(0.9f))
            .setHeight(new ChildBasedSizeConstraint(4f))
            .setColor(Color.DARK_GRAY)
            .setChildOf(buttonBlock);
            //.enableEffect(create(Color.YELLOW));

    UIComponent button_10 = new UIWrappedText()
            .setText("RELOAD")
            .setX(new CenterConstraint())
            .setY(new PixelConstraint(4f))
            .setWidth(new RelativeConstraint(0.9f))
            .setHeight(new PixelConstraint(10f))
            .setTextScale(new PixelConstraint(0.9f))
            .setChildOf(buttonLine_1)
            .onMouseClick(($, $$) -> {
                manager().requestAll();
                return null;
            });


    UIComponent button_11 = new UIWrappedText()
            .setText("REFRESH")
            .setX(new CenterConstraint())
            .setY(new SiblingConstraint(4f))
            .setWidth(new RelativeConstraint(0.9f))
            .setHeight(new PixelConstraint(10f))
            .setTextScale(new PixelConstraint(0.9f))
            .setChildOf(buttonLine_1)
            .onMouseClick(($, $$) -> {
                refresh();
                return null;
            });


    UIComponent list = new ScrollComponent()
            .setX(new CenterConstraint())
            .setY(new CenterConstraint())
            .setWidth(new FillConstraint())
            .setHeight(new FillConstraint())
            .setChildOf(listBlock)
            .enableEffect(create(Color.RED));

    public VMScreen() {
        super(ElementaVersion.V7);

    }


    private void refresh(){
        list.clearChildren();

        for(var key : VManagerClient.CLIENT_VIEW.data().keySet()){
            var entry = VManagerClient.CLIENT_VIEW.get(key);

            var container = new UIBlock()
                    .setX(new CenterConstraint())
                    .setY(new SiblingConstraint(2f))
                    .setWidth(new RelativeConstraint(1f))
                    .setHeight(new ChildBasedSizeConstraint())
                    //.enableEffect(create(Color.YELLOW))
                    .setColor(Color.DARK_GRAY)
                    .setChildOf(list);

            new UIBlock()
                    .setX(new SiblingConstraint(5f))
                    .setY(new PixelConstraint(5f))
                    .setWidth(new PixelConstraint(10))
                    .setHeight(new PixelConstraint(10))
                    .setColor(Color.GRAY)
                    .setChildOf(container)
                    .enableEffect(create(Color.YELLOW))
                    .onMouseClick(
                            (component, mouseButton) -> {
                                currentSelection = key;
                                selectionField.setText(String.valueOf(key));
                                return null;
                            }
                    );


            new TreeListComponent(UIFactory.create(entry))
                    .setX(new SiblingConstraint(5f))
                    .setY(new PixelConstraint(5f))
                    .setWidth(new RelativeConstraint(0.96f))
                    .setHeight(new ChildBasedSizeConstraint())
                    .setChildOf(container)
                    //.enableEffect(create(Color.RED))
                    .onMouseClick(
                            (component, mouseButton) -> {
                                component.grabWindowFocus();
                                return null;
                            }
                    );


        }
    }

    @Override
    public void afterInitialization() {
        super.afterInitialization();
        refresh();
    }


    public class ClientViewBox extends UIContainer {
        private long id;

        UIComponent selection = new UIBlock(Color.BLUE)
                .setX(new PixelConstraint(5f))
                .setY(new PixelConstraint(5f))
                .setWidth(new PixelConstraint(10f))
                .setHeight(new PixelConstraint(10f))
                .enableEffect(create(Color.RED))
                .setChildOf(this).onMouseClick(
                        (component, mouseButton) -> {
                            currentSelection = id;
                            selectionField.setText(String.valueOf(id));
                            return null;
                        }
                );

        ScrollComponent scroll = (ScrollComponent)
                new ScrollComponent()
                        .setX(new RelativeConstraint(5f))
                        .setY(new SiblingConstraint(5f))
                        .setWidth(new FillConstraint())
                        .setHeight(new ChildBasedRangeConstraint())
                        .setChildOf(this)
                        .onMouseClick(
                                (component, mouseButton) -> {
                                    component.grabWindowFocus();
                                    return null;
                                }
                        )
                        .enableEffect(create(Color.GREEN));




        public ClientViewBox(long id) {
            this.id = id;

        }

        void apply(List<String> lines){
            lines.forEach(
                    line -> new UIWrappedText(line)
                            .setX(new CenterConstraint())
                            .setY(new SiblingConstraint(5f))
                            .setWidth(new RelativeConstraint(0.96f))
                            .setHeight(new PixelConstraint(10))
                            .enableEffect(create(Color.BLUE))
                            .setChildOf(scroll)
            );
        }

    }





}
