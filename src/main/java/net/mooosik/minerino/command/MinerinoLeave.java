package net.mooosik.minerino.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.text.LiteralText;
import net.mooosik.minerino.config.ModConfig;
import net.mooosik.minerino.twitch.Twitch;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class MinerinoLeave {

    public static LiteralArgumentBuilder build() {

        return literal("leave").then(argument("channel", StringArgumentType.word()).suggests((context, builder) -> {

            for (String s : Twitch.getChatMessages().keySet()) {

                builder.suggest(s);

            }
            return builder.buildFuture();

        }).executes(context -> {       //if only login

            if(Twitch.getClient() == null) {
                ((FabricClientCommandSource) context.getSource()).sendError(new LiteralText("[Minerino] Connect to Twitch first using /minerino login"));
                return -1;
            }

            String channelString = StringArgumentType.getString(context, "channel");
            ModConfig config = ModConfig.getConfig();

            if(channelString.equals("minecraft")) {
                ((FabricClientCommandSource) context.getSource()).sendError(new LiteralText("[Minerino] You can't leavey Minecraft"));

                return -1;
            }

            if(config.getChannels().contains(channelString)) {
                config.getChannels().remove(channelString);
                Twitch.getChatMessages().remove(channelString);
                Twitch.getClient().getChat().leaveChannel(channelString);
                ((FabricClientCommandSource) context.getSource()).sendFeedback(new LiteralText("[Minerino] Successfully left channel " + channelString));
                if(channelString.equals(config.getActiveChat().toLowerCase())) {
                    config.setActiveChat("Minecraft");
                    ((FabricClientCommandSource) context.getSource()).sendFeedback(new LiteralText("[Minerino] Joined channel Minecraft"));
                }
                config.save();
            } else {
                ((FabricClientCommandSource) context.getSource()).sendError(new LiteralText("[Minerino] You are not connected to channel " + channelString));
                return -1;
            }

            return 1;
        })); }
}
