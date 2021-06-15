package net.mooosik.minerino.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.mooosik.minerino.config.ModConfig;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

/***
 * Forge only:
 * turn off infomessage
 */
public class MinerinoInfoMessage {

    public static LiteralArgumentBuilder build() {

        return literal("infoMessage").then(argument("bool", BoolArgumentType.bool()).executes(context -> {

            ModConfig.getConfig().setSendInfoMessage(BoolArgumentType.getBool(context,"bool"));
            ModConfig.getConfig().save();
            return 1;
        }));

    }
}


