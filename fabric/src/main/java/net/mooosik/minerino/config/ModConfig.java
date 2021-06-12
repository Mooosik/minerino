package net.mooosik.minerino.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import net.fabricmc.loader.api.FabricLoader;
import net.mooosik.minerino.twitch.Twitch;

public class ModConfig {

    private static ModConfig CONFIG = null;
    private final File configFile;

    private String channel;
    private String username;
    private String oauthKey;
    private List<String> ignoreList;
    private List<String> notificationList;

    private String activeChat;

    private List<String> channels;

    public ModConfig() {
        this.configFile = FabricLoader
                .getInstance()
                .getConfigDirectory()
                .toPath()
                .resolve("minerino.json")
                .toFile();
        this.channel = "";
        this.username = "";
        this.oauthKey = "";
        this.ignoreList = new ArrayList<>();
        this.notificationList = new ArrayList<>();
        this.channels = new ArrayList<>();
        this.activeChat = "Minecraft";
    }

    public static ModConfig getConfig() {
        if (CONFIG == null) {
            CONFIG = new ModConfig();
        }

        return CONFIG;
    }

    public void load() {
        try {
            String jsonStr = new String(Files.readAllBytes(this.configFile.toPath()));
            if (!jsonStr. equals("")) {
                JsonParser jsonParser = new JsonParser();
                JsonObject jsonObject = (JsonObject) jsonParser.parse(jsonStr);
                this.activeChat = jsonObject.has("channel")
                        ? jsonObject.getAsJsonPrimitive("channel").getAsString()
                        : "Minecraft";
                this.username = jsonObject.has("username")
                        ? jsonObject.getAsJsonPrimitive("username").getAsString()
                        : "";
                this.oauthKey = jsonObject.has("oauthKey")
                        ? jsonObject.getAsJsonPrimitive("oauthKey").getAsString()
                        : "";

                if (jsonObject.has("channels")) {
                    JsonArray ignoreListJsonArray = jsonObject.getAsJsonArray("channels");
                    this.channels = new ArrayList<>();
                    for (JsonElement usernameJsonElement : ignoreListJsonArray) {
                        this.channels.add(usernameJsonElement.getAsString());
                    }
                }



                if (jsonObject.has("ignoreList")) {
                    JsonArray ignoreListJsonArray = jsonObject.getAsJsonArray("ignoreList");
                    this.ignoreList = new ArrayList<>();
                    for (JsonElement usernameJsonElement : ignoreListJsonArray) {
                        this.ignoreList.add(usernameJsonElement.getAsString());
                    }
                }

                if (jsonObject.has("notificationList")) {
                    JsonArray ignoreListJsonArray = jsonObject.getAsJsonArray("notificationList");
                    this.notificationList = new ArrayList<>();
                    for (JsonElement elem : ignoreListJsonArray) {
                        this.notificationList.add(elem.getAsString());
                    }
                }

            }
        } catch (IOException e) {
            // Do nothing, we have no file and thus we have to keep everything as default
        }
    }

    public void save() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("activeChat", this.activeChat);
        jsonObject.addProperty("username", this.username);
        jsonObject.addProperty("oauthKey", this.oauthKey);


        JsonArray channelsList = new JsonArray();
        for (String username : this.channels) {
            channelsList.add(username);
        }
        jsonObject.add("channels",channelsList);


            JsonArray ignoreListJsonArray = new JsonArray();
        for (String username : this.ignoreList) {
            ignoreListJsonArray.add(username);
        }
        jsonObject.add("ignoreList", ignoreListJsonArray);

        JsonArray notificationListArray = new JsonArray();
        for (String username : this.notificationList) {
            notificationListArray.add(username);
        }
        jsonObject.add("notificationList", notificationListArray );


        try (PrintWriter out = new PrintWriter(configFile)) {
            out.println(jsonObject.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOauthKey() {
        return oauthKey;
    }

    public void setOauthKey(String oauthKey) {
        this.oauthKey = oauthKey;
    }


    public List<String> getIgnoreList() {
        return ignoreList;
    }

    public List<String> getNotificationList() {return notificationList;}



    public List<String> getChannels() {
        return channels;
    }

    public void setChannels(List<String> channels) {
        this.channels = channels;
    }

    public String getActiveChat() {
        if(Twitch.getClient() == null) {
            return "Minecraft";
        }
        return activeChat;
    }

    public void setActiveChat(String activeChat) {
        this.activeChat = activeChat;
    }
}
