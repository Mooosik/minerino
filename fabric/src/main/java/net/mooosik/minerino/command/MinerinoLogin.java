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

            if(config.getAccounts().keySet().size() == 0) {
                loginTooltip(context);
                return -1;
            }
            else if(config.getAccounts().keySet().size() == 1) {     //If the person has no alts

                return login(context, config.getAccounts().keySet().toArray(new String[0])[0]);
            } else {
                MutableText usernames = new LiteralText("[Minerino] Available accounts: ");
                for (String username: config.getAccounts().keySet()
                     ) {
                    usernames.append(Twitch.buildLinkedCommandText(username, "/minerino login ").formatted(Formatting.DARK_AQUA));
                }

                ((FabricClientCommandSource) context.getSource()).sendFeedback(usernames);
                return 0;
            }

        })
            .then(argument("username", StringArgumentType.word()).executes(context -> {

                ModConfig config = ModConfig.getConfig();
                String username = StringArgumentType.getString(context, "username");

                if(!config.getAccounts().containsKey(username.toLowerCase())) {
                    ((FabricClientCommandSource) context.getSource()).sendError(
                            new LiteralText("[Minerino] You are are not logged in with username " + username));

                    loginTooltip(context);
                    return -1;
                }

              return login(context, username);
            })       //with username and oauth parameters
                .then(argument("oauth", StringArgumentType.string())
                    .executes(context -> {

                       ModConfig config = ModConfig.getConfig();

                        config.addAccount(StringArgumentType.getString(context, "username"),StringArgumentType.getString(context, "oauth"));
                        config.save();
                        ((FabricClientCommandSource) context.getSource()).sendFeedback(new LiteralText("[Minerino] Successfully registered username " + StringArgumentType.getString(context, "username")));

                        return login(context,StringArgumentType.getString(context, "username"));

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
            ((FabricClientCommandSource) context.getSource()).sendFeedback(new LiteralText("[Minerino] Successfully signed in as " + username));

            if(config.getChannels().size() > 0) {

                MutableText joinedChannels = new LiteralText("");
                //String joinedChannels = "";
                for (String channel: config.getChannels()
                ) {
                    if(Twitch.joinChannel(channel)) {

                        joinedChannels.append(Twitch.buildLinkedCommandText(channel, "/minerino switch "));
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

    /**
     * Sends the login tooltip (to avoid duplicate code)
     * @param context
     */
    private static void loginTooltip(CommandContext context) {
        ((FabricClientCommandSource) context.getSource())
                .sendFeedback(new LiteralText("[Minerino] Please sign in first using \"/minerino login <twitch username> <token>\". You can use ").append(
                        new LiteralText("https://twitchapps.com/tmi/").styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,"https://twitchapps.com/tmi/"))).formatted(Formatting.BLUE))
                        .append(" to generate an oAuthentication token. Remove the \"oauth:\" part from the token."));
    }

}
