package net.bzkgns.theFloorIsLava.statistics.visual;

import net.bzkgns.theFloorIsLava.TheFloorIsLava;
import net.bzkgns.theFloorIsLava.statistics.StatisticType;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class RankingInstance {

    private final TheFloorIsLava plugin = TheFloorIsLava.getInstance();

    private static final Map<Integer, RankingInstance> instances = new HashMap<>();
    private static int nextId = 0;

    private static int addInstance(RankingInstance instance) {
        int id = nextId++;
        instances.put(id, instance);
        return id;
    }

    private static RankingInstance getInstanceById(int id) {
        return instances.get(id);
    }

    public static RankingInstance getRankingInstanceFromEntity(Entity entity) {
        if (entity.getPersistentDataContainer().has(new NamespacedKey(TheFloorIsLava.getInstance(), "rankingId"), org.bukkit.persistence.PersistentDataType.INTEGER)) {
            PersistentDataContainer dataContainer = entity.getPersistentDataContainer();
            if (dataContainer.has(new NamespacedKey(TheFloorIsLava.getInstance(), "rankingId"), org.bukkit.persistence.PersistentDataType.INTEGER)) {
                Integer id = dataContainer.get(new NamespacedKey(TheFloorIsLava.getInstance(), "rankingId"), org.bukkit.persistence.PersistentDataType.INTEGER);
                if (id != null)
                    return getInstanceById(id);
            }
        }
        return null;
    }

    private final Interaction interactionRight;
    private final Interaction interactionLeft;
    private final List<TextDisplay> rankDisplay = new ArrayList<>();
    private final TextDisplay textDisplayLeft;
    private final TextDisplay textDisplayRight;
    private final TextDisplay textDisplayType;
    private final Location location;
    private StatisticType statisticType = StatisticType.GAMES_PLAYED;
    public RankingInstance(@NotNull Location location){
        int currentId = RankingInstance.addInstance(this);
        this.interactionRight = location.getWorld().spawn(location.clone().add(1,0,0), Interaction.class, interaction -> {
            interaction.setInteractionHeight(.25f);
            interaction.setInteractionWidth(.25f);
            interaction.getScoreboardTags().add("rankingRight");
            interaction.getPersistentDataContainer().set(new NamespacedKey(plugin, "rankingId"), org.bukkit.persistence.PersistentDataType.INTEGER, currentId);
        });
        this.interactionLeft = location.getWorld().spawn(location.clone().add(-1,0,0), Interaction.class, interaction -> {
            interaction.setInteractionHeight(.25f);
            interaction.setInteractionWidth(.25f);
            interaction.getScoreboardTags().add("rankingLeft");
            interaction.getPersistentDataContainer().set(new NamespacedKey(plugin, "rankingId"), org.bukkit.persistence.PersistentDataType.INTEGER, currentId);
        });

        this.location = location.clone();

        this.textDisplayLeft = location.getWorld().spawn(location.clone().add(1,0,0), TextDisplay.class, textDisplay -> {
            textDisplay.text(Component.text("→"));
            textDisplay.getPersistentDataContainer().set(new NamespacedKey(plugin, "rankingId"), org.bukkit.persistence.PersistentDataType.INTEGER, currentId);
        });

        this.textDisplayRight = location.getWorld().spawn(location.clone().add(-1,0,0), TextDisplay.class, textDisplay -> {
            textDisplay.text(Component.text("←"));
            textDisplay.getPersistentDataContainer().set(new NamespacedKey(plugin, "rankingId"), org.bukkit.persistence.PersistentDataType.INTEGER, currentId);
        });

        this.textDisplayType = location.getWorld().spawn(location.clone().add(0,0,0), TextDisplay.class, textDisplay -> {
            textDisplay.text(Component.text(statisticType.getColumnName()));
            textDisplay.getPersistentDataContainer().set(new NamespacedKey(plugin, "rankingId"), org.bukkit.persistence.PersistentDataType.INTEGER, currentId);
        });
        this.updateDisplay();
    }

    public void updateDisplay(){
        textDisplayType.text(Component.text(statisticType.getColumnName()));
        LinkedHashMap<UUID, Integer> top = plugin.getStatisticsManager().getTop(statisticType, 10);
        for (TextDisplay display : rankDisplay) {
            display.remove();
        }
        rankDisplay.clear();
        int i = 0;
        for (Map.Entry<UUID, Integer> entry : top.entrySet()) {
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player != null) {
                Location displayLocation = location.clone().add(0, 0.5 + i * 0.3, 0);
                int finalI = i;
                TextDisplay display = location.getWorld().spawn(displayLocation, TextDisplay.class, textDisplay -> {
                    textDisplay.text(Component.text((finalI + 1) + ". " + player.getName() + ": " + entry.getValue()));
                    textDisplay.getPersistentDataContainer().set(new NamespacedKey(plugin, "rankingId"), org.bukkit.persistence.PersistentDataType.INTEGER, RankingInstance.addInstance(this));
                });
                rankDisplay.add(display);
                i++;
            }
        }

    }

    public void nextType(){
        int size = StatisticType.values().length;
        this.statisticType =  StatisticType.values()[(this.statisticType.ordinal()+1)%size];
        updateDisplay();
    }

    public void previousType(){
        int size = StatisticType.values().length;
        this.statisticType =  StatisticType.values()[(this.statisticType.ordinal()+1+size)%size];
        updateDisplay();
    }

    public void destroy(){
        interactionRight.remove();
        interactionLeft.remove();
        for (TextDisplay display : rankDisplay) {
            display.remove();
        }
        textDisplayType.remove();
        textDisplayRight.remove();
        textDisplayLeft.remove();
    }
}
