package net.mooosik.minerino.config;

import com.google.gson.*;
import net.mooosik.minerino.twitch.Twitch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ModConfig {

    private static ModConfig CONFIG = null;
    private final File configFile;

    private boolean sendInfoMessage;
    public boolean INFOFLAG;

    private String channel;
    private List<String> ignoreList;
    private List<String> notificationList;
    private HashMap<String, String> accounts;

    private String activeChat;
    private String activeAccount;

    private List<String> channels;

    public ModConfig() {
        this.configFile = new File("minerino.json");
        try {
            this.configFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.channel = "";
        this.ignoreList = new ArrayList<>();
        this.notificationList = new ArrayList<>();
        this.channels = new ArrayList<>();
        this.accounts = new HashMap<>();
        this.activeChat = "Minecraft";
        this.sendInfoMessage = true;
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

                this.sendInfoMessage = jsonObject.has("sendInfoMessage")
                        ? jsonObject.getAsJsonPrimitive("sendInfoMessage").getAsBoolean()
                        : true;

                INFOFLAG = this.sendInfoMessage;

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

                Gson gson = new GsonBuilder().create();
                if (jsonObject.has("accounts")) {
                    JsonArray ignoreListJsonArray = jsonObject.getAsJsonArray("channels");
                    this.accounts = gson.fromJson(jsonObject.get("accounts"), HashMap.class);
                }


            }
        } catch (IOException e) {
            // Do nothing, we have no file and thus we have to keep everything as default
        }
    }

    public void save() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("activeChat", this.activeChat);
        jsonObject.addProperty("sendInfoMessage", this.sendInfoMessage);

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

        Gson gson = new Gson();
        jsonObject.add("accounts", gson.toJsonTree(accounts));

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

    public boolean isSendInfoMessage() {
        return sendInfoMessage;
    }

    public void setSendInfoMessage(boolean sendInfoMessage) {
        this.sendInfoMessage = sendInfoMessage;
    }

    public void addAccount(String username, String oauthKey) {
        this.accounts.put(username.toLowerCase(),oauthKey);
    }

    public HashMap<String, String> getAccounts() {
        return accounts;
    }

    public String getUsername() {
        return activeAccount;
    }

    public void setActiveAccount(String username) {
        activeAccount = username;
    }
}
