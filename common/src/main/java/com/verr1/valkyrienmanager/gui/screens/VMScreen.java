package com.verr1.valkyrienmanager.gui.screens;

import com.verr1.valkyrienmanager.VManagerClient;
import com.verr1.valkyrienmanager.foundation.data.VTag;
import com.verr1.valkyrienmanager.gui.factory.FilterGenerator;
import com.verr1.valkyrienmanager.gui.factory.UIFactory;
import com.verr1.valkyrienmanager.gui.widgets.UIButton;
import com.verr1.valkyrienmanager.gui.widgets.UIOption;
import com.verr1.valkyrienmanager.manager.VClientManager;
import com.verr1.valkyrienmanager.manager.db.general.item.NetworkKey;
import com.verr1.valkyrienmanager.manager.db.general.item.VItem;
import com.verr1.valkyrienmanager.manager.db.snapshot.item.SnapShotConfig;
import gg.essential.elementa.ElementaVersion;
import gg.essential.elementa.UIComponent;
import gg.essential.elementa.WindowScreen;
import gg.essential.elementa.components.*;
import gg.essential.elementa.components.Window;
import gg.essential.elementa.components.input.UITextInput;
import gg.essential.elementa.constraints.*;
import gg.essential.elementa.effects.OutlineEffect;
import kotlin.Pair;
import org.joml.Vector3d;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.verr1.valkyrienmanager.gui.factory.FilterGenerator.generate;

public class VMScreen extends WindowScreen {

    private static VClientManager manager(){
        return VManagerClient.manager();
    }

    UIComponent main = new UIBlock()
            .setX(new CenterConstraint())
            .setY(new CenterConstraint())
            .setWidth(new RelativeConstraint(0.9f))
            .setHeight(new RelativeConstraint(0.96f))
            .setColor(Color.DARK_GRAY)
            .setChildOf(getWindow());
    //.enableEffect(create(Color.RED));

    UIComponent dataContainer = new UIBlock()
            .setX(new RelativeConstraint(0.05f))
            .setY(new CenterConstraint())
            .setWidth(new RelativeConstraint(0.8f))
            .setHeight(new RelativeConstraint(0.9f))
            .setColor(Color.GRAY)
            .setChildOf(main);

    UIComponent dataBlock = new ScrollComponent()
            .setX(new PixelConstraint(0f))
            .setY(new CenterConstraint())
            .setWidth(new RelativeConstraint(1f))
            .setHeight(new RelativeConstraint(1f))
            .setChildOf(dataContainer);
    //.enableEffect(create(Color.GREEN));

    UIComponent controlBlock = new UIBlock()
            .setX(new SiblingConstraint(10f))
            .setY(new CenterConstraint())
            .setWidth(new PixelConstraint(15f))
            .setHeight(new RelativeConstraint(0.9f))
            .setColor(Color.LIGHT_GRAY)
            .setChildOf(main);

    UIComponent reloadButton = new UIButton(getWindow())
            .setTooltips(List.of("Reload Client"))
            .setColor(new Color(0xAF00AF33, true))
            .setX(new CenterConstraint())
            .setY(new PixelConstraint(15f))
            .setWidth(new PixelConstraint(10f))
            .setHeight(new PixelConstraint(10f))
            .setChildOf(controlBlock)
            .onMouseClick(($, $$) -> {
                manager().requestAll();
                manager().requestSnapConfig();
                VManagerClient.CLIENT_EXECUTOR.executeLater(this::refreshAll, 10);
                return null;
            });

    UIComponent refreshButton = new UIButton(getWindow())
            .setTooltips(List.of("Refresh Client"))
            .setColor(new Color(0xAF00AF33, true))
            .setX(new CenterConstraint())
            .setY(new SiblingConstraint(4f))
            .setWidth(new PixelConstraint(10f))
            .setHeight(new PixelConstraint(10f))
            .onMouseClickConsumer($ -> {
                refreshAll();
            })
            .setChildOf(controlBlock);

    public static void executeLater(Runnable task, int tick){
        VManagerClient.CLIENT_EXECUTOR.executeLater(task, tick);
    }

    private static final Set<OutlineEffect.Side> ALL_SIDES = new HashSet<>(List.of(
            OutlineEffect.Side.Top,
            OutlineEffect.Side.Bottom,
            OutlineEffect.Side.Left,
            OutlineEffect.Side.Right
    ));

    public static OutlineEffect create(Color color){
        return new OutlineEffect(color, 1, true, true, ALL_SIDES);
    }

    Filter filter() {
        return filter;
    }

    Filter filter = (Filter)new Filter(
            getWindow(),
            this::refresh,
            () -> manager().playerPos()
    )
            .setWidth(new RelativeConstraint(0.4f))
            .setHeight(new ChildBasedSizeConstraint())
            .enableEffect(create(Color.BLUE.darker()));

