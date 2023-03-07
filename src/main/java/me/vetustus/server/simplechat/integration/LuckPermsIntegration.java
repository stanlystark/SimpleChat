package me.vetustus.server.simplechat.integration;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.minecraft.server.network.ServerPlayerEntity;

public class LuckPermsIntegration {

    public static String getPrimaryGroup(ServerPlayerEntity player) {
        LuckPerms api = LuckPermsProvider.get();
        User user = api.getPlayerAdapter(ServerPlayerEntity.class).getUser(player);

        return user.getPrimaryGroup();

    }
    public static String getPrefix(ServerPlayerEntity player) {
        LuckPerms api = LuckPermsProvider.get();
        User user = api.getPlayerAdapter(ServerPlayerEntity.class).getUser(player);

        return user.getCachedData().getMetaData().getPrefix();
    }
    public static String getSuffix(ServerPlayerEntity player) {
        LuckPerms api = LuckPermsProvider.get();
        User user = api.getPlayerAdapter(ServerPlayerEntity.class).getUser(player);

        return user.getCachedData().getMetaData().getSuffix();
    }
}
