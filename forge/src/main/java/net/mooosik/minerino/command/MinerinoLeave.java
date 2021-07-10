package net.mooosik.minerino.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.StringTextComponent;
import net.mooosik.minerino.config.ModConfig;
import net.mooosik.minerino.twitch.Twitch;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

public class MinerinoLeave {

    /**
     * Leave a channel
     * @return
     */
    public static LiteralArgumentBuilder build() {

        return literal("leave").then(argument("channel", StringArgumentType.word()).suggests((context, builder) -> {

            for (String s : Twitch.getChatMessages().keySet()) {

                builder.suggest(s);

            }
            return builder.buildFuture();

        }).executes(context -> {       //if only login

            if(Twitch.getClient() == null) {
                ((CommandSource) context.getSource()).sendErrorMessage(new StringTextComponent("[Minerino] Connect to Twitch first using /minerino login"));
                return -1;
            }

            String channelString = StringArgumentType.getString(context, "channel");
            ModConfig config = ModConfig.getConfig();

            if(channelString.equals("minecraft")) {
                ((CommandSource) context.getSource()).sendErrorMessage(new StringTextComponent("[Minerino] You can't leavey Minecraft"));

                return -1;
            }

            if(config.getChannels().contains(channelString)) {
                config.getChannels().remove(channelString);
                Twitch.getChatMessages().remove(channelString);
                Twitch.getClient().getChat().leaveChannel(channelString);
                ((CommandSource) context.getSource()).sendFeedback(new StringTextComponent("[Minerino] Successfully left channel " + channelString),false);
                if(channelString.equals(config.getActiveChat().toLowerCase())) {
                    config.setActiveChat("Minecraft");
                    ((CommandSource) context.getSource()).sendFeedback(new StringTextComponent("[Minerino] Joined channel Minecraft"),false);
                }
                config.save();
            } else {
                ((CommandSource) context.getSource()).sendErrorMessage(new StringTextComponent("[Minerino] You are not connected to channel " + channelString));
                return -1;
            }

            return 1;
        })); }
}
