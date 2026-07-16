package net.bzkgns.theFloorIsLavaManager.items.team_respawn_anchor;

import net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaManager;
import net.bzkgns.theFloorIsLavaManager.teams.TeamData;
import net.bzkgns.theFloorIsLavaManager.teams.TeamManager;
import net.bzkgns.theFloorIsLavaManager.utils.TextUtils;
import net.minecraft.core.BlockPos;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeamRespawnManager {
    private static TeamRespawnManager instance;

    private final TheFloorIsLavaManager plugin = TheFloorIsLavaManager.getInstance();

    private final Map<String, BlockPos> respawnPoints = new HashMap<>(); // Map to store team names and their respawn points

    private TeamRespawnManager() {
        // Private constructor to prevent instantiation
    }

    public static TeamRespawnManager getInstance() {
        if (instance == null) {
            instance = new TeamRespawnManager();
        }
        return instance;
    }

    public void setRespawnPoint(String teamName, BlockPos respawnPoint) {
        respawnPoints.put(teamName, respawnPoint);
    }

    public BlockPos getRespawnPoint(String teamName) {
        return respawnPoints.get(teamName);
    }

    public void removeRespawnPoint(String teamName) {
        World game_world = plugin.getWorldManager().getGameWorld();
        if(game_world == null){
            plugin.getLogger().warning("Game world is null while trying to remove respawn point for team: " + teamName);
            return;
        }
        Block respawnBlock = game_world.getBlockAt(respawnPoints.get(teamName).getX(), respawnPoints.get(teamName).getY(), respawnPoints.get(teamName).getZ());
        if(respawnBlock.getType() == Material.RESPAWN_ANCHOR){
            respawnBlock.setType(Material.AIR);
        }
        respawnPoints.remove(teamName);
    }

    public boolean hasRespawnPoint(String teamName) {
        return respawnPoints.containsKey(teamName);
    }

    public String getTeamNameByRespawnPoint(BlockPos respawnPoint) {
        for (Map.Entry<String, BlockPos> entry : respawnPoints.entrySet()) {
            if (entry.getValue().equals(respawnPoint)) {
                return entry.getKey();
            }
        }
        return null; // Return null if no team is found for the given respawn point
    }

    public BlockPos getPlayerTeamRespawnPosition(UUID playerUUID) {
        System.out.println("Checking respawn point for player UUID: " + playerUUID);
        TeamData playerTeam = TeamManager.getInstance().getPlayerTeam(playerUUID);
        if (playerTeam != null) {
            plugin.getLogger().info("Player " + playerUUID + " is in team: " + playerTeam.getName());
            return getRespawnPoint(playerTeam.getName());

        }
        return null; // Return null if the player is not in a team or no respawn point is set
    }

    public void checkRespawnPointValidity(){
        for (Map.Entry<String, BlockPos> entry : respawnPoints.entrySet()) {
            String teamName = entry.getKey();
            BlockPos respawnPoint = entry.getValue();
            if (respawnPoint.getY() < plugin.getGameManager().getDangerManager().getDangerLevel()){
                removeRespawnPoint(teamName);
                TeamManager.broadcastTeamMessage(TextUtils.errorMessage("Votre point de respawn a été supprimé car il est sous le niveau de danger actuel !"), TeamManager.getInstance().getTeam(teamName));
                plugin.getLogger().info("Respawn point for team " + teamName + " has been removed due to being below the danger level.");
                plugin.getWorldManager().getGameWorld().getBlockAt(respawnPoint.getX(),respawnPoint.getY(),respawnPoint.getZ()).setType(Material.AIR);
            }
        }
    }

    public Map<String, BlockPos> getRespawnPoints() {
        return respawnPoints;
    }
}
