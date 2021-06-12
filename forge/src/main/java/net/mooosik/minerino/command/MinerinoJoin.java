package net.mooosik.minerino.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.StringTextComponent;
import net.mooosik.minerino.config.ModConfig;
import net.mooosik.minerino.twitch.Twitch;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

public class MinerinoJoin {
    public static LiteralArgumentBuilder build() {

        return literal("join").then(argument("channel", StringArgumentType.word()).executes(context -> {       //if only login

            if(Twitch.getClient() == null) {
                ((CommandSource) context.getSource()).sendErrorMessage(new StringTextComponent("[Minerino] Connect to Twitch first using /minerino login"));
                return -1;
            }

            ModConfig config = ModConfig.getConfig();

            String channelString = StringArgumentType.getString(context, "channel").toLowerCase();

            if(channelString.equals("minecraft")) {
                ((CommandSource) context.getSource()).sendErrorMessage(new StringTextComponent("[Minerino] You can't join Minecraft"));

                return -1;
            }

            if(!config.getChannels().contains(channelString)) {
                if(Twitch.joinChannel(channelString)) {
                    //config.setActiveChat(channelString);
                    config.getChannels().add(channelString);
                    Twitch.switchChat(channelString);
                    config.save();
                    //((FabricClientCommandSource) context.getSource()).sendFeedback(new LiteralText("[Minerino] Joined & switched to channel " + channelString));
                    return 1;
                } else {
                    ((CommandSource) context.getSource()).sendErrorMessage(new StringTextComponent("[Minerino] Failed to join channel "
                            + channelString + ". Maybe the channel doesn't exist?"));
                    return -1;
                }
            }

            ((CommandSource) context.getSource()).sendErrorMessage(new StringTextComponent("[Minerino] Already joined channel " + channelString));
            return -1;


        })); }
}
