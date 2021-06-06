package net.mooosik.minerino.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.mooosik.minerino.config.ModConfig;
import net.mooosik.minerino.twitch.Twitch;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class MinerinoChat {

    public static LiteralArgumentBuilder build() {
        return literal("switch")
                .then(argument("channel", StringArgumentType.word())/*.suggests((context, builder) -> {

                    for (String s : Twitch.getChatMessages().keySet()) {

                            builder.suggest(s);

                    }
                    return builder.buildFuture();

                })*/.executes(context -> {
                    String channel = StringArgumentType.getString(context, "channel");

                    if(Twitch.getClient() == null) {
                        ((FabricClientCommandSource) context.getSource()).sendError(new LiteralText("[Minerino] Not connected to twitch! Try /minerino login"));
                        return -1;
                    }
                    if(!Twitch.switchChat(channel)) {
                        ((FabricClientCommandSource) context.getSource()).sendError(new LiteralText("[Minerino] Failed to switch to channel "
                                + channel + ". Maybe the channel doesn't exist?"));
                    }

                    return 1;
                }));
    }

}