    UIComponent filterWindowButton = new UIButton(getWindow())
            .setTooltips(List.of("Filter Window"))
            .setColor(new Color(0xAF00AF33, true))
            .setX(new CenterConstraint())
            .setY(new SiblingConstraint(4f))
            .setWidth(new PixelConstraint(10f))
            .setHeight(new PixelConstraint(10f))
            .setChildOf(controlBlock)
            .onMouseClick(($, $$) -> {
                Optional.ofNullable(filter().parent).ifPresent(
                        p -> p.removeChild(filter())
                );

                filter()
                    .setX(new CenterConstraint())
                    .setY(new CenterConstraint())
                    .setChildOf(getWindow());
                return null;
            });


    Command command() {
        return command;
    }

    Command command = (Command)new Command(
            getWindow(),
            this::currentSelection,
            () -> {}
    )
            .setWidth(new PixelConstraint(160f))
            .setHeight(new ChildBasedSizeConstraint())
            .enableEffect(create(Color.BLUE.darker()));

    UIComponent commandWindowButton = new UIButton(getWindow())
            .setTooltips(List.of("Command Window"))
            .setColor(new Color(0xAF00AF33, true))
            .setX(new CenterConstraint())
            .setY(new SiblingConstraint(4f))
            .setWidth(new PixelConstraint(10f))
            .setHeight(new PixelConstraint(10f))
            .setChildOf(controlBlock)
            .onMouseClick(($, $$) -> {
                Optional.ofNullable(command().parent).ifPresent(
                        p -> p.removeChild(command())
                );

                command()
                        .setX(new CenterConstraint())
                        .setY(new CenterConstraint())
                        .setChildOf(getWindow());
                return null;
            });

    Config config() {
        return config;
    }

    Config config = (Config) new Config(
            getWindow()
    )
            .setWidth(new PixelConstraint(260f))
            .setHeight(new ChildBasedSizeConstraint())
            .enableEffect(create(Color.BLUE.darker()));

    UIComponent configWindowButton = new UIButton(getWindow())
            .setTooltips(List.of("Config Window"))
            .setColor(new Color(0xAF00AF33, true))
            .setX(new CenterConstraint())
            .setY(new SiblingConstraint(4f))
            .setWidth(new PixelConstraint(10f))
            .setHeight(new PixelConstraint(10f))
            .setChildOf(controlBlock)
            .onMouseClick(($, $$) -> {
                Optional.ofNullable(config().parent).ifPresent(
                        p -> p.removeChild(config())
                );

                config()
                        .setX(new CenterConstraint())
                        .setY(new CenterConstraint())
                        .setChildOf(getWindow());
                return null;
            });

    public long currentSelection() {
        return currentSelection;
    }

    private long currentSelection = -1L;

    public VMScreen() {
        super(ElementaVersion.V7);
    }


    @Override
    public void afterInitialization() {
        super.afterInitialization();
        refresh();
    }

    private void refreshAll(){
        command().onChanged();
        config().onChanged();
        refresh(filter().make());
    }

    private void refresh(){
        dataBlock.clearChildren();
        VManagerClient.CLIENT_VIEW
                .data()
                .entrySet()
                .stream()
                .map(e -> new Pair<>("", Set.of(e)))
                .forEach(this::addCard);
    }

    private void refresh(Function<Set<Map.Entry<Long, VItem>>, Stream<Pair<String, Set<Map.Entry<Long, VItem>>>>> converter){
        dataBlock.clearChildren();
        converter.apply(
                VManagerClient
                        .CLIENT_VIEW
                        .data()
                        .entrySet())
                .forEach(this::addCard);
    }

    private void addCard(Pair<String, Set<Map.Entry<Long, VItem>>> s){

        UIComponent cardGroup = new UIBlock()
                .setX(new PixelConstraint(0f))
                .setY(new SiblingConstraint(5f))
                .setWidth(new RelativeConstraint(0.96f))
                .setHeight(new ChildBasedSizeConstraint())
                .setColor(Color.LIGHT_GRAY)
                .setChildOf(dataBlock);

        if(!s.getFirst().isEmpty()){
            UIComponent groupTitle = new UIWrappedText()
                    .setText(s.getFirst())
                    .setX(new CenterConstraint())
                    .setY(new SiblingConstraint(5f))
                    .setWidth(new RelativeConstraint(0.96f))
                    .setHeight(new PixelConstraint(10f))
                    .setColor(Color.WHITE)
                    .setChildOf(cardGroup);
        }


        s.getSecond().forEach(entry -> {
            new VCard(
                    entry.getValue(),
                    getWindow()
            )
                    .withSelectorConfig(sel -> sel.onMouseClick(($, $$) -> {
                        currentSelection = entry.getKey();
                        command.onChanged();
                        return null;
                    }))
                    .setX(new CenterConstraint())
                    .setY(new SiblingConstraint(0f))
                    .setWidth(new RelativeConstraint(0.96f))
                    .setHeight(new ChildBasedSizeConstraint())
                    .setColor(Color.DARK_GRAY)
                    .setChildOf(cardGroup);
        });

    }


