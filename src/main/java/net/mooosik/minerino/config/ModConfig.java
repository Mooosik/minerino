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
import java.util.stream.Collectors;

import net.fabricmc.loader.api.FabricLoader;

public class ModConfig {

    public static final String DEFAULT_CHANNEL = "";
    public static final String DEFAULT_USERNAME = "";
    public static final String DEFAULT_OAUTH_KEY = "";
    public static final String DEFAULT_PREFIX = ":";
    public static final List<String> DEFAULT_IGNORE_LIST = new ArrayList<>();

    private static ModConfig SINGLE_INSTANCE = null;
    private final File configFile;

    private String channel;
    private String username;
    private String oauthKey;
    private String prefix;
    private String dateFormat;
    private List<String> ignoreList;
    private List<String> notificationList;


    private List<String> channels;

    public ModConfig() {
        this.configFile = FabricLoader
                .getInstance()
                .getConfigDirectory()
                .toPath()
                .resolve("minerino.json")
                .toFile();
        this.channel = DEFAULT_CHANNEL;
        this.username = DEFAULT_USERNAME;
        this.oauthKey = DEFAULT_OAUTH_KEY;
        this.prefix = DEFAULT_PREFIX;
        this.ignoreList = new ArrayList<>(DEFAULT_IGNORE_LIST);
        this.notificationList = new ArrayList<>();
        this.channels = new ArrayList<>();
    }

    public static ModConfig getConfig() {
        if (SINGLE_INSTANCE == null) {
            SINGLE_INSTANCE = new ModConfig();
        }

        return SINGLE_INSTANCE;
    }

    public void load() {
        try {
            String jsonStr = new String(Files.readAllBytes(this.configFile.toPath()));
            if (!jsonStr. equals("")) {
                JsonParser jsonParser = new JsonParser();
                JsonObject jsonObject = (JsonObject) jsonParser.parse(jsonStr);
                this.channel = jsonObject.has("channel")
                        ? jsonObject.getAsJsonPrimitive("channel").getAsString()
                        : DEFAULT_CHANNEL;
                this.username = jsonObject.has("username")
                        ? jsonObject.getAsJsonPrimitive("username").getAsString()
                        : DEFAULT_USERNAME;
                this.oauthKey = jsonObject.has("oauthKey")
                        ? jsonObject.getAsJsonPrimitive("oauthKey").getAsString()
                        : DEFAULT_OAUTH_KEY;
                this.prefix = jsonObject.has("prefix")
                        ? jsonObject.getAsJsonPrimitive("prefix").getAsString()
                        : DEFAULT_PREFIX;

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
        jsonObject.addProperty("channel", this.channel);
        jsonObject.addProperty("username", this.username);
        jsonObject.addProperty("oauthKey", this.oauthKey);
        jsonObject.addProperty("prefix", this.prefix);


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

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public List<String> getIgnoreList() {
        return ignoreList;
    }

    public void setIgnoreList(List<String> ignoreList) {
        // Force all usernames to be lowercase
        this.ignoreList = ignoreList.parallelStream().map(String::toLowerCase).collect(Collectors.toList());
    }



    public List<String> getNotificationList() {return notificationList;}

    public void setNotificationList(List<String> list) {
        notificationList = list;
    }

    public List<String> getChannels() {
        return channels;
    }

    public void setChannels(List<String> channels) {
        this.channels = channels;
    }

}
