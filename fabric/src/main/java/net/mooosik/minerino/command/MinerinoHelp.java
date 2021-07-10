package net.mooosik.minerino.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.command.CommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;

public class MinerinoHelp {

    /**
     * Lists all commands of the mod
     * @return
     */
    public static LiteralArgumentBuilder build() {
        return literal("help").executes(context -> {
            ((FabricClientCommandSource) context.getSource())
                    .sendFeedback(new LiteralText("[Minerino] Commands:"));
            ((FabricClientCommandSource) context.getSource())
                    .sendFeedback(new LiteralText("[Minerino] Logging in: ").append(new LiteralText("/minerino login|logout [<twitch username> [<oAuth token>]]").formatted(Formatting.GOLD)));
            ((FabricClientCommandSource) context.getSource())
                    .sendFeedback(new LiteralText("[Minerino] Joining / leaving Channels: ").append(new LiteralText("/minerino join|leave <channel>").formatted(Formatting.GOLD)));
            ((FabricClientCommandSource) context.getSource())
                    .sendFeedback(new LiteralText("[Minerino] Switching active channel ").append(new LiteralText("/minerino switch <channel>").formatted(Formatting.GOLD)).append(new LiteralText(" (You can also click on a channel name)").formatted(Formatting.GREEN)));
            ((FabricClientCommandSource) context.getSource())
                    .sendFeedback(new LiteralText("[Minerino] List all channels: ").append(new LiteralText("/minerino list OR /ml").formatted(Formatting.GOLD)));
            ((FabricClientCommandSource) context.getSource())
                    .sendFeedback(new LiteralText("[Minerino] Notifications: ").append(new LiteralText("/minerino alert add|remove <keyword>").formatted(Formatting.GOLD)));
            ((FabricClientCommandSource) context.getSource())
                    .sendFeedback(new LiteralText("[Minerino] Ignoring: ").append(new LiteralText("/minerino ignore|unignore <twitch username>").formatted(Formatting.GOLD)));

            return 1;
        });

    }
}
