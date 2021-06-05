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

    public static LiteralArgumentBuilder build() {

        return literal("join").then(argument("channel", StringArgumentType.word()).executes(context -> {       //if only login

            if(Twitch.getClient() == null) {
                ((FabricClientCommandSource) context.getSource()).sendError(new LiteralText("Connect to Twitch first using /minerino login"));
                return -1;
            }

            ModConfig config = ModConfig.getConfig();

            String channelString = StringArgumentType.getString(context, "channel");
            if(!config.getChannels().contains(channelString)) {


                try {
                    Twitch.getClient().getChat().joinChannel(channelString);
                } catch (Exception e) {
                    ((FabricClientCommandSource) context.getSource()).sendError(new LiteralText("Failed to join channel "
                            + channelString + ". Maybe the channel doesn't exist?"));
                    e.printStackTrace();
                    return -1;
                }

                config.getChannels().add(channelString);
                config.save();

                ((FabricClientCommandSource) context.getSource()).sendFeedback(new LiteralText("Joined channel " + channelString));
                return 1;
            }

            ((FabricClientCommandSource) context.getSource()).sendError(new LiteralText("Already joined channel " + channelString));
                return -1;


        })); }
}
