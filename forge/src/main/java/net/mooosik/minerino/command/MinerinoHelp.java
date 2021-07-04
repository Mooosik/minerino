package net.mooosik.minerino.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;

public class MinerinoHelp {

    /**
     * Lists all commands of the mod
     * @return
     */
    public static LiteralArgumentBuilder build() {

        return literal("help").executes(context -> {

            ((CommandSource) context.getSource())
                    .sendFeedback(new StringTextComponent("[Minerino] Commands:"), false);
            ((CommandSource) context.getSource())
                    .sendFeedback(new StringTextComponent("[Minerino] Logging in: ").appendSibling(new StringTextComponent("/minerino login|logout [<twitch username> <oAuth token>]").mergeStyle(TextFormatting.GOLD)), false);
            ((CommandSource) context.getSource())
                    .sendFeedback(new StringTextComponent("[Minerino] Joining / leaving Channels: ").appendSibling(new StringTextComponent("/minerino join|leave <channel>").mergeStyle(TextFormatting.GOLD)), false);
            ((CommandSource) context.getSource())
                    .sendFeedback(new StringTextComponent("[Minerino] Switching active channel ").appendSibling(new StringTextComponent("/minerino switch <channel>").mergeStyle(TextFormatting.GOLD)).appendSibling(new StringTextComponent(" (You can also click on a channel name)").mergeStyle(TextFormatting.GREEN)), false);
            ((CommandSource) context.getSource())
                    .sendFeedback(new StringTextComponent("[Minerino] List all channels: ").appendSibling(new StringTextComponent("/minerino list OR /ml").mergeStyle(TextFormatting.GOLD)), false);
            ((CommandSource) context.getSource())
                    .sendFeedback(new StringTextComponent("[Minerino] Notifications: ").appendSibling(new StringTextComponent("/minerino alert add|remove <keyword>").mergeStyle(TextFormatting.GOLD)), false);
            ((CommandSource) context.getSource())
                    .sendFeedback(new StringTextComponent("[Minerino] Ignoring: ").appendSibling(new StringTextComponent("/minerino ignore|unignore <twitch username>").mergeStyle(TextFormatting.GOLD)), false);

            return 1;
        });

    }




}
