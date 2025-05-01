package com.verr1.valkyrienmanager.foundation.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.verr1.valkyrienmanager.VManagerClient;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class VMClientCommands {

    private static LiteralArgumentBuilder<CommandSourceStack> lt(String name){
        return LiteralArgumentBuilder.literal(name);
    }

    private static<T> RequiredArgumentBuilder<CommandSourceStack, T> arg(String name, ArgumentType<T> type){
        return RequiredArgumentBuilder.argument(name, type);
    }

    public static void registerClientCommands(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(
                Commands.literal(
                        "vsm"
                ).then(
                        lt("ui").executes(
                                VMClientCommands::OpenGUICommand
                        )
                )
        );
    }


    private static int OpenGUICommand(CommandContext<CommandSourceStack> context){
        VManagerClient.manager().openGUI();
        return 1;
    }

    private static int ReloadViewCommand(CommandContext<CommandSourceStack> context){
        VManagerClient.manager().requestAll();
        return 1;
    }
}
