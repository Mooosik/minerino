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

            if(config.getAccounts().keySet().size() == 0) {
                loginTooltip(context);
                return -1;
            }
            else if(config.getAccounts().keySet().size() == 1) {     //If the person has no alts

                return login(context, config.getAccounts().keySet().toArray(new String[0])[0]);
            } else {
                IFormattableTextComponent usernames = new StringTextComponent("[Minerino] Available accounts: ");
                for (String username: config.getAccounts().keySet()
                ) {
                    usernames.appendSibling(Twitch.buildLinkedCommandText(username, "/minerino login ").mergeStyle(TextFormatting.DARK_AQUA));
                }

                ((CommandSource) context.getSource()).sendFeedback(usernames,false);
                return 0;
            }

        }) .then(argument("username", StringArgumentType.word()).executes(context -> {

            ModConfig config = ModConfig.getConfig();
            String username = StringArgumentType.getString(context, "username");

            if(!config.getAccounts().containsKey(username.toLowerCase())) {
                ((CommandSource) context.getSource()).sendErrorMessage(
                        new StringTextComponent("[Minerino] You are are not logged in with username " + username));

                loginTooltip(context);
                return -1;
            }

            return login(context, username);
        })
                .then(argument("oauth", StringArgumentType.string())
                                .executes(context -> {

                                    ModConfig config = ModConfig.getConfig();

                                    config.addAccount(StringArgumentType.getString(context, "username"),StringArgumentType.getString(context, "oauth"));
                                    config.save();
                                    ((CommandSource) context.getSource()).sendFeedback(new StringTextComponent("[Minerino] Successfully registered username " + StringArgumentType.getString(context, "username")),false);

                                    return login(context, StringArgumentType.getString(context, "username"));

                                })));
    }

    /**
     * Login with a specific username
     * @param context
     * @param username
     * @return
     */
    private static int login(CommandContext context, String username) {
        ModConfig config = ModConfig.getConfig();

        Twitch.close();

        if(Twitch.setup(username, config.getAccounts().get(username.toLowerCase()))) {

            ((CommandSource) context.getSource()).sendFeedback(new StringTextComponent("[Minerino] Successfully signed in as " + config.getUsername()),false);

            if(config.getChannels().size() > 0) {

                IFormattableTextComponent joinedChannels = new StringTextComponent("");
                //String joinedChannels = "";
                for (String channel: config.getChannels()
                ) {
                    if(Twitch.joinChannel(channel)) {

                        joinedChannels.appendSibling(Twitch.buildLinkedCommandText(channel,"/minerino switch "));
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



    /**
     * Sends the login tooltip (to avoid duplicate code)
     * @param context
     */
    private static void loginTooltip(CommandContext context) {
      ((CommandSource) context.getSource())
            .sendFeedback(new StringTextComponent("[Minerino] Please sign in first using \"/minerino login <twitch username> <token>\". You can use ").appendSibling(
                            new StringTextComponent("https://twitchapps.com/tmi/").setStyle(Style.EMPTY.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,"https://twitchapps.com/tmi/"))).mergeStyle(TextFormatting.BLUE))
            .appendString(" to generate an oAuthentication token. Remove the \"oauth:\" part from the token."),false);
    }
}