    static class VCard extends UIBlock{


        final UIButton selector;
        final TreeListComponent content;

        VCard(VItem vdata, Window main){
            super();
            selector = (UIButton) new UIButton(main)
                    .setX(new SiblingConstraint(5f))
                    .setY(new PixelConstraint(5))
                    .setWidth(new PixelConstraint(10))
                    .setHeight(new PixelConstraint(10))
                    .setColor(Color.GRAY)
                    .setChildOf(this)
            ;
            content = (TreeListComponent)new TreeListComponent(UIFactory.create(vdata))
                    .setX(new SiblingConstraint(5f))
                    .setY(new CenterConstraint())
                    .setWidth(new RelativeConstraint(0.96f))
                    .setHeight(new ChildBasedSizeConstraint())
                    .setChildOf(this).onMouseClick(
                            (component, mouseButton) -> {
                                component.grabWindowFocus();
                                return null;
                            }
                    );
        }

        public VCard withSelectorConfig(Consumer<UIBlock> config){
            config.accept(selector);
            return this;
        }

    }


    static class Config extends Draggable {
        SnapShotConfig.ReplaceMode replaceMode = SnapShotConfig.ReplaceMode.NO_NEGLECTED;
        SnapShotConfig.PlaceMode placeMode = SnapShotConfig.PlaceMode.ALL;

        UIOption<String> tagRemoveSelect;
        UITextInput tagInput;


        UITextInput re_placeInput;
        UIOption<String> replaceBlackListRemoveSelect;
        UIOption<String> placeBlackListRemoveSelect;



