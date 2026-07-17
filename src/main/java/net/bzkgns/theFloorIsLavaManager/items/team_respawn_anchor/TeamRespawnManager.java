package net.bzkgns.theFloorIsLavaManager.items.team_respawn_anchor;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaManager;
import net.bzkgns.theFloorIsLavaManager.teams.TeamData;
import net.bzkgns.theFloorIsLavaManager.teams.TeamManager;
import net.bzkgns.theFloorIsLavaManager.utils.TextUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.*;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;

@SuppressWarnings("UnstableApiUsage")
public class TeamRespawnManager {
    private static TeamRespawnManager instance;

    private final TheFloorIsLavaManager plugin = TheFloorIsLavaManager.getInstance();

    private final Map<String, Location> respawnPoints = new HashMap<>(); // Map to store team names and their respawn points
    private final Map<String, Entity> respawnEffects = new HashMap<>(); // Map to store team names and their respawn points
    public static final Material respawnAnchorMaterial = Material.GLASS; // Material for the respawn anchor

    private TeamRespawnManager() {
        // Private constructor to prevent instantiation
    }

    public static TeamRespawnManager getInstance() {
        if (instance == null) {
            instance = new TeamRespawnManager();
        }
        return instance;
    }

    public void setRespawnPoint(String teamName, Location location) {

        if (respawnPoints.containsKey(teamName)){
            Location oldRespawnPoint = respawnPoints.get(teamName);
            World world = location.getWorld();
            if(world == null){
                plugin.getLogger().warning("world is null while trying to remove old respawn point for team: " + teamName);
                return;
            }
            Block oldRespawnBlock = world.getBlockAt(oldRespawnPoint.getBlockX(), oldRespawnPoint.getBlockY(), oldRespawnPoint.getBlockZ());
            if(oldRespawnBlock.getType() == respawnAnchorMaterial){
                oldRespawnBlock.setType(Material.AIR);
            }
        }

        respawnPoints.put(teamName, location);
        location = location.clone().add(0.5,1.5,0.5);

        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        item.setData(DataComponentTypes.PROFILE, ResolvableProfile.resolvableProfile().name("MrCodingMen"));

        ItemDisplay playerHeadBlockDisplay = location.getWorld().spawn(location, ItemDisplay.class, itemDisplay -> {
            itemDisplay.setItemStack(item);
            itemDisplay.setTransformation(new Transformation(
                    new Vector3f(0, 0, 0),
                    new Quaternionf(0, 0, 0, 1),
                    new Vector3f(1, 1, 1),
                    new Quaternionf(0, 0, 0, 1))
            );
            itemDisplay.setInvulnerable(true);
            itemDisplay.setGravity(false);
            itemDisplay.setCustomNameVisible(false);
            itemDisplay.setPersistent(true);
        });
        ArmorStand particleArmorStand = location.getWorld().spawn(location, ArmorStand.class, armorStand -> {
            armorStand.setInvulnerable(true);
            armorStand.setVisible(false);
            armorStand.setGravity(false);
            armorStand.setCustomNameVisible(false);
            armorStand.setPersistent(true);
            armorStand.addScoreboardTag("tfl_respawn_team_effect_armorstand");
        });

        TextDisplay textDisplay = location.getWorld().spawn(location.clone().add(0, 1.5, 0), TextDisplay.class, display -> {
            display.text(Component.text("Ancre ce réaparition de l'équipe\n").append(Component.text(teamName).color(TeamManager.getInstance().getTeam(teamName).getColor())));
            display.setInvulnerable(true);
            display.setGravity(false);
            display.setCustomNameVisible(false);
            display.setPersistent(true);
            display.setBackgroundColor(Color.fromARGB(0)); // Transparent background
            display.setBillboard(Display.Billboard.CENTER);
            display.setTransformation(new Transformation(
                    new Vector3f(0, 0, 0),
                    new Quaternionf(0, 0, 0, 1),
                    new Vector3f(.5f, .5f, .5f),
                    new Quaternionf(0, 0, 0, 1)));
        });
        ArmorStand baseArmorStand = location.getWorld().spawn(location, ArmorStand.class, armorStand -> {
            armorStand.setVisible(false);
            armorStand.setInvulnerable(true);
            armorStand.setGravity(false);
            armorStand.setMarker(true);
            armorStand.setCustomNameVisible(false);
            armorStand.setPersistent(true);
            armorStand.addScoreboardTag("tfl_respawn_team_effect");
            armorStand.addPassenger(playerHeadBlockDisplay);
            armorStand.addPassenger(particleArmorStand);
            armorStand.addPassenger(textDisplay);
        });
        if (respawnEffects.containsKey(teamName)) {
            Entity oldEffect = respawnEffects.get(teamName);
            if (oldEffect != null && !oldEffect.isDead()) {
                recursivelyRemovePassengers(oldEffect);
            }
        }
        respawnEffects.put(teamName, baseArmorStand);
    }

