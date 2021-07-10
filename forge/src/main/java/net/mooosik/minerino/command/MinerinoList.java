package net.mooosik.minerino.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.mooosik.minerino.twitch.Twitch;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;

public class MinerinoList {

    /**
     * Lists all channels command
     * @return
     */
    public static LiteralArgumentBuilder build() {

        return literal("list").executes(context -> {

            return listMessages(context);

        });

    }

    /**
     * Lists all channels command
     * @return
     */
    public static LiteralArgumentBuilder buildShort() {

        return literal("ml").executes(context -> {

            return listMessages(context);

        });

    }


    /**
     * Gets all channels the client is connected to and posts them in chat
     * @param context CommandContext
     * @return whether or not the request was successful
     */
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
            channels.appendSibling(Twitch.buildLinkedCommandText(channel,"/minerino switch ").mergeStyle(TextFormatting.DARK_PURPLE));
        }

        ((CommandSource) context.getSource())
                .sendFeedback(channels, false);
        return 1;
    }
}
