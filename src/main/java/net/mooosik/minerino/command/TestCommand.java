package net.mooosik.minerino.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.TranslatableText;
import net.mooosik.minerino.Minerino;
import net.mooosik.minerino.gui.SpruceMainMenuScreen;

public class TestCommand implements Command<Object> {

    @Override
    public int run(CommandContext<Object> context) throws CommandSyntaxException {

        String s = StringArgumentType.getString(context, "argu");
        System.out.println("TEST COMMAND " /*+ s*/);

        return 0;
    }
}
