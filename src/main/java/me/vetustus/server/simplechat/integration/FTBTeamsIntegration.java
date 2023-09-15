package me.vetustus.server.simplechat.integration;

import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.api.Team;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Optional;

public class FTBTeamsIntegration {

    public static String getTeam(ServerPlayerEntity player) {
        Optional<Team> team = FTBTeamsAPI.api().getManager().getTeamForPlayerID(player.getUuid());

        if (team == null || team.get().isPlayerTeam()) {
            return "";
        }

        return team.get().getName().getString();
    }

}
