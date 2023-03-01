package me.vetustus.server.simplechat.integration;

import dev.ftb.mods.ftbteams.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.data.Team;
import net.minecraft.server.network.ServerPlayerEntity;

public class FTBTeamsIntegration {

    public static String getTeam(ServerPlayerEntity player) {
        Team team = FTBTeamsAPI.getPlayerTeam(player.getUuid());

        if (team == null || team.getType().isPlayer()) {
            return "";
        }

        return team.getDisplayName();
    }

}