        @SuppressWarnings("unchecked")
        public Config(UIComponent main){
            super(main);
            float lWidth = 140f;
            UIComponent replaceOptContainer = new UIBlock()
                    .setX(new PixelConstraint(2f))
                    .setY(new SiblingConstraint(2f))
                    .setColor(new InheritedColorConstraint())
                    .setWidth(new ChildBasedSizeConstraint())
                    .setHeight(new PixelConstraint(10f))
                    .setChildOf(content());

                    UIWrappedText replaceOptText = (UIWrappedText)new UIWrappedText()
                            .setText("Replace Mode: ")
                            .setX(new SiblingConstraint())
                            .setY(new CenterConstraint())
                            .setWidth(new PixelConstraint(lWidth))
                            .setHeight(new RelativeConstraint(1f))
                            .setColor(Color.WHITE)
                            .setChildOf(replaceOptContainer);

                    UIOption<SnapShotConfig.ReplaceMode> replaceOption = (UIOption<SnapShotConfig.ReplaceMode>) new UIOption<>(
                            List.of(SnapShotConfig.ReplaceMode.values()),
                            SnapShotConfig.ReplaceMode::name,
                            ctx -> {
                                this.replaceMode = ctx;
                                Optional.ofNullable(manager()).ifPresent(m -> m.handleReplaceConfig(ctx));
                                replaceOptText.setText("Replace: " + this.replaceMode);
                            },
                            main
                    )
                            .setColor(Color.RED.darker().darker())
                            .setX(new SiblingConstraint(2f))
                            .setY(new CenterConstraint())
                            .setWidth(new PixelConstraint(10f))
                            .setHeight(new PixelConstraint(10f))
                            .setChildOf(replaceOptContainer);

            UIComponent placeOptContainer = new UIBlock()
                    .setX(new PixelConstraint(2f))
                    .setY(new SiblingConstraint(2f))
                    .setColor(new InheritedColorConstraint())
                    .setWidth(new ChildBasedSizeConstraint())
                    .setHeight(new PixelConstraint(10f))
                    .setChildOf(content());

                    UIWrappedText placeOptText = (UIWrappedText)new UIWrappedText()
                            .setText("Place Mode: ")
                            .setX(new SiblingConstraint())
                            .setY(new CenterConstraint())
                            .setWidth(new PixelConstraint(lWidth))
                            .setHeight(new RelativeConstraint(1f))
                            .setColor(Color.WHITE)
                            .setChildOf(placeOptContainer);

                    UIOption<SnapShotConfig.PlaceMode> placeOption = (UIOption<SnapShotConfig.PlaceMode>) new UIOption<>(
                            List.of(SnapShotConfig.PlaceMode.values()),
                            SnapShotConfig.PlaceMode::name,
                            ctx -> {
                                this.placeMode = ctx;
                                Optional.ofNullable(manager()).ifPresent(m -> m.handlePlaceConfig(ctx));
                                placeOptText.setText("Place: " + this.placeMode);
                            },
                            main
                    )
                            .setColor(Color.RED.darker().darker())
                            .setX(new SiblingConstraint(2f))
                            .setY(new CenterConstraint())
                            .setWidth(new PixelConstraint(10f))
                            .setHeight(new PixelConstraint(10f))
                            .setChildOf(placeOptContainer);

            UIComponent tagContainer = new UIBlock()
                    .setX(new PixelConstraint(2f))
                    .setY(new SiblingConstraint(2f))
                    .setColor(new InheritedColorConstraint())
                    .setWidth(new ChildBasedSizeConstraint())
                    .setHeight(new PixelConstraint(10f))
                    .setChildOf(content());

                    tagInput = (UITextInput) new UITextInput()
                            .setX(new SiblingConstraint())
                            .setY(new CenterConstraint())
                            .setWidth(new PixelConstraint(lWidth))
                            .setHeight(new RelativeConstraint(1f))
                            .setChildOf(tagContainer)
                            .enableEffect(create(Color.BLACK))
                            .onMouseClick(
                                    (c, e) -> {
                                        c.grabWindowFocus();
                                        return null;
                                    }
                            );

                    tagRemoveSelect = (UIOption<String>) new UIOption<>(
                            List.of(""),
                            s -> s,
                            tagInput::setText,
                            main
                    )
                            .setColor(Color.RED.darker().darker())
                            .setX(new SiblingConstraint(4f))
                            .setY(new CenterConstraint())
                            .setWidth(new PixelConstraint(10f))
                            .setHeight(new PixelConstraint(10f))
                            .setChildOf(tagContainer);

                    UIComponent tagRemoveButton = new UIButton("-", main)
                            .setTooltips(List.of("Remove Tag"))
                            .setColor(Color.RED.darker())
                            .setX(new SiblingConstraint(4f))
                            .setY(new CenterConstraint())
                            .setWidth(new PixelConstraint(10f))
                            .setHeight(new PixelConstraint(10f))
                            .onMouseClickConsumer($ -> handleRemoveTag())
                            .setChildOf(tagContainer);



                    UIComponent tagAddButton = new UIButton("+", main)
                            .setTooltips(List.of("Add Tag"))
                            .setColor(Color.YELLOW.darker())
                            .setX(new SiblingConstraint(4f))
                            .setY(new CenterConstraint())
                            .setWidth(new PixelConstraint(10f))
                            .setHeight(new PixelConstraint(10f))
                            .onMouseClickConsumer($ -> handleAddTag())
                            .setChildOf(tagContainer);

            UIComponent re_placeContainer = new UIBlock()
                    .setX(new PixelConstraint(2f))
                    .setY(new SiblingConstraint(2f))
                    .setColor(new InheritedColorConstraint())
                    .setWidth(new ChildBasedSizeConstraint())
                    .setHeight(new PixelConstraint(10f))
                    .setChildOf(content());

                    re_placeInput = (UITextInput) new UITextInput()
                            .setX(new SiblingConstraint())
                            .setY(new CenterConstraint())
                            .setWidth(new PixelConstraint(lWidth))
                            .setHeight(new RelativeConstraint(1f))
                            .setChildOf(re_placeContainer)
                            .enableEffect(create(Color.BLACK))
                            .onMouseClick(
                                    (c, e) -> {
                                        c.grabWindowFocus();
                                        return null;
                                    }
                            );

                    replaceBlackListRemoveSelect = (UIOption<String>) new UIOption<>(
                            List.of(""),
                            s -> s,
                            re_placeInput::setText,
                            main
                    )
                            .setColor(Color.RED.darker().darker())
                            .setX(new SiblingConstraint(4f))
                            .setY(new CenterConstraint())
                            .setWidth(new PixelConstraint(10f))
                            .setHeight(new PixelConstraint(10f))
                            .setChildOf(re_placeContainer);



                    UIComponent replaceBlackListRemoveButton = new UIButton("-", main)
                            .setTooltips(List.of("Remove Replace Neglect"))
                            .setColor(Color.RED.darker())
                            .setX(new SiblingConstraint(4f))
                            .setY(new CenterConstraint())
                            .setWidth(new PixelConstraint(10f))
                            .setHeight(new PixelConstraint(10f))
                            .onMouseClickConsumer($ -> handleRemoveReplace())
                            .setChildOf(re_placeContainer);


                    UIComponent replaceBlackListAddButton = new UIButton("+", main)
                            .setTooltips(List.of("Add Replace Neglect"))
                            .setColor(Color.YELLOW.darker())
                            .setX(new SiblingConstraint(4f))
                            .setY(new CenterConstraint())
                            .setWidth(new PixelConstraint(10f))
                            .setHeight(new PixelConstraint(10f))
                            .onMouseClickConsumer($ -> handleAddReplace())
                            .setChildOf(re_placeContainer);

                    placeBlackListRemoveSelect = (UIOption<String>) new UIOption<>(
                            List.of(""),
                            s -> s,
                            re_placeInput::setText,
                            main
                    )
                            .setColor(Color.RED.darker().darker())
                            .setX(new SiblingConstraint(4f))
                            .setY(new CenterConstraint())
                            .setWidth(new PixelConstraint(10f))
                            .setHeight(new PixelConstraint(10f))
                            .setChildOf(re_placeContainer);

                    UIComponent placeBlackListRemoveButton = new UIButton("-", main)
                            .setTooltips(List.of("Remove Place Neglect"))
                            .setColor(Color.RED.darker())
                            .setX(new SiblingConstraint(4f))
                            .setY(new CenterConstraint())
                            .setWidth(new PixelConstraint(10f))
                            .setHeight(new PixelConstraint(10f))
                            .onMouseClickConsumer($ -> handleRemovePlace())
                            .setChildOf(re_placeContainer);


                    UIComponent placeBlackListAddButton = new UIButton("+", main)
                            .setTooltips(List.of("Add Place Neglect"))
                            .setColor(Color.YELLOW.darker())
                            .setX(new SiblingConstraint(4f))
                            .setY(new CenterConstraint())
                            .setWidth(new PixelConstraint(10f))
                            .setHeight(new PixelConstraint(10f))
                            .onMouseClickConsumer($ -> handleAddPlace())
                            .setChildOf(re_placeContainer);
            onChanged();
        }



