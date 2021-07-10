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
            ((FabricClientCommandSource) context.getSource()).sendError(new LiteralText("[Minerino] Not connected to twitch! Try /minerino login"));
            return -1;
        }

        ((FabricClientCommandSource) context.getSource())
                .sendFeedback(new LiteralText("[Minerino] Click on a channel to switch:"));

        MutableText channels = new LiteralText("[Minerino] ");
        for ( String channel : Twitch.getChatMessages().keySet()
        ) {
            channels.append(Twitch.buildLinkedText(channel).formatted(Formatting.DARK_PURPLE));
        }

        ((FabricClientCommandSource) context.getSource())
                .sendFeedback(channels);
        return 1;
    }
}
