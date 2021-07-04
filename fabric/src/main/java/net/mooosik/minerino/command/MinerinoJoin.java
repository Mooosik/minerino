package net.mooosik.minerino.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.text.LiteralText;
import net.mooosik.minerino.config.ModConfig;
import net.mooosik.minerino.twitch.Twitch;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class MinerinoJoin {


    /**
     * Join a channel command
     * @return
     */
    public static LiteralArgumentBuilder build() {

        return literal("join").then(argument("channel", StringArgumentType.word()).executes(context -> {       //if only login

            if(Twitch.getClient() == null) {
                ((FabricClientCommandSource) context.getSource()).sendError(new LiteralText("[Minerino] Connect to Twitch first using /minerino login"));
                return -1;
            }

            ModConfig config = ModConfig.getConfig();

            String channelString = StringArgumentType.getString(context, "channel").toLowerCase();

            if(channelString.equals("minecraft")) {
                ((FabricClientCommandSource) context.getSource()).sendError(new LiteralText("[Minerino] You can't join Minecraft"));

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
                    ((FabricClientCommandSource) context.getSource()).sendError(new LiteralText("[Minerino] Failed to join channel "
                            + channelString + ". Maybe the channel doesn't exist?"));
                    return -1;
                }
            }

            ((FabricClientCommandSource) context.getSource()).sendError(new LiteralText("[Minerino] Already joined channel " + channelString));
                return -1;


        })); }
}