        public void handleAddTag(){
            String tag = tagInput.getText();
            manager().handleTag(tag, true);
            manager().requestAll();
            executeLater(this::onChanged, 10);
        }

        public void handleRemoveTag(){
            String tag = tagInput.getText();
            manager().handleTag(tag, false);
            manager().requestAll();
            executeLater(this::onChanged, 10);
        }

        public void handleAddReplace(){
            String tag = re_placeInput.getText();
            manager().handleReplaceConfig(tag, true);
            manager().requestSnapConfig();
            executeLater(this::onChanged, 10);
        }

        public void handleRemoveReplace(){
            String tag = re_placeInput.getText();
            manager().handleReplaceConfig(tag, false);
            manager().requestSnapConfig();
            executeLater(this::onChanged, 10);
        }

        public void handleAddPlace(){
            String tag = re_placeInput.getText();
            manager().handlePlaceConfig(tag, true);
            manager().requestSnapConfig();
            executeLater(this::onChanged, 10);
        }

        public void handleRemovePlace(){
            String tag = re_placeInput.getText();
            manager().handlePlaceConfig(tag, false);
            manager().requestSnapConfig();
            executeLater(this::onChanged, 10);
        }

        public void onChanged(){

            Set<String> allTags = VManagerClient.CLIENT_VIEW.vtags().values().stream().map(VTag::name).collect(Collectors.toSet());

            Set<String> snapPlaceBlackList = VManagerClient.SNAP_CLIENT_VIEW.snapConfig().placeBlackList;
            Set<String> snapReplaceWhiteList = VManagerClient.SNAP_CLIENT_VIEW.snapConfig().replaceBlackList;

            tagRemoveSelect.withScope(allTags);
            replaceBlackListRemoveSelect.withScope(snapReplaceWhiteList);
            placeBlackListRemoveSelect.withScope(snapPlaceBlackList);


        }

    }

    static class Command extends Draggable{

        enum ModifyLevel{
            CLUSTER, SELECTED
        }
        ModifyLevel modifyLevel = ModifyLevel.CLUSTER;

        UITextInput nameInput;
        UIOption<String> tagRemoveSelect;
        UIOption<String> tagAddSelect;
        UIWrappedText tagInput;
        Supplier<Long> selector;
        UIWrappedText selectionDisplay;
        Runnable refreshCallback;

