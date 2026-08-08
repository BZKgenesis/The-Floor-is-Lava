package net.bzkgns.theFloorIsLava.items.abilities;

import net.bzkgns.theFloorIsLava.config.items.ItemsConfig;
import net.bzkgns.theFloorIsLava.config.ConfigRegistry;
import net.bzkgns.theFloorIsLava.teams.TeamData;
import net.bzkgns.theFloorIsLava.teams.TeamManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.type.Campfire;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class HealCampInstance {
    private static final ItemsConfig itemsConfig = (ItemsConfig) ConfigRegistry.getConfigManager("items").getConfig();
    private static final Vector3fc WEIRD_TRANSFORMATION_TRANSLATION = new Vector3f(-0.125f, 0.01f, 0.625f + 0.25f*0.0625f);
    private static final Vector3fc WEIRD_TRANSFORMATION_SCALE = new Vector3f(10f, 10f*0.57f,10f);

    private final TextDisplay textDisplay;
    private final AreaEffectCloud areaEffectCloud;
    private final TextDisplay timeTextDisplay;
    private Integer aliveTicks = itemsConfig.getHealCampMaxAliveTicks(); // 5 minutes in ticks
    private final Location location;

    public HealCampInstance(Location location, float size, Player player) {
        TeamData data = TeamManager.getInstance().getPlayerTeam(player.getUniqueId());
        NamedTextColor color = NamedTextColor.GRAY;
        if (data != null) color = data.getColor();
        World world = location.getWorld();
        this.textDisplay = world.spawn(location.clone().add(0.5,0.01f,0.5), TextDisplay.class);
        this.textDisplay.setBackgroundColor(Color.fromARGB(0));

        this.textDisplay.setTransformation(new Transformation(new Vector3f(WEIRD_TRANSFORMATION_TRANSLATION).mul(size) ,
                new AxisAngle4f((float) -Math.toRadians(90),1,0,0),
                new Vector3f(WEIRD_TRANSFORMATION_SCALE).mul(size),
                new AxisAngle4f()));

        this.textDisplay.setTextOpacity((byte) 100);
        this.textDisplay.text(Component.text("▇", color));

        this.areaEffectCloud = world.spawn(location.clone().add(0.5f,0,0.5f), AreaEffectCloud.class);
        this.areaEffectCloud.setBasePotionType(PotionType.HEALING);
        this.areaEffectCloud.setReapplicationDelay(itemsConfig.getHealCampApplicationDelay());
        this.areaEffectCloud.setWaitTime(0);
        this.areaEffectCloud.setDuration(itemsConfig.getHealCampMaxAliveTicks()); // 5 minutes
        this.areaEffectCloud.setRadius(size/2f);
        if (data != null) data.getVanillaTeam().addEntities(areaEffectCloud);

        this.timeTextDisplay = world.spawn(location.clone().add(0.5f, 1f, 0.5f), TextDisplay.class);
        this.timeTextDisplay.text(Component.text("", color));
        this.timeTextDisplay.setBillboard(Display.Billboard.VERTICAL);

        this.location = location;
    }

    public void remove() {
        textDisplay.remove();
        areaEffectCloud.remove();
        timeTextDisplay.remove();
        if (location.getBlock().getType() == Material.CAMPFIRE) {
            Campfire campfire = (Campfire) location.getBlock().getBlockData();
            campfire.setLit(false);
            location.getBlock().setBlockData(campfire);
        }
    }

    public void tick() {
        aliveTicks--;
        int minutes = aliveTicks / 1200; // 20 ticks per second * 60 seconds
        int seconds = (aliveTicks / 20) % 60; // 20 ticks per second
        String timeString = String.format("%d:%02d", minutes, seconds);
        timeTextDisplay.text(Component.text(timeString, timeTextDisplay.text().color()));
    }

    public boolean isAlive() {
        return aliveTicks > 0;
    }
}
