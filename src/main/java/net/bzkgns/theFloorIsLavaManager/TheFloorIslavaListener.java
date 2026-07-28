package net.bzkgns.theFloorIsLavaManager;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DyedItemColor;
import net.bzkgns.theFloorIsLavaManager.items.*;
import net.bzkgns.theFloorIsLavaManager.items.items.*;
import net.bzkgns.theFloorIsLavaManager.items.abilities.TeamRespawnManager;
import net.bzkgns.theFloorIsLavaManager.kits.KitManager;
import net.bzkgns.theFloorIsLavaManager.managers.DangerManager;
import net.bzkgns.theFloorIsLavaManager.managers.GameManager;
import net.bzkgns.theFloorIsLavaManager.managers.GameState;
import net.bzkgns.theFloorIsLavaManager.sidebar.provider.GameSidebarProvider;
import net.bzkgns.theFloorIsLavaManager.sidebar.provider.LobbySidebarProvider;
import net.bzkgns.theFloorIsLavaManager.statistics.StatisticType;
import net.bzkgns.theFloorIsLavaManager.teams.TeamData;
import net.bzkgns.theFloorIsLavaManager.teams.TeamManager;
import net.bzkgns.theFloorIsLavaManager.utils.BlockUtils;
import net.bzkgns.theFloorIsLavaManager.lang.Messages;
import net.bzkgns.theFloorIsLavaManager.utils.menu.MenuHolder;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.util.TriState;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static net.bzkgns.theFloorIsLavaManager.utils.BlockUtils.getWoolBlockByPlayer;

@SuppressWarnings("UnstableApiUsage")
public class TheFloorIslavaListener implements Listener {

    private final TheFloorIsLavaManager plugin;

    private final Map<UUID, Location> deathLocations = new HashMap<>();


    private static final List<Material> DYABLE_ITEMS =
            List.of(
                    Material.LEATHER_HELMET,
                    Material.LEATHER_CHESTPLATE,
                    Material.LEATHER_LEGGINGS,
                    Material.LEATHER_BOOTS
            );


    public TheFloorIslavaListener(TheFloorIsLavaManager plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockForm(BlockFormEvent event) {
        Material newType = event.getNewState().getType();

        // Empêche l'eau/lave de créer de l'obsidienne ou du cobble
        if (newType == Material.OBSIDIAN || newType == Material.COBBLESTONE || newType == Material.STONE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        player.setFlyingFallDamage(TriState.TRUE);
        KitManager.getInstance().assignKitToPlayer(player.getUniqueId(), "default");
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Player p = event.getPlayer();
            World w = p.getWorld();

            p.resetPlayerTime(); // ou setPlayerTime(w.getTime(), false)
            p.setPlayerWeather(w.hasStorm() ? WeatherType.DOWNFALL : WeatherType.CLEAR);
        }, 2L);

        String url = plugin.getResourcePackManager().getUrl();
        String sha1 = plugin.getResourcePackManager().getSha1();
        if (url == null || url.isEmpty() || sha1 == null || sha1.isEmpty()) {
            plugin.getLogger().warning("Resource pack URL or SHA-1 is not set. Players will not receive the resource pack.");
        } else {
            plugin.getLogger().info("Resource pack URL: " + url);
            plugin.getLogger().info("Resource pack SHA-1: " + sha1);
            player.setResourcePack(
                    url,
                    sha1,
                    true,
                    Messages.component(player, "resourcepack.required")
            );
        }
        plugin.getLogger().info("Player " + player.getName() + " joined. Total world time: " + event.getPlayer().getStatistic(Statistic.TOTAL_WORLD_TIME));
        plugin.getGameManager().addPlayerToBossBar(player);

        switch (plugin.getGameManager().getState()){
            case LOBBY -> {
                GameManager.initLobbyPlayer(player);
                player.give(new TeamManagerItem().giveItem(player));
                player.give(new GiveAllItem().giveItem(player));
                plugin.getSidebarManager().show(player, new LobbySidebarProvider(player));
            }
            case RUNNING -> {
                plugin.getSidebarManager().show(player, new GameSidebarProvider(player));
                event.getPlayer().setAllowFlight(false);
                if (plugin.getGameManager().isPlayerInGame(event.getPlayer())){
                    event.getPlayer().setGameMode(GameMode.SURVIVAL);
                    KitManager.getInstance().applyKitToPlayerAttributeOnly(player);
                    if (!event.getPlayer().getWorld().equals(plugin.getWorldManager().getGameWorld())) {
                        event.getPlayer().teleport(plugin.getWorldManager().getDefaultSpawnLocation());
                        event.getPlayer().setRespawnLocation(plugin.getWorldManager().getDefaultSpawnLocation(), true);
                    }
                } else {
                    event.getPlayer().teleport(plugin.getWorldManager().getPreGameSpawnLocation());
                    event.getPlayer().setRespawnLocation(plugin.getWorldManager().getPreGameSpawnLocation(), true);
                    event.getPlayer().setGameMode(GameMode.SPECTATOR);
                }
            }
            case ENDING -> event.getPlayer().setGameMode(GameMode.SPECTATOR);
        }
        discoverRecipes(event.getPlayer());
        if(event.getPlayer().getWorld().equals(plugin.getServer().getWorld("minecraft:overworld"))){
            Messages.send(event.getPlayer(), "error.not_here");
        }
    }