        @SuppressWarnings("unchecked")
        public Command(UIComponent main, Supplier<Long> selector, Runnable refreshCallback) {
            super(main);

            float lWidth = 90f;
            this.refreshCallback = refreshCallback;
            this.selector = selector;

            selectionDisplay = (UIWrappedText)new UIWrappedText()
                    .setColor(Color.WHITE)
                    .setX(new PixelConstraint(2f))
                    .setY(new PixelConstraint(2f))
                    .setWidth(new PixelConstraint(lWidth))
                    .setHeight(new PixelConstraint(10f))
                    .setChildOf(content());

            UIComponent levelContainer = new UIBlock()
                    .setX(new PixelConstraint(2f))
                    .setY(new SiblingConstraint(2f))
                    .setColor(new InheritedColorConstraint())
                    .setWidth(new ChildBasedSizeConstraint())
                    .setHeight(new PixelConstraint(10f))
                    .setChildOf(content());

                    UIWrappedText levelText = (UIWrappedText)new UIWrappedText()
                            .setText("Modify Level")
                            .setX(new SiblingConstraint())
                            .setY(new CenterConstraint())
                            .setWidth(new PixelConstraint(lWidth))
                            .setHeight(new RelativeConstraint(1f))
                            .setColor(Color.WHITE)
                            .setChildOf(levelContainer);

                    UIOption<ModifyLevel> modifyLevel = (UIOption<ModifyLevel>) new UIOption<>(
                            List.of(ModifyLevel.values()),
                            ModifyLevel::name,
                            ctx -> {
                                this.modifyLevel = ctx;
                                levelText.setText("Modify: " + this.modifyLevel);
                            },
                            main
                    )
                            .setColor(Color.RED.darker().darker())
                            .setX(new SiblingConstraint(2f))
                            .setY(new CenterConstraint())
                            .setWidth(new PixelConstraint(10f))
                            .setHeight(new PixelConstraint(10f))
                            .setChildOf(levelContainer);

            UIComponent tp = new UIButton("TP With Cluster", main)
                    .setColor(Color.GREEN.darker())
                    .setX(new PixelConstraint(2f))
                    .setY(new SiblingConstraint(2f))
                    .setWidth(new PixelConstraint(lWidth))
                    .setHeight(new PixelConstraint(10f))
                    .setChildOf(content())
                    .onMouseClickConsumer($ -> handleTeleport());


            UIComponent toggle_own = new UIButton("Own / Abandon", main)
                    .setColor(Color.GREEN.darker())
                    .setX(new PixelConstraint(2f))
                    .setY(new SiblingConstraint(2f))
                    .setWidth(new PixelConstraint(lWidth))
                    .setHeight(new PixelConstraint(10f))
                    .setChildOf(content())
                    .onMouseClickConsumer($ -> handleOwn());

            UIComponent toggle_static = new UIButton("Toggle Static", main)
                    .setColor(Color.GREEN.darker())
                    .setX(new PixelConstraint(2f))
                    .setY(new SiblingConstraint(2f))
                    .setWidth(new PixelConstraint(lWidth))
                    .setHeight(new PixelConstraint(10f))
                    .setChildOf(content())
                    .onMouseClickConsumer($ -> handleStatic());

            UIComponent nameContainer = new UIBlock()
                    .setX(new PixelConstraint(2f))
                    .setY(new SiblingConstraint(2f))
                    .setColor(new InheritedColorConstraint())
                    .setWidth(new ChildBasedSizeConstraint())
                    .setHeight(new PixelConstraint(10f))
                    .setChildOf(content());

                    nameInput = (UITextInput) new UITextInput()
                            .setX(new SiblingConstraint())
                            .setY(new CenterConstraint())
                            .setWidth(new PixelConstraint(lWidth))
                            .setHeight(new RelativeConstraint(1f))
                            .setChildOf(nameContainer)
                            .enableEffect(create(Color.BLACK))
                            .onMouseClick(
                                    (c, e) -> {
                                        c.grabWindowFocus();
                                        return null;
                                    }
                            );

                    UIButton confirmRename = (UIButton) new UIButton("R", main)
                            .setTooltips(List.of("Rename"))
                            .setColor(Color.YELLOW.darker())
                            .setX(new SiblingConstraint(4f))
                            .setY(new CenterConstraint())
                            .setWidth(new PixelConstraint(10f))
                            .setHeight(new PixelConstraint(10f))
                            .onMouseClickConsumer($ -> handleRename())
                            .setChildOf(nameContainer);


            UIComponent tagContainer = new UIBlock()
                    .setX(new PixelConstraint(2f))
                    .setY(new SiblingConstraint(2f))
                    .setColor(new InheritedColorConstraint())
                    .setWidth(new ChildBasedSizeConstraint())
                    .setHeight(new PixelConstraint(10f))
                    .setChildOf(content());

                    tagInput = (UIWrappedText) new UIWrappedText()
                            .setX(new SiblingConstraint())
                            .setY(new CenterConstraint())
                            .setWidth(new PixelConstraint(lWidth))
                            .setHeight(new RelativeConstraint(1f))
                            .setChildOf(tagContainer)
                            .enableEffect(create(Color.BLACK));

                    tagRemoveSelect = (UIOption<String>) new UIOption<>(
                            List.of(""),
                            s -> s,
                            tagInput::setText,
                            main
                    )
                            .setColor(Color.RED.darker().darker())
                            .setX(new SiblingConstraint(4f))
                            .setY(new CenterConstraint())
                            .setWidth(new PixelConstraint(10f))
                            .setHeight(new PixelConstraint(10f))
                            .setChildOf(tagContainer);

                    UIComponent tagRemoveButton = new UIButton("-", main)
                            .setTooltips(List.of("Remove Tag"))
                            .setColor(Color.RED.darker())
                            .setX(new SiblingConstraint(4f))
                            .setY(new CenterConstraint())
                            .setWidth(new PixelConstraint(10f))
                            .setHeight(new PixelConstraint(10f))
                            .onMouseClickConsumer($ -> handleRemove())
                            .setChildOf(tagContainer);


                    tagAddSelect = (UIOption<String>) new UIOption<>(
                            List.of(""),
                            s -> s,
                            tagInput::setText,
                            main
                    )
                            .setColor(Color.RED.darker().darker())
                            .setX(new SiblingConstraint(4f))
                            .setY(new CenterConstraint())
                            .setWidth(new PixelConstraint(10f))
                            .setHeight(new PixelConstraint(10f))
                            .setChildOf(tagContainer);

                    UIComponent tagAddButton = new UIButton("+", main)
                            .setTooltips(List.of("Add Tag"))
                            .setColor(Color.YELLOW.darker())
                            .setX(new SiblingConstraint(4f))
                            .setY(new CenterConstraint())
                            .setWidth(new PixelConstraint(10f))
                            .setHeight(new PixelConstraint(10f))
                            .onMouseClickConsumer($ -> handleAdd())
                            .setChildOf(tagContainer);


            onChanged();
        }

