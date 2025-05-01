package com.verr1.valkyrienmanager.registry;

import com.mojang.blaze3d.systems.RenderSystem;
import com.simibubi.create.foundation.gui.UIRenderHelper;
import com.simibubi.create.foundation.utility.Color;
import com.verr1.valkyrienmanager.VManagerMod;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public enum VMGuiTextures {

    SIMPLE_BACKGROUND("simple_background", 176, 108),
    SIMPLE_BACKGROUND_HALF("simple_background_half", 87, 108),
    SIMPLE_BACKGROUND_QUARTER("simple_background_5_6", 164, 138),
    SIMPLE_BACKGROUND_LARGE("simple_background_large", 256, 133),
    SIMPLE_BACKGROUND_ONE_LINE("simple_background_one_line", 160, 29),


    SMALL_BUTTON_RED("icons10x10", 0, 0, 10, 10),
    SMALL_BUTTON_GREEN("icons10x10", 10, 0, 10, 10),
    SMALL_BUTTON_SELECTION("icons10x10", 20, 0, 10, 10),
    SMALL_BUTTON_NO("icons10x10", 30, 0, 10, 10),
    SMALL_BUTTON_YES("icons10x10", 40, 0, 10, 10),

    TAB_BUTTON_BACKGROUND("tab_button_background", 0, 0, 40, 12),
    TAB_BUTTON_FRAME("tab_button_frame", 0, 0, 40, 1),
    TAB_BAR("tab_bar", 0, 0, 164, 24),
            ;

    public static final int FONT_COLOR = 0x575F7A;

    public final ResourceLocation location;
    public final int width;
    public final int height;
    public final int startX;
    public final int startY;

    VMGuiTextures(String location, int width, int height) {
        this(location, 0, 0, width, height);
    }

    VMGuiTextures(int startX, int startY) {
        this("icons", startX * 16, startY * 16, 16, 16);
    }

    VMGuiTextures(String location, int startX, int startY, int width, int height) {
        this(VManagerMod.MOD_ID, location, startX, startY, width, height);
    }

    VMGuiTextures(String namespace, String location, int startX, int startY, int width, int height) {
        this.location = new ResourceLocation(namespace, "textures/gui/" + location + ".png");
        this.width = width;
        this.height = height;
        this.startX = startX;
        this.startY = startY;
    }




    public int width() {
        return width;
    }


    public int height() {
        return height;
    }

}
