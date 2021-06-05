package net.mooosik.minerino.twitch;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.core.EventManager;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import net.minecraft.util.Formatting;
import net.mooosik.minerino.config.ModConfig;

import java.util.*;

public class Twitch {

    private static TwitchClient TWITCHCLIENT;

    private static Formatting[] colors = Arrays.stream(Formatting.values()).filter(Formatting::isColor).sorted(Comparator.comparing(Formatting::getColorValue)).toArray(Formatting[]::new);

    private static HashMap<String, Formatting> TwitchUserColors = new HashMap<>();


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


    public static boolean containsNotification(String message) {
        List<String> notifications = ModConfig.getConfig().getNotificationList();

        for (String s: notifications
             ) {
            if(message.contains(s)) {
                return  true;
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




    }


