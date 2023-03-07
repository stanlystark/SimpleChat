package me.vetustus.server.simplechat;

import com.google.gson.annotations.SerializedName;

public class ChatConfig {
    public static final String CONFIG_PATH = "config/simplechat.json";

    @SerializedName("enable_chat_mod")
    private final boolean isChatModEnabled;
    @SerializedName("enable_global_chat")
    private final boolean isGlobalChatEnabled;
    @SerializedName("enable_world_chat")
    private final boolean isWorldChatEnabled;
    @SerializedName("enable_chat_colors")
    private final boolean isChatColorsEnabled;
    @SerializedName("local_chat_format")
    private final String localChatFormat;
    @SerializedName("global_chat_format")
    private final String globalChatFormat;
    @SerializedName("world_chat_format")
    private final String worldChatFormat;
    @SerializedName("chat_range")
    private final int chatRange;

    public ChatConfig() {
        isChatModEnabled = true;
        isGlobalChatEnabled = false;
        isWorldChatEnabled = false;
        isChatColorsEnabled = false;
        localChatFormat = "%player% > &7%message%";
        globalChatFormat = "%player% > &e%message%";
        worldChatFormat = "%player% > &b%message%";
        chatRange = 100;
    }

    public boolean isChatModEnabled() {
        return isChatModEnabled;
    }

    public boolean isGlobalChatEnabled() {
        return isGlobalChatEnabled;
    }

    public boolean isWorldChatEnabled() {
        return isWorldChatEnabled;
    }

    public boolean isChatColorsEnabled() {
        return isChatColorsEnabled;
    }

    public int getChatRange() {
        return chatRange;
    }

    public String getGlobalChatFormat() {
        return globalChatFormat;
    }

    public String getWorldChatFormat() {
        return worldChatFormat;
    }

    public String getLocalChatFormat() {
        return localChatFormat;
    }
}