        public void onChanged(){
            Set<String> tags = VManagerClient.CLIENT_VIEW
                    .getOptional(selector.get()).map(
                            vItem -> vItem.get(NetworkKey.VTAG).view().raw.stream().map(VTag::name).collect(Collectors.toSet())
                    )
                    .orElse(Set.of());

            Set<String> allTags = VManagerClient.CLIENT_VIEW.vtags().values().stream().map(VTag::name).collect(Collectors.toSet());

            Set<String> notIncludeTags = allTags.stream().filter(t -> !tags.contains(t)).collect(Collectors.toSet());

            selectionDisplay.setText("Selected: " + selector.get());
            tagRemoveSelect.withScope(tags);
            tagAddSelect.withScope(notIncludeTags);

            String slug = VManagerClient.CLIENT_VIEW
                    .getOptional(selector.get())
                    .map(vItem -> vItem.get(NetworkKey.SLUG).view())
                    .orElse("");

            nameInput.setText(slug);

            refreshCallback.run();
        }

        public void handleAdd(){
            long id = selector.get();
            String tag = tagInput.getText();
            manager().handleTag(id, tag, true, modifyLevel == ModifyLevel.CLUSTER);
            manager().requestAll(id);
            executeLater(this::onChanged, 10);
        }

        public void handleRemove(){
            long id = selector.get();
            String tag = tagInput.getText();
            manager().handleTag(id, tag, false, modifyLevel == ModifyLevel.CLUSTER);
            manager().requestAll(id);
            executeLater(this::onChanged, 10);
        }

        public void handleRename(){
            long id = selector.get();
            String name = nameInput.getText();
            manager().rename(id, name, modifyLevel == ModifyLevel.CLUSTER);
            manager().requestAll(id);
            executeLater(this::onChanged, 10);
        }

        public void handleOwn(){
            manager().toggleOwn(selector.get());
            executeLater(this::onChanged, 10);
        }

        public void handleStatic(){
            manager().toggleStatic(selector.get());
            executeLater(this::onChanged, 10);
        }

        public void handleTeleport(){
            manager().teleportToLocal(selector.get());
            executeLater(this::onChanged, 10);
        }

    }

    static class Filter extends Draggable{

        UIComponent searchBarContainer = new UIBlock()
                .setX(new PixelConstraint(4f))
                .setY(new PixelConstraint(2f))
                .setColor(Color.DARK_GRAY)
                .setWidth(new RelativeConstraint(0.8f))
                .setHeight(new PixelConstraint(10f))
                .setChildOf(content());


        UITextInput searchBar = (UITextInput)new UITextInput()
                .setX(new SiblingConstraint())
                .setY(new PixelConstraint(0f))
                .setWidth(new RelativeConstraint(1f))
                .setHeight(new RelativeConstraint(1f))
                .setChildOf(searchBarContainer)
                .setTextScale(new PixelConstraint(0.95f))
                .enableEffect(create(Color.BLACK))
                .onMouseClick(
                        (component, clickEvent) -> {
                            component.grabWindowFocus();
                            return null;
                        }
                );



        private<T extends Enum<?>> void createOptions(UIComponent root, String title, Class<T> enumClass, Consumer<T> receiver){
            UIComponent newLine = new UIBlock(new InheritedColorConstraint())
                    .setX(new PixelConstraint(4f))
                    .setY(new SiblingConstraint(2f))
                    .setWidth(new RelativeConstraint(0.9f))
                    .setHeight(new PixelConstraint(10f))
                    .setChildOf(content());



            UIWrappedText newText = (UIWrappedText)new UIWrappedText()
                    .setText(title)
                    .setX(new SiblingConstraint())
                    .setY(new CenterConstraint())
                    .setWidth(new RelativeConstraint(0.9f))
                    .setHeight(new RelativeConstraint(1f))
                    .setChildOf(newLine);

            UIComponent searchOption = new UIOption<>(
                    Arrays.stream(enumClass.getEnumConstants()).toList(),
                    Enum::name,
                    ctx -> {
                        newText.setText(title + ctx.name());
                        receiver.accept(ctx);
                    },
                    root
            )
                    .setX(new SiblingConstraint(2f))
                    .setY(new CenterConstraint())
                    .setWidth(new PixelConstraint(10f))
                    .setHeight(new PixelConstraint(10f))
                    .setChildOf(newLine);
        }

        UIButton apply;


