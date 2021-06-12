package net.mooosik.minerino.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;
import net.mooosik.minerino.config.ModConfig;
import net.mooosik.minerino.twitch.Twitch;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

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
                                    ((CommandSource) context.getSource()).sendFeedback(new StringTextComponent("[Minerino] Successfully registered username " + config.getUsername()),false);

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
        ((CommandSource) context.getSource()).sendFeedback(new StringTextComponent("[Minerino] Attempting to log in..."),false);

        if(config.getUsername().equals("") || config.getOauthKey().equals("")) {
            ((CommandSource) context.getSource())
                    .sendFeedback(new StringTextComponent("[Minerino] Please sign in with an oAuthentication token first. You can use ").appendSibling(
                            new StringTextComponent("https://twitchapps.com/tmi/").setStyle(Style.EMPTY.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,"https://twitchapps.com/tmi/"))).mergeStyle(TextFormatting.BLUE))
                            .appendString(" to generate a token. Remove the \"oauth:\" part from the token."),false);

            return -1;
        }


        if(Twitch.setup()) {
            ((CommandSource) context.getSource()).sendFeedback(new StringTextComponent("[Minerino] Successfully signed in as " + config.getUsername()),false);

            if(config.getChannels().size() > 0) {

                IFormattableTextComponent joinedChannels = new StringTextComponent("");
                //String joinedChannels = "";
                for (String channel: config.getChannels()
                ) {
                    if(Twitch.joinChannel(channel)) {

                        joinedChannels.appendSibling(Twitch.buildLinkedText(channel));
                    }
                }
                joinedChannels.mergeStyle(TextFormatting.DARK_PURPLE);


                ((CommandSource) context.getSource()).sendFeedback(new StringTextComponent("[Minerino] Connected to channel(s) ").appendSibling(joinedChannels), false);
                ((CommandSource) context.getSource()).sendFeedback(new StringTextComponent("[Minerino] Click on a channel to switch to it!").mergeStyle(TextFormatting.BLUE), false);
                ((CommandSource) context.getSource()).sendFeedback(new StringTextComponent("[Minerino] Your active chat is set to " + config.getActiveChat()), false);
            } else {
                ((CommandSource) context.getSource()).sendFeedback(new StringTextComponent("[Minerino] No channels found to connect to. Use /minerino join <channel>"), false);

            }
            return 1;
        } else {
            ((CommandSource) context.getSource()).sendFeedback(new StringTextComponent("[Minerino] Something went wrong!"), false);
            return -1;
        }

    }
}