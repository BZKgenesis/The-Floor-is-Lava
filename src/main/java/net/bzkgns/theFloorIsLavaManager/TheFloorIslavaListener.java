package net.bzkgns.theFloorIsLavaManager;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.bzkgns.theFloorIsLavaManager.items.*;
import net.bzkgns.theFloorIsLavaManager.items.items.*;
import net.bzkgns.theFloorIsLavaManager.items.abilities.TeamRespawnManager;
import net.bzkgns.theFloorIsLavaManager.kits.KitManager;
import net.bzkgns.theFloorIsLavaManager.managers.GameManager;
import net.bzkgns.theFloorIsLavaManager.managers.GameState;
import net.bzkgns.theFloorIsLavaManager.statistics.StatisticType;
import net.bzkgns.theFloorIsLavaManager.teams.TeamData;
import net.bzkgns.theFloorIsLavaManager.teams.TeamGUI;
import net.bzkgns.theFloorIsLavaManager.teams.TeamManager;
import net.bzkgns.theFloorIsLavaManager.utils.BlockUtils;
import net.bzkgns.theFloorIsLavaManager.shop.ShopGUI;
import net.bzkgns.theFloorIsLavaManager.lang.Messages;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.util.TriState;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

import static net.bzkgns.theFloorIsLavaManager.utils.BlockUtils.getWoolBlockByPlayer;

@SuppressWarnings("UnstableApiUsage")
public class TheFloorIslavaListener implements Listener {

    private final TheFloorIsLavaManager plugin;

    private final Map<UUID, Location> deathLocations = new HashMap<>();


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

        player.setResourcePack(
                plugin.getResourcePackManager().getUrl(),
                plugin.getResourcePackManager().getSha1(),
                true,
                Messages.component(player, "resourcepack.required")
        );
        plugin.getLogger().info("Player " + player.getName() + " joined. Total world time: " + event.getPlayer().getStatistic(Statistic.TOTAL_WORLD_TIME));
        plugin.getGameManager().addPlayerToBossBar(player);

        switch (plugin.getGameManager().getState()){
            case LOBBY -> {
                GameManager.initLobbyPlayer(event.getPlayer());
                event.getPlayer().give(new TeamManagerItem().giveItem(event.getPlayer()));
                event.getPlayer().give(new GiveAllItem().giveItem(event.getPlayer()));
            }
            case RUNNING -> {
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
    public void onTeamManagerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return; // Ignore la main secondaire
        }

        Action action = event.getAction();

        if (action != Action.RIGHT_CLICK_AIR &&
                action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (!event.hasItem()) return;

        ItemStack item = event.getItem();
        if (item == null) return;
        if (!new TeamManagerItem().isItem(item)) return;
        if (plugin.getGameManager().getState()==GameState.RUNNING) return;

        event.setCancelled(true);
        TeamGUI.openMainMenu(event.getPlayer());
    }

    @EventHandler
    public void onShopInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return; // Ignore la main secondaire
        }

        Action action = event.getAction();

        if (action != Action.RIGHT_CLICK_AIR &&
                action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (!event.hasItem()) return;

        ItemStack item = event.getItem();
        if (item == null) return;
        if (!new ShopItem().isItem(item)) return;

        event.setCancelled(true);

        ShopGUI.open(event.getPlayer(),0);
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
                if (deathLocation != null){
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        player.teleport(deathLocation);
                        player.setGameMode(GameMode.SPECTATOR);
                    });
                    return;
                }
                Bukkit.getScheduler().runTask(plugin, () -> {
                    player.teleport(TheFloorIsLavaManager.getInstance().getWorldManager().getPreGameSpawnLocation());
                    player.setGameMode(GameMode.SPECTATOR);
                });
            }
        }
    }


    @EventHandler
    public void onEggBridgeLaunch(ProjectileLaunchEvent event){
        if (!(event.getEntity() instanceof Egg egg)) return;
        if (!(egg.getShooter() instanceof Player p)) return;

        ItemStack item = p.getInventory().getItemInMainHand();
        if (!new EggBridgeItem().isItem(item)) return;
        event.getEntity().getPersistentDataContainer().set(
                new NamespacedKey(JavaPlugin.getPlugin(TheFloorIsLavaManager.class),"eggBridgeEntity"),
                PersistentDataType.STRING,
                "eggBridgeEntity");
    }
    @EventHandler
    public void onSnowballPlateLaunch(ProjectileLaunchEvent event){
        if (!(event.getEntity() instanceof Snowball snowball)) return;
        if (!(snowball.getShooter() instanceof Player p)) return;

        ItemStack item = p.getInventory().getItemInMainHand();
        if (!new SnowballPlateItem().isItem(item)) return;
        event.getEntity().getPersistentDataContainer().set(
                new NamespacedKey(JavaPlugin.getPlugin(TheFloorIsLavaManager.class), "snowballPlateEntity"),
                PersistentDataType.STRING,
                "snowballPlateEntity");
    }

    @EventHandler
    public void onSnowballHit(ProjectileHitEvent event){
        if (!(event.getEntity() instanceof Snowball snowball)) return;
        if (!(Objects.equals(snowball.getPersistentDataContainer().get(
                        new NamespacedKey(plugin, "snowballPlateEntity"),
                        PersistentDataType.STRING),
                "snowballPlateEntity"))
        ) return;
        if (!(snowball.getShooter() instanceof Player p)) return;

        Location loc = snowball.getLocation().getBlock().getLocation();
        fillAround(loc, 4, BlockUtils.getWoolBlockByPlayer(p));
    }

    private void fillAround(Location center, @SuppressWarnings("SameParameterValue") int radius, Material material) {
        World world = center.getWorld();
        int cx = center.getBlockX();
        int cy = center.getBlockY();
        int cz = center.getBlockZ();

        for (int x = cx - radius; x <= cx + radius; x++) {
            for (int z = cz - radius; z <= cz + radius; z++) {
                Block b = world.getBlockAt(x, cy, z);

                // Si tu veux éviter de remplacer n’importe quoi :
                if (!b.getType().isSolid()) {
                    b.setType(material, false);
                }
            }
        }
    }

    private boolean shouldGiveItem(ItemStack stack){
        List<Material> materials = List.of(Material.DIAMOND, Material.GOLD_INGOT,Material.IRON_INGOT,Material.COPPER_INGOT,Material.AMETHYST_SHARD,Material.EMERALD,Material.REDSTONE,Material.LAPIS_LAZULI,Material.EGG,Material.SNOWBALL,Material.COBBLESTONE,Material.DIRT,Material.BAKED_POTATO,Material.GRAY_WOOL);
        return materials.contains(stack.getType()) || ItemManager.getAssociatedCustomItem(stack) != null;
    }


}