        FilterGenerator.SearchContext searchContext = FilterGenerator.SearchContext.ID;
        FilterGenerator.GroupContext groupContext = FilterGenerator.GroupContext.BY_CLUSTER;
        FilterGenerator.GroupPolicy groupPolicy = FilterGenerator.GroupPolicy.MAX;
        FilterGenerator.MatchContext matchContext = FilterGenerator.MatchContext.PARTIAL;
        FilterGenerator.SortContext sortContext = FilterGenerator.SortContext.NONE;


        Supplier<Vector3d> playerPosGetter;

        Filter(Window main,
               Consumer<
                        Function<
                                Set<Map.Entry<Long, VItem>>,
                                Stream<
                                        Pair<
                                                String,
                                                Set<Map.Entry<Long, VItem>>>
                                        >
                                >
                        > onApply,
               Supplier<Vector3d> playerPos
        ){

            super(main);
            this.playerPosGetter = playerPos;

            createOptions(main, "Search : ", FilterGenerator.SearchContext.class, ctx -> {
                searchContext = ctx;
            });
            createOptions(main, "Group : ", FilterGenerator.GroupContext.class, ctx -> {
                groupContext = ctx;
            });
            createOptions(main, "Metric : ", FilterGenerator.GroupPolicy.class, ctx -> {
                groupPolicy = ctx;
            });
            createOptions(main, "Match : ", FilterGenerator.MatchContext.class, ctx -> {
                matchContext = ctx;
            });
            createOptions(main, "Sort : ", FilterGenerator.SortContext.class, ctx -> {
                sortContext = ctx;
            });

            apply = (UIButton) new UIButton(main)
                    .withOnMouseRelease(() -> onApply.accept(generate(makeContext())))
                    .setTooltips(List.of("confirm"))
                    .setColor(Color.ORANGE)
                    .setChildOf(searchBarContainer)
                    .setX(new SiblingConstraint(2f))
                    .setY(new PixelConstraint(0f))
                    .setWidth(new PixelConstraint(20f))
                    .setHeight(new PixelConstraint(10f))
                    .enableEffect(create(Color.RED))
                    ;


        }

        public Function<Set<Map.Entry<Long, VItem>>, Stream<Pair<String, Set<Map.Entry<Long, VItem>>>>> make(){
            return generate(makeContext());
        }

        FilterGenerator.Context makeContext(){
            Vector3d pos = playerPosGetter.get();
            return new FilterGenerator.Context(
                    matchContext,
                    searchContext,
                    sortContext,
                    groupContext,
                    groupPolicy,
                    pos,
                    Arrays.stream(searchBar.getText().split(" ")).toList()
            );
        }

    }

    static class Draggable extends UIBlock {
        boolean isDragged = false;

        public Pair<Float, Float> dragOffset() {
            return dragOffset;
        }

        Pair<Float, Float> dragOffset = new Pair<>(0f, 0f);

        private final UIComponent topBar = new UIBlock(Color.BLUE.darker())
                .setX(new RelativeConstraint(0f))
                .setWidth(new RelativeConstraint(1f))
                .setHeight(new PixelConstraint(10f))
                .setChildOf(this)
                .onMouseClick(
                        (c, e) -> {
                            isDragged = true;
                            dragOffset = new Pair<>(e.getAbsoluteX(), e.getAbsoluteY());
                            return null;
                        }
                ).onMouseRelease(
                        c -> {
                            isDragged = false;
                            return null;
                        }
                ).onMouseDrag(
                        (component, x, y, integer) -> {
                            if(!isDragged)return null;

                            float abs_x = x + component.getLeft();
                            float abs_y = y + component.getTop();

                            float del_x = abs_x - dragOffset().component1();
                            float del_y = abs_y - dragOffset().component2();

                            dragOffset = new Pair<>(abs_x, abs_y);

                            float new_x = this.getLeft() + del_x;
                            float new_y = this.getTop() + del_y;

                            this
                                    .setX(new PixelConstraint(new_x))
                                    .setY(new PixelConstraint(new_y));


                            return null;
                        }
                );;

        public UIComponent content() {
            return content;
        }

        private final UIComponent content = new UIBlock(Color.DARK_GRAY.darker())
                .setX(new RelativeConstraint(0f))
                .setY(new SiblingConstraint())
                .setWidth(new RelativeConstraint(1f))
                .setHeight(new AdditiveConstraint(new ChildBasedSizeConstraint(), new PixelConstraint(5f)))
                .setChildOf(this);

        private UIComponent x;

        public Draggable(UIComponent main) {
            x = new UIButton(main)
                    .setColor(Color.DARK_GRAY)
                    .setX(new SubtractiveConstraint(new RelativeConstraint(1f), new PixelConstraint(10f)))
                    .setWidth(new PixelConstraint(10f))
                    .setHeight(new PixelConstraint(10f))
                    .onMouseClick((component, event) -> {
                        this.parent.removeChild(this);
                        return null;
                    })
                    .setChildOf(topBar);
            x.setComponentName("close");

        }

    }




}
