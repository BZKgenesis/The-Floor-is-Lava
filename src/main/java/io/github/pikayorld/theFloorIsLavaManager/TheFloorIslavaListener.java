package io.github.pikayorld.theFloorIsLavaManager;

import io.github.pikayorld.theFloorIsLavaManager.Teams.TeamGUI;
import io.github.pikayorld.theFloorIsLavaManager.Teams.TeamManagerItem;
import io.github.pikayorld.theFloorIsLavaManager.shop.ShopGUI;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;

import java.util.List;
import java.util.Objects;

import static io.github.pikayorld.theFloorIsLavaManager.BlockColorUtils.getWoolBlockByPlayer;


public class TheFloorIslavaListener implements Listener {

    private final TheFloorIsLavaManager plugin;

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
        World world = Bukkit.getWorld("world");
        if (event.getPlayer().getStatistic(Statistic.TOTAL_WORLD_TIME) < 100 && world != null){
            Location spawnPos = new Location (world,0.5,281,0.5);
            event.getPlayer().teleport(spawnPos);
            if (!plugin.getDangerManagerInstance().getHasStarted()){
                event.getPlayer().getInventory().clear();
                event.getPlayer().give(TeamManagerItem.giveTeamManagerItem());
                event.getPlayer().give(ShopGUI.giveShopItem());
            }
        }
        if (plugin.getDangerManagerInstance().getHasStarted() && !plugin.getDangerManagerInstance().isPlayerInGame(event.getPlayer())){
            event.getPlayer().setGameMode(GameMode.SPECTATOR);
        }
        for (String recipe_key : TheFloorIsLavaManager.RECIPES_KEY){
            event.getPlayer().discoverRecipe(new NamespacedKey(plugin,recipe_key));
        }
    }

    @EventHandler
    public void onPlaced(BlockPlaceEvent event){
        Player p = event.getPlayer();
        Block block = event.getBlockPlaced();
        if (block.getType().toString().endsWith("WOOL")){
            block.setType(getWoolBlockByPlayer(p));
        }
        if (PopupTower.isPopupTower(event.getItemInHand())){
            Rotation rotation = Rotation.NONE;
            float angle =p.getYaw()+180;
            if (angle<=45 || angle>=315){
                rotation = Rotation.NONE;
            } else if (angle>=45 && angle<=135) {
                rotation = Rotation.CLOCKWISE;
            } else if (angle>=135 && angle<=225) {
                rotation = Rotation.FLIPPED;
            }else if (angle>=225 && angle<=315) {
                rotation = Rotation.COUNTER_CLOCKWISE;
            }
            PopupTower.placePopupTower(p,block.getLocation(),rotation);
        }
    }

    @EventHandler
    public void noPickup(PlayerAttemptPickupItemEvent e){
        Item item = e.getItem();
        ItemStack stack = item.getItemStack();
        if (stack.getType().toString().endsWith("WOOL")){
            item.setItemStack(new ItemStack(Material.LIGHT_GRAY_WOOL, stack.getAmount()));
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDamage(EntityDamageEvent e){
        if (e.getEntity() instanceof Player victime && e.getDamageSource().getCausingEntity()!=null){
            if (e.getDamageSource().getCausingEntity() instanceof Player aggresseur){
                if (!TheFloorIsLavaManager.pvp){
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onTeamInvInteract(PlayerInteractEvent event) {
        if (!event.hasItem()) return;

        ItemStack item = event.getItem();
        if (item == null) return;
        if (!TeamInventoryManager.getInstance().isTeamInventoryItem(item)) return;

        event.setCancelled(true);

        Player player = event.getPlayer();
        String team = getTeamOf(player); // À TOI d’implémenter selon ton plugin d’équipes

        if (team == null) {
            player.sendMessage("§cT'as pas d'équipe, donc pas de coffre partagé.");
            return;
        }

        TeamInventory inv = TeamInventoryManager.getInstance().getTeamInventory(team);
        player.openInventory(inv.getInventory());
    }

    @EventHandler
    public void onTeamManagerInteract(PlayerInteractEvent event) {
        if (!event.hasItem()) return;

        ItemStack item = event.getItem();
        if (item == null) return;
        if (!TeamManagerItem.isTeamManagerItem(item)) return;
        if (plugin.getDangerManagerInstance().getHasStarted()) return;

        event.setCancelled(true);

        Player player = event.getPlayer();
        String team = getTeamOf(player); // À TOI d’implémenter selon ton plugin d’équipes


        TeamGUI.openMainMenu(plugin, event.getPlayer());
    }

    @EventHandler
    public void onShopInteract(PlayerInteractEvent event) {
        if (!event.hasItem()) return;

        ItemStack item = event.getItem();
        if (item == null) return;
        if (!ShopGUI.isShopItem(item)) return;

        event.setCancelled(true);

        ShopGUI.open(event.getPlayer(),0);
    }

    private String getTeamOf(Player p){
        if (p!= null){
            Team team = plugin.getServer().getScoreboardManager().getMainScoreboard().getEntryTeam(p.getName());
            if (team != null){
                return team.getName();
            }
        }
        return null;
    }

    @EventHandler
    public void onFallDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player &&
                event.getCause() == EntityDamageEvent.DamageCause.FALL) {

            if (player.getInventory().getBoots() != null &&
                    player.getInventory().getBoots().getType() == Material.LEATHER_BOOTS) {

                event.setDamage(event.getDamage() * plugin.getFallDamageReduction()); // 80% de réduction
            }
        }
    }
    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        plugin.getLogger().info("OnDeath with respawn");
        if (plugin.getDangerManagerInstance().getNoRespawn()){
            plugin.getLogger().info("OnDeath no respawn");
            Player player = event.getEntity();
            Location deathLocation = player.getLocation();
            event.getEntity().getWorld().strikeLightningEffect(event.getEntity().getLocation());

            // Empêche le respawn auto si jamais tu l'as modifié ailleurs
            if (event.getDamageSource().getCausingEntity() instanceof Player assassin){
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
            Bukkit.getScheduler().runTaskLater(plugin, () -> { event.getEntity().spigot().respawn(); }, 4L);
        }
    }

    @EventHandler
    public void onEggBridgeLaunch(ProjectileLaunchEvent event){
        if (!(event.getEntity() instanceof Egg egg)) return;
        if (!(egg.getShooter() instanceof Player p)) return;

        ItemStack item = p.getInventory().getItemInMainHand();
        if (!EggBridge.isEggBridgeItem(item)) return;
        event.getEntity().getPersistentDataContainer().set(new NamespacedKey(JavaPlugin.getPlugin(TheFloorIsLavaManager.class), "eggBridgeEntity"), PersistentDataType.STRING, "eggBridgeEntity");
    }
    @EventHandler
    public void onSnowballPlateLaunch(ProjectileLaunchEvent event){
        if (!(event.getEntity() instanceof Snowball snowball)) return;
        if (!(snowball.getShooter() instanceof Player p)) return;

        ItemStack item = p.getInventory().getItemInMainHand();
        if (!SnowballPlate.isSnowballPlateItem(item)) return;
        event.getEntity().getPersistentDataContainer().set(new NamespacedKey(JavaPlugin.getPlugin(TheFloorIsLavaManager.class), "snowballPlateEntity"), PersistentDataType.STRING, "snowballPlateEntity");
    }

    @EventHandler
    public void onSnowballHit(ProjectileHitEvent event){
        if (!(event.getEntity() instanceof Snowball snowball)) return;
        if (!(Objects.equals(snowball.getPersistentDataContainer().get(new NamespacedKey(plugin, "snowballPlateEntity"), PersistentDataType.STRING), "snowballPlateEntity"))) return;
        if (!(snowball.getShooter() instanceof Player p)) return;

        Location loc = snowball.getLocation().getBlock().getLocation();
        Block block = loc.getBlock();
        fillAround(loc, 4, BlockColorUtils.getWoolBlockByPlayer(p));
    }

    private void fillAround(Location center, int radius, Material material) {
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
        if (TeamInventoryManager.getInstance().isTeamInventoryItem(stack)){
            return true;
        }
        if (PopupTower.isPopupTower(stack)){
            return true;
        }
        if (materials.contains(stack.getType())){
            return true;
        }
        return false;
    }


}
