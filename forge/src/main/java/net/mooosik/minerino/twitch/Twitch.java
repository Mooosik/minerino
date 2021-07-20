package net.mooosik.minerino.twitch;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;
import net.mooosik.minerino.config.ModConfig;
import net.mooosik.minerino.util.SizedStack;

import java.util.*;
import java.util.regex.Pattern;

public class Twitch {

    private static TwitchClient TWITCHCLIENT;

    private static TextFormatting[] colors = Arrays.stream(TextFormatting.values()).filter(TextFormatting::isColor).sorted(Comparator.comparing(TextFormatting::getColor)).toArray(TextFormatting[]::new);

    private static HashMap<String, TextFormatting> TwitchUserColors = new HashMap<>();

    private static HashMap<String, SizedStack<ITextComponent>> chatMessages = new HashMap<>();


    public static boolean SWITCHMODE = false;

    /**
     * Setup the twitch client
     *
     */
    public static boolean setup(String username, String key) {
        try {
            TWITCHCLIENT = TwitchClientBuilder.builder()
                    .withEnableChat(true)
                    .withDefaultEventHandler(SimpleEventHandler.class)
                    .withChatAccount(new OAuth2Credential(username, key))
                    .build();


            TWITCHCLIENT.getEventManager().getEventHandler(SimpleEventHandler.class).registerListener(new TwitchEventHandler());
            ModConfig.getConfig().setActiveAccount(username);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * Closes the connection and sets the client back to null
     */
    public static void close() {
        if(TWITCHCLIENT != null) {
            TWITCHCLIENT.close();
            TWITCHCLIENT = null;
        }
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



    public static HashMap<String, TextFormatting> getTwitchUserColors() {
        return TwitchUserColors;
    }

    public static TwitchClient getClient() {
        return TWITCHCLIENT;
    }


    /**
     * Calculate chat color based on distance
     * @param hex string
     * @return
     */
    public static TextFormatting calculateMinecraftColor(String hex) {

        java.awt.Color twitchColor = java.awt.Color.decode(hex);
        TextFormatting calculatedFormatting = TextFormatting.WHITE;
        double dist = 10000;
        for (TextFormatting f: colors
        ) {
            java.awt.Color tmpColor = new java.awt.Color(f.getColor());
            double tmp = colorDistance(twitchColor, tmpColor);

            if(tmp < dist) {
                dist = tmp;
                calculatedFormatting = f;
            }

        }

        return calculatedFormatting;
    }

    /**
     * Calculates the distance between two colors
     * Based on this paper: https://www.compuphase.com/cmetric.htm
     * @param c1 color 1
     * @param c2 color 2
     * @return distance
     */
    private static double colorDistance(java.awt.Color c1, java.awt.Color c2) {
        int red1 = c1.getRed();
        int red2 = c2.getRed();
        int rmean = (red1 + red2) >> 1;
        int r = red1 - red2;
        int g = c1.getGreen() - c2.getGreen();
        int b = c1.getBlue() - c2.getBlue();
        return Math.sqrt((((512+rmean)*r*r)>>8) + 4*g*g + (((767-rmean)*b*b)>>8));
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


    public static HashMap<String, SizedStack<ITextComponent>> getChatMessages() {
        return chatMessages;
    }



    /**
     * Switch to a different channel
     * @param channel
     * @return
     */
    public static boolean switchChat(String channel) {
        if(chatMessages.containsKey(channel)) {     //First check if the channel exists

            if(channel.equals(ModConfig.getConfig().getActiveChat())) {
                Minecraft.getInstance().ingameGUI.sendChatMessage(ChatType.CHAT,
                        new StringTextComponent("[Minerino] You are already reading " + channel).mergeStyle(TextFormatting.RED), UUID.randomUUID());
                return false;
            }

            //Get current channel and attach a message that will show up the next time we switch to the channel. This way we have a marker which messages are new
            chatMessages.get(ModConfig.getConfig().getActiveChat()).push(new StringTextComponent("[Minerino] New messages:").mergeStyle(TextFormatting.GREEN));


            //Switch to new chat
            ModConfig.getConfig().setActiveChat(channel);
            ModConfig.getConfig().save();

            SWITCHMODE = true;      //Turn on switchmode so notifications and message manipulations dont begin

            //Send join success message
            Minecraft.getInstance().ingameGUI.sendChatMessage(ChatType.CHAT, new StringTextComponent("[Minerino] Switched to channel " + channel).mergeStyle(TextFormatting.GREEN),UUID.randomUUID());
            for (ITextComponent message: chatMessages.get(channel)    //Add all queued messages
            ) {
                Minecraft.getInstance().ingameGUI.sendChatMessage(ChatType.CHAT, message, UUID.randomUUID());
            }


            SWITCHMODE = false;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Builds a linked text with a word and a command
     * @param word
     * @param command
     * @return
     */
    public static IFormattableTextComponent buildLinkedCommandText(String word, String command) {
        return new StringTextComponent("["+ word + "] ").modifyStyle((style) -> style.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command + word)));
    }
}

