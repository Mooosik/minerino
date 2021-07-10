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



    public static HashMap<String, TextFormatting> getTwitchUserColors() {
        return TwitchUserColors;
    }

    public static TwitchClient getClient() {
        return TWITCHCLIENT;
    }

    /**
     * Calculate chat color based on distance
     * @param color
     * @return
     */
    public static TextFormatting calculateMCColor(Integer color) {
        int i = colors.length -1;
        int distance = Math.abs(colors[i].getColor() - color);
        int tmp;

        TextFormatting f = colors[0];
        for(i = 14;i >= 0; i--) {
            tmp = Math.abs(colors[i].getColor() - color);
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
     * Builds the prefix and makes it clickable to make switching to different channels easier
     * @param channel
     * @return
     */
    public static IFormattableTextComponent buildLinkedText(String channel) {
        return new StringTextComponent("["+ channel + "] ").modifyStyle((style) -> style.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/minerino switch " + channel)));
    }
}

