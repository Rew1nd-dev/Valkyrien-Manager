package com.verr1.valkyrienmanager.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

import java.util.function.UnaryOperator;

public class ComponentUtil {

    public static Component QUERY_TITLE = Component.literal("VSM Data:").withStyle(titleStyle());
    public static Component DASH = Component.literal("-----------------------------------").withStyle(dashStyle());

    public static Component titleWithContent(String title, String content, UnaryOperator<Style> titleStyle, UnaryOperator<Style> contentStyle){
        return Component.literal(title).withStyle(titleStyle)
                .append(
                    Component.literal(":").withStyle(titleStyle))
                .append(
                    Component.literal(content).withStyle(contentStyle)
                );
    }

    public static UnaryOperator<Style> titleStyle(){
        return s -> s.withBold(true).withColor(ChatFormatting.GRAY);
    }

    public static UnaryOperator<Style> dashStyle(){
        return s -> s.withBold(true).withColor(ChatFormatting.DARK_GRAY);
    }

    public static UnaryOperator<Style> contentStyle(){
        return s -> s.withBold(false).withColor(ChatFormatting.DARK_AQUA);
    }

}
