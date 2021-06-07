package net.mooosik.minerino.twitch;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.core.EventManager;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.MessageType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.mooosik.minerino.config.ModConfig;
import net.mooosik.minerino.util.SizedStack;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Twitch {

    private static TwitchClient TWITCHCLIENT;

    private static Formatting[] colors = Arrays.stream(Formatting.values()).filter(Formatting::isColor).sorted(Comparator.comparing(Formatting::getColorValue)).toArray(Formatting[]::new);

    private static HashMap<String, Formatting> TwitchUserColors = new HashMap<>();

    private static HashMap<String, SizedStack<Text>> chatMessages = new HashMap<>();


    public static boolean SWITCHMODE = false;

    /**
     * Setup the twitch client
     */
    public static boolean setup() {
        try {
        TWITCHCLIENT = TwitchClientBuilder.builder()
                .withEnableChat(true)
                .withDefaultEventHandler(SimpleEventHandler.class)
                .withChatAccount(new OAuth2Credential(ModConfig.getConfig().getUsername(), ModConfig.getConfig().getOauthKey()))
                .build();


            TWITCHCLIENT.getEventManager().getEventHandler(SimpleEventHandler.class).registerListener(new TwitchEventHandler());

        return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public static void close() {
        TWITCHCLIENT.close();
        TWITCHCLIENT = null;
    }


    /**
     * Check if the message contains a notification
     * @param message
     * @return
     */
    public static boolean containsNotification(String message) {
        List<String> notifications = ModConfig.getConfig().getNotificationList();

        for (String s: notifications
             ) {

            String regex = "^.+?(> |: ).*\\b@?"+ s + "\\b";     //This regex string was created with the help of @nakomaru (on twitch). Thanks DonkHappy
            Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

            if(p.matcher(message).find()) {
                return true;
            }


        }

        return false;
    }



    public static HashMap<String, Formatting> getTwitchUserColors() {
        return TwitchUserColors;
    }

    public static TwitchClient getClient() {
        return TWITCHCLIENT;
    }

    public static Formatting calculateMinecraftColor(Integer color) {
        int compare = (int)Math.floor(colors.length / 2);
        for(int i = (int)Math.floor(colors.length / 4); i > 0; i = (int)Math.floor(i / 2)) {

            if(color >= colors[compare].getColorValue()) {
                //compare = Math.min(compare + i, colors.length-1);
                compare = compare +i;
            } else if(color < colors[compare].getColorValue()) {
                //compare = Math.max(compare - i, 0);
                compare = compare -i;
            } else {
                break;
            }

        }


        System.out.println(compare);
        return colors[compare];
    }

    public static Formatting calculateMCColor(Integer color) {
       int i = colors.length -1;
        int distance = Math.abs(colors[i].getColorValue() - color);
        int tmp;

        Formatting f = colors[0];
        for(i = 14;i >= 0; i--) {
            tmp = Math.abs(colors[i].getColorValue() - color);
            if(tmp < distance ) {
                distance = tmp;
                f = colors[i];
            }
            }

        return f;


    }

    /***
     * Join a channel and add it the channel to the chatMessages HashMap
     * @param channel
     * @return
     */
    public static boolean joinChannel(String channel) {
        try {
            TWITCHCLIENT.getChat().joinChannel(channel);
            chatMessages.put(channel, new SizedStack<>(50));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public static HashMap<String, SizedStack<Text>> getChatMessages() {
        return chatMessages;
    }

    /**
     * Switch to a different channel
     * @param channel
     * @return
     */
    public static boolean switchChat(String channel) {
        if(chatMessages.containsKey(channel)) {     //First check if the channel exists

            //Get current channel and attach a message that will show up the next time we switch to the channel. This way we have a marker which messages are new
            chatMessages.get(ModConfig.getConfig().getActiveChat()).push(new LiteralText("[Minerino] New messages:").formatted(Formatting.GREEN));


            //Switch to new chat
            ModConfig.getConfig().setActiveChat(channel);
            ModConfig.getConfig().save();

            SWITCHMODE = true;      //Turn on switchmode so notifications and message manipulations dont begin

            //Send join success message
            MinecraftClient.getInstance().inGameHud.addChatMessage(MessageType.CHAT, new LiteralText("[Minerino] Switched to channel " + channel).formatted(Formatting.GREEN),UUID.randomUUID());
            for (Text message: chatMessages.get(channel)    //Add all queued messages
                 ) {
                MinecraftClient.getInstance().inGameHud.addChatMessage(MessageType.CHAT, message, UUID.randomUUID());
            }


            SWITCHMODE = false;
            return true;
        } else {
            return false;
        }


    }
}


