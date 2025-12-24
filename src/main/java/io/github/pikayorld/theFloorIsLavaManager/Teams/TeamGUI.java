package io.github.pikayorld.theFloorIsLavaManager.Teams;

import io.github.pikayorld.theFloorIsLavaManager.BlockColorUtils;
import io.github.pikayorld.theFloorIsLavaManager.TheFloorIsLavaManager;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import javax.xml.crypto.Data;
import java.util.UUID;


public class TeamGUI implements Listener {


    private final TheFloorIsLavaManager plugin;


    public TeamGUI(TheFloorIsLavaManager plugin) {
        this.plugin = plugin;
    }


    public static void openMainMenu(TheFloorIsLavaManager plugin, Player p) {
        Inventory inv = Bukkit.createInventory(null, 27, "Menu d'équipe");


        TeamManager tm = plugin.getTeamManager();
        TeamData team = tm.getPlayerTeam(p.getUniqueId());


        if (team == null) {
            inv.setItem(11, createItem(Material.GREEN_WOOL, "Créer une équipe", "create_team"));
            inv.setItem(15, createItem(Material.LIGHT_BLUE_WOOL, "Rejoindre une équipe", "join_team"));
        } else {
            if (p.getName().equals(team.getName())){
                inv.setItem(13, createItem(Material.BLUE_WOOL, "Gérer mon équipe", "manage_team"));
                inv.setItem(15, createItem(Material.PAPER, "Demandes reçues", "request_menu"));
            }
            inv.setItem(11, createItem(Material.BARRIER, "Quitter l'équipe", "leave_team"));
        }
        p.openInventory(inv);
    }


    public static void openConfirmLeaveMenu(Player p) {
        Inventory inv = Bukkit.createInventory(null, 27, "Quitter l'équipe ?");
        inv.setItem(11, createItem(Material.GREEN_WOOL, "Oui", "confirm_leaving"));
        inv.setItem(15, createItem(Material.RED_WOOL, "Non", "deny_leaving"));
        p.openInventory(inv);
    }
    public static void openRequestsMenu(TeamManager teamManager, Player p) {
        Inventory inv = Bukkit.createInventory(null, 54, "Demandes en attente");
        int i = 0;
        for (UUID playerUuid : teamManager.getInviteManager().getListOfRequestToTeam(teamManager.getPlayerTeam(p.getUniqueId()).getName())){
            Player target = Bukkit.getServer().getPlayer(playerUuid);
            if (target!=null){
                ItemStack item = createItem(Material.GREEN_WOOL, target.getName(), "accept_request");
                item.setData(DataComponentTypes.LORE, ItemLore.lore().addLine(Component.text("Accepter la demande")).build());
                inv.setItem(i, item);
                i++;
            }
        }
        p.openInventory(inv);
    }
    public static void openManageMenu(TeamManager teamManager, Player p) {
        Inventory inv = Bukkit.createInventory(null, 54, "Kick des membres");
        int i = 0;
        if (teamManager.getPlayerTeam(p.getUniqueId())!=null){
            for (UUID playerUuid : teamManager.getPlayerTeam(p.getUniqueId()).getMembers()){
                Player target = Bukkit.getServer().getPlayer(playerUuid);
                if (target!=null && !target.getName().equals(p.getName())){
                    inv.setItem(i, createItem(Material.RED_WOOL, target.getName(), "kick"));
                    i++;
                }
            }
            p.openInventory(inv);
        }
    }
    public static void openAskJoinMenu(TeamManager teamManager, Player p) {
        Inventory inv = Bukkit.createInventory(null, 54, "Demander à rejoindre une équipe");
        int i = 0;
        for ( String teamName : teamManager.getTeams()){
            TeamData team = teamManager.getTeam(teamName);
            if (team !=null){
                ItemStack item = createItem(BlockColorUtils.getWoolBlockByNamedTextColor(team.getColor()),team.getName());
                StringBuilder description = new StringBuilder();
                ItemLore.Builder lore = ItemLore.lore();
                for (UUID memberUuid : team.getMembers()){
                    Player member = Bukkit.getPlayer(memberUuid);
                    if (member != null){
                        lore.addLine(Component.text("- "+member.getName()));
                    }
                }
                item.setData(DataComponentTypes.LORE, lore.build());
                inv.setItem(i, item);
            }
            i++;
        }
        p.openInventory(inv);
    }

