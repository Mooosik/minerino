package net.mooosik.minerino.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
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
    private static int listMessages(CommandContext context) {

        if(Twitch.getClient() == null) {
            ((FabricClientCommandSource) context.getSource()).sendError(new LiteralText("[Minerino] Not connected to twitch! Try /minerino login"));
            return -1;
        }

        ((FabricClientCommandSource) context.getSource())
                .sendFeedback(new LiteralText("[Minerino] Click on a channel to switch:"));

        MutableText channels = new LiteralText("[Minerino] ");
        for ( String channel : Twitch.getChatMessages().keySet()
        ) {
            channels.append(Twitch.buildLinkedCommandText(channel, "/minerino switch ").formatted(Formatting.DARK_PURPLE));
        }

        ((FabricClientCommandSource) context.getSource())
                .sendFeedback(channels);
        return 1;
    }
}
