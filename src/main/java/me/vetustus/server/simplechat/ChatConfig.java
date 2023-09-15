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
    @SerializedName("no_players_nearby_text")
    private final String noPlayerNearbyText;
    @SerializedName("no_players_nearby_action_bar")
    private final boolean noPlayerNearbyActionBar;
    @SerializedName("no_players_nearby_message")
    private final boolean noPlayerNearbyMessage;
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
        noPlayerNearbyText = "&fNo players nearby. Please use &e!<message> &ffor global chat.";
        noPlayerNearbyMessage = true;
        noPlayerNearbyActionBar = true;
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

    public boolean noPlayerNearbyActionBar() {
        return noPlayerNearbyActionBar;
    }

    public boolean noPlayerNearbyMessage() {
        return noPlayerNearbyMessage;
    }

    public int getChatRange() {
        return chatRange * chatRange;
    }

    public String getGlobalChatFormat() {
        return globalChatFormat;
    }

    public String getWorldChatFormat() {
        return worldChatFormat;
    }


    public String getNoPlayerNearbyText() {
        return noPlayerNearbyText;
    }

    public String getLocalChatFormat() {
        return localChatFormat;
    }
}