    private static ItemStack createItem(Material mat, String name) {
        return createItem(mat, name, null, "");
    }

    private static ItemStack createItem(Material mat, String name, String customModelData) {
        return createItem(mat, name, null, customModelData);
    }

    private static ItemStack createItem(Material mat, String name, String id, String customModelData) {
        ItemStack it = new ItemStack(mat);
        if (!customModelData.isBlank()){
            it.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addString(customModelData).build());
        }
        ItemMeta m = it.getItemMeta();
        m.setDisplayName("§e" + name);
        if (id != null)
            m.getPersistentDataContainer().set(new NamespacedKey(JavaPlugin.getPlugin(TheFloorIsLavaManager.class), "buttonId"), PersistentDataType.STRING, id);
        it.setItemMeta(m);
        return it;
    }


    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (plugin.getDangerManagerInstance().getHasStarted()) return;
        Player p = (Player) e.getWhoClicked();
        if (e.getView().getTitle().equals("Menu d'équipe")) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) return;
            String name = e.getCurrentItem().getItemMeta().getDisplayName();


            if (name.contains("Créer")) {
                plugin.getTeamManager().createTeamForPlayer(p);
                p.closeInventory();
            }

            if (name.contains("Gérer")) {
                openManageMenu(plugin.getTeamManager(), p);
            }

            if (name.contains("Rejoindre")) {
                openAskJoinMenu(plugin.getTeamManager(), p);
            }


            if (name.contains("Demandes")) {
                openRequestsMenu(plugin.getTeamManager(), p);
            }


            if (name.contains("Quitter")) {
                openConfirmLeaveMenu(p);
            }
        }


        if (e.getView().getTitle().equals("Quitter l'équipe ?")) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) return;
            String name = e.getCurrentItem().getItemMeta().getDisplayName();


            if (name.contains("Oui")) {
                plugin.getTeamManager().removePlayerFromTeam(p);
                p.sendMessage("§cTu as quitté ton équipe.");
                p.closeInventory();
            } else if (name.contains("Non")) {
                p.closeInventory();
            }
        }


        if (e.getView().getTitle().equals("Demandes en attente")) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) return;


            String targetName = e.getCurrentItem().getItemMeta().getDisplayName().substring(2);
            Player target = Bukkit.getPlayer(targetName);
            plugin.getLogger().info(targetName);
            if (target == null) return;


            TeamData team = plugin.getTeamManager().getPlayerTeam(p.getUniqueId());
            if (team == null) return;


            team.acceptRequest(target.getUniqueId());
            plugin.getTeamManager().addPlayerToVanillaTeam(target, team.getName());
            plugin.getTeamManager().getInviteManager().remove(target.getUniqueId());


            target.sendMessage("§aTa demande a été acceptée !");
            p.sendMessage("§aJoueur ajouté à l'équipe.");
            p.closeInventory();
        }


        if (e.getView().getTitle().equals("Demander à rejoindre une équipe")) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) return;
            plugin.getLogger().info("currentItem");


            String targetName = e.getCurrentItem().getItemMeta().getDisplayName().substring(2);


            TeamData teamAsked = plugin.getTeamManager().getTeam(targetName);
            if (teamAsked == null) return;
            plugin.getLogger().info("teamAsk" + teamAsked.getName());

            plugin.getTeamManager().getInviteManager().sendRequest(p.getUniqueId(), teamAsked.getName());


            p.sendMessage("§aTa demande a été envoyé !");
            for (UUID memberUuid : teamAsked.getMembers()){
                Player member = plugin.getServer().getPlayer(memberUuid);
                member.sendMessage(Component.text("demande reçu de " + p.getName()));
            }
            p.closeInventory();
        }


        if (e.getView().getTitle().equals("Kick des membres")) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) return;
            plugin.getLogger().info("currentItem");


            String targetName = e.getCurrentItem().getItemMeta().getDisplayName().substring(2);


            Player kickedPlayer = plugin.getServer().getPlayer(targetName);
            if (kickedPlayer == null) return;
            plugin.getLogger().info("kick" + kickedPlayer.getName());

            TeamData team = plugin.getTeamManager().getTeam(p.getName());
            if (team == null) return;
            plugin.getTeamManager().removePlayerFromTeam(kickedPlayer);


            p.sendMessage("§aLe joueur " + targetName + " a été expulsé de l'équipe");
            p.closeInventory();
        }
    }
}
