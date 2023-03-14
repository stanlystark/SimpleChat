package me.vetustus.server.simplechat;

import com.google.gson.Gson;
import me.vetustus.server.simplechat.api.event.PlayerChatCallback;
import me.vetustus.server.simplechat.integration.FTBTeamsIntegration;
import me.vetustus.server.simplechat.integration.LuckPermsIntegration;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;

import static me.vetustus.server.simplechat.ChatColor.translateChatColors;

public class SimpleChat implements ModInitializer {
    public ChatConfig config;
    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void onInitialize() {

        try {
            loadConfig();
	    LOGGER.info("The config is saved!");
        } catch (IOException e) {
            e.printStackTrace();
        }

        boolean ftbteams = FabricLoader.getInstance().isModLoaded("ftbteams");
        boolean luckperms = FabricLoader.getInstance().isModLoaded("luckperms");

        PlayerChatCallback.EVENT.register((player, message) -> {
            PlayerChatCallback.ChatMessage chatMessage = new PlayerChatCallback.ChatMessage(player, message);

            /*
             * If someone wants to use the mod as a library,
             * they must disable the "enable_chat_mod" parameter,
             * then the chat will not be handled by the mod.
             */
            if (!config.isChatModEnabled())
                return chatMessage;

            chatMessage.setCancelled(true);

            // TODO: Add mention/private message

            boolean isGlobalMessage = false;
            boolean isWorldMessage = false;
            String chatFormat = config.getLocalChatFormat();
            if (config.isGlobalChatEnabled()) {
                if (message.startsWith("!")) {
                    isGlobalMessage = true;
                    chatFormat = config.getGlobalChatFormat();
                    message = message.substring(1);
                }
            }
            if (config.isWorldChatEnabled()) {
                if (message.startsWith("#")) {
                    isWorldMessage = true;
                    chatFormat = config.getWorldChatFormat();
                    message = message.substring(1);
                }
            }
            String prepareStringMessage = chatFormat
                    .replaceAll("%player%", player.getName().getString())
                    .replaceAll("%ftbteam%", ftbteams ? FTBTeamsIntegration.getTeam(player) : "")
                    .replaceAll("%lp_group%", luckperms ? translateChatColors('&', LuckPermsIntegration.getPrimaryGroup(player)) : "")
                    .replaceAll("%lp_prefix%", luckperms ? translateChatColors('&', LuckPermsIntegration.getPrefix(player)) : "")
                    .replaceAll("%lp_suffix%", luckperms ? translateChatColors('&', LuckPermsIntegration.getSuffix(player)) : "");
            prepareStringMessage = translateChatColors('&', prepareStringMessage);

            String stringMessage = prepareStringMessage
                    .replaceAll("%message%", message);

            if (config.isChatColorsEnabled())
                stringMessage = translateChatColors('&', stringMessage);

            Text resultMessage = literal(stringMessage);

            int isPlayerLocalFound = 0;

            List<ServerPlayerEntity> players = Objects.requireNonNull(player.getServer(), "The server cannot be null.")
                    .getPlayerManager().getPlayerList();
            for (ServerPlayerEntity p : players) {

                if (config.isGlobalChatEnabled()) {
                    if (isGlobalMessage) {
                        p.sendMessage(resultMessage, false);
                    } else if (isWorldMessage && config.isWorldChatEnabled()) {
                        if (p.getEntityWorld().getRegistryKey().getValue() == player.getEntityWorld().getRegistryKey().getValue()) {
                            p.sendMessage(resultMessage, false);
                        }
                    } else {
                        if (p.squaredDistanceTo(player) <= config.getChatRange() && p.getEntityWorld().getRegistryKey().getValue() == player.getEntityWorld().getRegistryKey().getValue()) {
                            p.sendMessage(resultMessage, false);
                            isPlayerLocalFound++;
                        }
                    }
                } else if (config.isWorldChatEnabled()) {
                    if (isWorldMessage) {
                        if (p.getEntityWorld().getRegistryKey().getValue() == player.getEntityWorld().getRegistryKey().getValue()) {
                            p.sendMessage(resultMessage, false);
                        }
                    } else {
                        if (p.squaredDistanceTo(player) <= config.getChatRange() && p.getEntityWorld().getRegistryKey().getValue() == player.getEntityWorld().getRegistryKey().getValue()) {
                            p.sendMessage(resultMessage, false);
                            isPlayerLocalFound++;
                        }
                    }
                } else {
                    p.sendMessage(resultMessage, false);
                }

                //
//                if (config.isGlobalChatEnabled()) {
//                    if (isGlobalMessage) {
//                        p.sendMessage(resultMessage, false);
//                    } else {
//                        if (p.squaredDistanceTo(player) <= config.getChatRange()) {
//                            p.sendMessage(resultMessage, false);
//                        }
//                    }
//                } else {
//                    p.sendMessage(resultMessage, false);
//                }
            }

            if (isPlayerLocalFound <= 1 && !isGlobalMessage && !isWorldMessage) {
                String noPlayerNearbyText = config.getNoPlayerNearbyText();
                Text noPlayerNearbyTextResult = literal(translateChatColors('&', noPlayerNearbyText));
                player.sendMessage(noPlayerNearbyTextResult, config.noPlayerNearbyActionBar());
            }

            LOGGER.info(stringMessage);
            return chatMessage;
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(CommandManager.literal("simplechat").executes(context -> {
                    if (context.getSource().hasPermissionLevel(1)) {
                        try {
                            loadConfig();
                            context.getSource().sendFeedback(literal("Settings are reloaded!"), false);
                        } catch (IOException e) {
                            context.getSource().sendFeedback(literal("An error occurred while reloading the settings (see the console)!"), false);
                            e.printStackTrace();
                        }
                    } else {
                        context.getSource().sendFeedback(literal("You don't have the right to do this! If you think this is an error, contact your server administrator.")
                                .copy().formatted(Formatting.RED), false);
                    }
                    return 1;
                })));
    }

    private void loadConfig() throws IOException {
        File configFile = new File(ChatConfig.CONFIG_PATH);
	File configFolder = new File("config/");
	if (!configFolder.exists())
		configFolder.mkdirs();
        if (!configFile.exists()) {
            Files.copy(Objects.requireNonNull(
                    this.getClass().getClassLoader().getResourceAsStream("simplechat.json"),
                    "Couldn't find the configuration file in the JAR"), configFile.toPath());
        }
        try {
            config = new Gson().fromJson(new FileReader(ChatConfig.CONFIG_PATH), ChatConfig.class);
        } catch (FileNotFoundException e) {
            config = new ChatConfig();
            e.printStackTrace();
        }
    }

    private Text literal(String text) {
        return Text.literal(text);
    }
}
