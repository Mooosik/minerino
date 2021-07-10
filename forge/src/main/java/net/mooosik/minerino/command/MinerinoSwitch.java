package net.mooosik.minerino.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.StringTextComponent;
import net.mooosik.minerino.twitch.Twitch;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

public class MinerinoSwitch {
    public static LiteralArgumentBuilder build() {
        return literal("switch")
                .then(argument("channel", StringArgumentType.word()).suggests((context, builder) -> {

                    for (String s : Twitch.getChatMessages().keySet()) {

                        builder.suggest(s);

                    }
                    return builder.buildFuture();

                }).executes(context -> {
                    String channel = StringArgumentType.getString(context, "channel");

                    if(Twitch.getClient() == null) {
                        ((CommandSource) context.getSource()).sendErrorMessage(new StringTextComponent("[Minerino] Not connected to twitch! Try /minerino login"));
                        return -1;
                    }
                    if(!Twitch.switchChat(channel)) {
                        ((CommandSource) context.getSource()).sendErrorMessage(new StringTextComponent("[Minerino] Failed to switch to channel "
                                + channel + "."));
                    }

                    return 1;
                }));
    }
}