    private void recursivelyRemovePassengers(Entity entity) {
        for (Entity passenger : entity.getPassengers()) {
            recursivelyRemovePassengers(passenger);
            passenger.remove();
        }
    }

    public Location getRespawnPoint(String teamName) {
        return respawnPoints.get(teamName);
    }

    public void removeRespawnPoint(String teamName) {
        Location respawnLocation = respawnPoints.get(teamName);
        World world = respawnLocation.getWorld();
        if(world == null){
            plugin.getLogger().warning("Game world is null while trying to remove respawn point for team: " + teamName);
            return;
        }
        Block respawnBlock = world.getBlockAt(respawnLocation.getBlockX(), respawnLocation.getBlockY(), respawnLocation.getBlockZ());
        System.out.println(respawnBlock);
        if(respawnBlock.getType() == respawnAnchorMaterial){
            respawnBlock.setType(Material.AIR);
        }
        respawnPoints.remove(teamName);
        if (respawnEffects.containsKey(teamName)) {
            Entity oldEffect = respawnEffects.get(teamName);
            if (oldEffect != null && !oldEffect.isDead()) {
                recursivelyRemovePassengers(oldEffect);
                respawnEffects.remove(teamName);
            }
        }
    }

    public boolean hasRespawnPoint(String teamName) {
        return respawnPoints.containsKey(teamName);
    }

    public String getTeamNameByRespawnPoint(Location respawnPoint) {
        for (Map.Entry<String, Location> entry : respawnPoints.entrySet()) {
            if (entry.getValue().equals(respawnPoint)) {
                return entry.getKey();
            }
        }
        return null; // Return null if no team is found for the given respawn point
    }

    public Location getPlayerTeamRespawnPosition(UUID playerUUID) {
        TeamData playerTeam = TeamManager.getInstance().getPlayerTeam(playerUUID);
        if (playerTeam != null) {
            return getRespawnPoint(playerTeam.getId()).clone().add(.5, 1, .5); // Return the respawn point for the player's team, adjusted to be above the block

        }
        return null; // Return null if the player is not in a team or no respawn point is set
    }

    public void checkRespawnPointValidity(){
        for (Map.Entry<String, Location> entry : respawnPoints.entrySet()) {
            String teamName = entry.getKey();
            Location respawnPoint = entry.getValue();
            if (respawnPoint.getY() < plugin.getGameManager().getDangerManager().getDangerLevel()){
                removeRespawnPoint(teamName);
                TeamManager.broadcastTeamMessage(TextUtils.errorMessage("Votre point de respawn a été supprimé car il est sous le niveau de danger actuel !"), TeamManager.getInstance().getTeam(teamName));
                plugin.getLogger().info("Respawn point for team " + teamName + " has been removed due to being below the danger level.");
            }
        }
    }

    public Map<String, Location> getRespawnPoints() {
        return respawnPoints;
    }
}
