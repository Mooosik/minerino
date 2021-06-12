package net.mooosik.minerino.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import net.mooosik.minerino.config.ModConfig;
import net.mooosik.minerino.twitch.Twitch;

import static net.minecraft.server.command.CommandManager.literal;

// argument("bar", word())
import static net.minecraft.server.command.CommandManager.argument;
public class MinerinoLogin {


    /**
     * Build the "login" command
     * @return
     */
    public static LiteralArgumentBuilder build() {

        return literal("login").executes(context -> {       //if only login
            ModConfig config = ModConfig.getConfig();

            return login(context, config);

        })
            .then(argument("username", StringArgumentType.word())       //with username and oauth parameters
                .then(argument("oauth", StringArgumentType.string())
                    .executes(context -> {

                       ModConfig config = ModConfig.getConfig();

                        config.setUsername(StringArgumentType.getString(context, "username"));
                       config.setOauthKey(StringArgumentType.getString(context, "oauth"));

                        config.save();
                        ((FabricClientCommandSource) context.getSource()).sendFeedback(new LiteralText("[Minerino] Successfully registered username " + config.getUsername()));

                        return login(context,config);

                    })));
    }

    /**
     * initialize the TwitchClient setup
     * @param context context of the command, since this is called by a command
     * @param config ModConfig
     * @return command success / failure
     */
    private static int login(CommandContext context, ModConfig config) {

        ((FabricClientCommandSource) context.getSource()).sendFeedback(new LiteralText("[Minerino] Attempting to log in..."));


        if(config.getUsername().equals("") || config.getOauthKey().equals("")) {
            ((FabricClientCommandSource) context.getSource())
                    .sendFeedback(new LiteralText("[Minerino] Please sign in with an oAuthentication token first. You can use ").append(
                            new LiteralText("https://twitchapps.com/tmi/").styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,"https://twitchapps.com/tmi/"))).formatted(Formatting.BLUE))
                                    .append(" to generate a token. Remove the \"oauth:\" part from the token."));

            return -1;
        }


        if(Twitch.setup()) {
            ((FabricClientCommandSource) context.getSource()).sendFeedback(new LiteralText("[Minerino] Successfully signed in as " + config.getUsername()));

            if(config.getChannels().size() > 0) {

                MutableText joinedChannels = new LiteralText("");
                //String joinedChannels = "";
                for (String channel: config.getChannels()
                ) {
                    if(Twitch.joinChannel(channel)) {

                        joinedChannels.append(Twitch.buildLinkedText(channel));
                    }
                }
                joinedChannels.formatted(Formatting.DARK_PURPLE);


                ((FabricClientCommandSource) context.getSource()).sendFeedback(new LiteralText("[Minerino] Connected to channel(s) ").append(joinedChannels));
                ((FabricClientCommandSource) context.getSource()).sendFeedback(new LiteralText("[Minerino] Click on a channel to switch to it!").formatted(Formatting.BLUE));
                ((FabricClientCommandSource) context.getSource()).sendFeedback(new LiteralText("[Minerino] Your active chat is set to " + config.getActiveChat()));
            } else {
                ((FabricClientCommandSource) context.getSource()).sendFeedback(new LiteralText("[Minerino] No channels found to connect to. Use /minerino join <channel>"));

            }
            return 1;
        } else {
            ((FabricClientCommandSource) context.getSource()).sendFeedback(new LiteralText("[Minerino] Something went wrong!"));
            return -1;
        }

    }
}