    private void discoverRecipes(Player player){
        for (String recipe_key : TheFloorIsLavaManager.RECIPES_KEY){
            player.discoverRecipe(new NamespacedKey(plugin,recipe_key));
        }
    }

    @EventHandler
    public void onDragItem(InventoryDragEvent event){
        if (event.getInventory().getHolder() instanceof MenuHolder){
            for (int slot : event.getRawSlots()){
                if (slot > 0 && slot < event.getInventory().getSize()){
                    event.setCancelled(true);
                }
            }
        }
    }


    @EventHandler
    public void onPlayerEquip(PlayerArmorChangeEvent event){
        if (DYABLE_ITEMS.contains(event.getNewItem().getType())){
            ItemStack armor = event.getNewItem();
            Player player = event.getPlayer();
            TeamData teamData = TeamManager.getInstance().getPlayerTeam(player.getUniqueId());
            if (teamData != null){
                armor.setData(DataComponentTypes.DYED_COLOR, DyedItemColor.dyedItemColor(Color.fromRGB(teamData.getColor().value())));
            }else{
                armor.setData(DataComponentTypes.DYED_COLOR, DyedItemColor.dyedItemColor(Color.fromRGB(Color.SILVER.asRGB())));
            }
            player.getInventory().setItem(event.getSlot(), armor);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        plugin.getStatisticsManager().load(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getStatisticsManager().unload(event.getPlayer());
    }

    @EventHandler
    public void onPlaced(BlockPlaceEvent event){
        if (!BlockUtils.canPlaceBlock(event.getBlockPlaced().getLocation())) {
            event.setCancelled(true);
            return;
        }
        Player player = event.getPlayer();
        Block block = event.getBlockPlaced();
        if (block.getType().toString().endsWith("WOOL")){
            block.setType(getWoolBlockByPlayer(player));
        }
    }

    @EventHandler
    public void onBroke(BlockBreakEvent event){
        if (!BlockUtils.canPlaceBlock(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickupWool(PlayerAttemptPickupItemEvent e){
        Item item = e.getItem();
        ItemStack stack = item.getItemStack();
        if (stack.getType().toString().endsWith("WOOL") && !(stack.getData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE) != null && Boolean.TRUE.equals(stack.getData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE)))){
            item.setItemStack(new ItemStack(Material.LIGHT_GRAY_WOOL, stack.getAmount()));
        }
    }
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        filterProtectedBlocks(event.blockList());
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        filterProtectedBlocks(event.blockList());
    }

    private void filterProtectedBlocks(List<Block> blocks) {
        blocks.removeIf(block ->
                !BlockUtils.canPlaceBlock(block.getLocation())
        );
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDamage(EntityDamageEvent e){
        if (e.getEntity() instanceof Player && e.getDamageSource().getCausingEntity()!=null){
            if (e.getDamageSource().getCausingEntity() instanceof Player){
                if (!TheFloorIsLavaManager.pvp){
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onFallDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player &&
                event.getCause() == EntityDamageEvent.DamageCause.FALL) {

            player.getInventory().getBoots();
            if (player.getInventory().getBoots().getType() == Material.LEATHER_BOOTS) {

                event.setDamage(event.getDamage() * plugin.getFallDamageReduction()); // 80% de réduction
            }
        }
    }

    @EventHandler
    public void OnCraft(CraftItemEvent event){
        if (event.getWhoClicked() instanceof Player player){
            if (event.getCurrentItem() == null) return;
            CustomItem customItem = ItemManager.getAssociatedCustomItem(event.getCurrentItem());
            if (customItem != null){
                ItemStack newItem = customItem.giveItem(player);
                newItem.setAmount(event.getCurrentItem().getAmount());
                event.setCurrentItem(newItem);
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        plugin.getLogger().info("OnDeath with respawn");
        if (plugin.getGameManager().getNoRespawn()){
            plugin.getLogger().info("OnDeath no respawn");
            Player player = event.getEntity();
            deathLocations.put(
                    player.getUniqueId(),
                    player.getLocation().clone()
            );
            event.getEntity().getWorld().strikeLightningEffect(event.getEntity().getLocation());

            if (event.getDamageSource().getCausingEntity() instanceof Player assassin){
                plugin.getStatisticsManager().increment(assassin, StatisticType.KILLS);
                for (int slotId = 0; slotId < 36; slotId++){
                    ItemStack stack = player.getInventory().getItem(slotId);
                    if (stack!=null && shouldGiveItem(stack)){
                        assassin.give(stack);
                    }
                }
                event.setKeepInventory(true);
                event.getDrops().clear(); // si tu veux pas qu'ils droppent, sinon retire
            }

            event.getEntity().setGameMode(GameMode.SPECTATOR);
            Bukkit.getScheduler().runTaskLater(plugin, () -> event.getEntity().spigot().respawn(), 4L);
        }

        if (plugin.getGameManager().getState() == GameState.RUNNING && plugin.getGameManager().isPlayerInGame(event.getEntity())){
            plugin.getStatisticsManager().increment(event.getEntity(), StatisticType.DEATHS);
            TeamData team = TeamManager.getInstance().getPlayerTeam(event.getEntity().getUniqueId());
            if (team != null && team.isEliminated()) {
                plugin.getLogger().info("Team " + team.getName() + " eliminated.");
                Messages.broadcastPing("team.eliminated",
                                Placeholder.component("team_name", team.getName())
                );
            }
        }
        if (plugin.getGameManager().isGameWinning()){
            TeamData winningTeam = TeamManager.getInstance().getTeamAlive().getFirst();
            // NOTE i18n : TextUtils.broadcastMessage(Component) envoie un unique Component déjà construit à tous
            // les joueurs connectés : la langue est donc figée (résolue ici via Bukkit.getServer(), donc en_us
            // par défaut) pour tout le monde, quelle que soit la locale de chaque joueur. Pour une vraie
            // localisation par joueur, il faudrait adapter TextUtils.broadcastMessage() pour qu'elle construise
            // un Component par joueur via Messages.component(player, ...).
            if (winningTeam != null) {
                winningTeam.getMembers().forEach(uuid ->{
                        Player player = Bukkit.getPlayer(uuid);
                        if (player != null)
                            plugin.getStatisticsManager().increment(player, StatisticType.GAMES_WON);
                });
                Messages.broadcast( "game.end_team_won",
                                Placeholder.component("team_name", winningTeam.getName())
                );
            }else{
                Messages.broadcast("game.end_no_winner");
            }
            plugin.getGameManager().endGame();
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        Location deathLocation = deathLocations.remove(player.getUniqueId());
        Location respawnPos = TeamRespawnManager.getInstance().getPlayerTeamRespawnPosition(player.getUniqueId());

        switch (TheFloorIsLavaManager.getInstance().getGameManager().getState()){
            case LOBBY -> {
                if (respawnPos != null){
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        player.teleport(respawnPos);
                        player.setGameMode(GameMode.SURVIVAL);
                        player.setAllowFlight(true);
                    });
                    return;
                }
                if (deathLocation != null){
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        player.teleport(deathLocation);
                        player.setGameMode(GameMode.SURVIVAL);
                        player.setAllowFlight(true);
                    });
                    return;
                }
                Bukkit.getScheduler().runTask(plugin, () -> {
                    player.teleport(TheFloorIsLavaManager.getInstance().getWorldManager().getLobbySpawnLocation());
                    player.setGameMode(GameMode.SURVIVAL);
                    player.setAllowFlight(true);
                });
            }
            case RUNNING  -> {
                if (respawnPos != null){
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        player.teleport(respawnPos);
                        player.setGameMode(GameMode.SURVIVAL);
                        KitManager.getInstance().applyKitToPlayerAttributeOnly(player);
                    });
                    return;
                }
                switch (TheFloorIsLavaManager.getInstance().getGameManager().getDangerManager().getState()){
                    case DangerManager.DangerState.PREPARATION -> Bukkit.getScheduler().runTask(plugin, () -> {
                        Location respawnLocation = player.getRespawnLocation();
                        if (respawnLocation != null){
                            player.teleport(respawnLocation);
                        }else{
                            player.teleport(TheFloorIsLavaManager.getInstance().getWorldManager().getDefaultSpawnLocation());
                        }
                        player.setGameMode(GameMode.SURVIVAL);
                    });
                    case DangerManager.DangerState.RISING -> Bukkit.getScheduler().runTask(plugin, () -> {
                        if (deathLocation != null){
                            player.teleport(deathLocation);
                        }else{
                            player.teleport(TheFloorIsLavaManager.getInstance().getWorldManager().getDefaultSpawnLocation());
                        }
                        player.setGameMode(GameMode.SPECTATOR);
                    });
                }
            }
            case ENDING -> Bukkit.getScheduler().runTask(plugin, () -> {
                if (deathLocation != null) {
                    player.teleport(deathLocation);
                } else {
                    player.teleport(TheFloorIsLavaManager.getInstance().getWorldManager().getDefaultSpawnLocation());
                }
                player.setGameMode(GameMode.SPECTATOR);
            });
        }
    }

    private boolean shouldGiveItem(ItemStack stack){
        List<Material> materials = List.of(Material.DIAMOND, Material.GOLD_INGOT,Material.IRON_INGOT,Material.COPPER_INGOT,Material.AMETHYST_SHARD,Material.EMERALD,Material.REDSTONE,Material.LAPIS_LAZULI,Material.EGG,Material.SNOWBALL,Material.COBBLESTONE,Material.DIRT,Material.BAKED_POTATO,Material.GRAY_WOOL);
        return materials.contains(stack.getType()) || ItemManager.getAssociatedCustomItem(stack) != null;
    }
}