package net.mooosik.minerino.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.mooosik.minerino.twitch.Twitch;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;

public class MinerinoList {

    public static LiteralArgumentBuilder build() {

        return literal("list").executes(context -> {

            return listMessages(context);

        });

    }

    public static LiteralArgumentBuilder buildShort() {

        return literal("ml").executes(context -> {

            return listMessages(context);

        });

    }



    public static int listMessages(CommandContext context) {

        if(Twitch.getClient() == null) {
            ((CommandSource) context.getSource()).sendErrorMessage(new StringTextComponent("[Minerino] Not connected to twitch! Try /minerino login"));
            return -1;
        }

        ((CommandSource) context.getSource())
                .sendFeedback(new StringTextComponent("[Minerino] Click on a channel to switch:"), false);

        IFormattableTextComponent channels = new StringTextComponent("[Minerino] ");
        for ( String channel : Twitch.getChatMessages().keySet()
        ) {
            channels.appendSibling(Twitch.buildLinkedText(channel).mergeStyle(TextFormatting.DARK_PURPLE));
        }

        ((CommandSource) context.getSource())
                .sendFeedback(channels, false);
        return 1;
    }
}
