package io.github.pikayorld.theFloorIsLavaManager;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Team;

import java.util.List;

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
        }

        event.getPlayer().discoverRecipe(new NamespacedKey(plugin,"batte"));
        event.getPlayer().discoverRecipe(new NamespacedKey(plugin,"eggBridge"));
        event.getPlayer().discoverRecipe(new NamespacedKey(plugin,"patate"));
        event.getPlayer().discoverRecipe(new NamespacedKey(plugin,"blocs_en_plus"));
        event.getPlayer().discoverRecipe(new NamespacedKey(plugin,"fireball"));
        event.getPlayer().discoverRecipe(new NamespacedKey(plugin,"ciseaux"));
        event.getPlayer().discoverRecipe(new NamespacedKey(plugin,"enderPearl"));
        event.getPlayer().discoverRecipe(new NamespacedKey(plugin,"popupTower"));
        event.getPlayer().discoverRecipe(new NamespacedKey(plugin,"teamInv"));
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
    public void onInteract(PlayerInteractEvent event) {
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
        if (plugin.getDangerManagerInstance().getNoRespawn()){
            Player player = event.getEntity();
            Location deathLocation = player.getLocation();

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


            Bukkit.getScheduler().runTask(plugin, () -> {
                player.setGameMode(GameMode.SPECTATOR);
                player.teleport(deathLocation);
            });
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
