package net.bzkgns.theFloorIsLavaManager.teams;

import net.bzkgns.theFloorIsLavaManager.managers.GameState;
import net.bzkgns.theFloorIsLavaManager.utils.BlockUtils;
import net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaManager;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.bzkgns.theFloorIsLavaManager.utils.TextUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
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

import java.util.UUID;

import static net.bzkgns.theFloorIsLavaManager.utils.TextUtils.*;

@SuppressWarnings("UnstableApiUsage")
public class TeamGUI implements Listener {

    private final TheFloorIsLavaManager plugin;


    public TeamGUI(TheFloorIsLavaManager plugin) {
        this.plugin = plugin;
    }


    public static void openMainMenu(Player p) {
        Inventory inv = Bukkit.createInventory(null, 27, Component.text("Menu d'équipe"));

        TeamManager tm = TeamManager.getInstance();
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
        Inventory inv = Bukkit.createInventory(null, 27, Component.text("Quitter l'équipe ?"));
        inv.setItem(11, createItem(Material.GREEN_WOOL, "Oui", "confirm_leaving"));
        inv.setItem(15, createItem(Material.RED_WOOL, "Non", "deny_leaving"));
        p.openInventory(inv);
    }
    public static void openRequestsMenu(Player p) {
        TeamManager teamManager = TeamManager.getInstance();
        Inventory inv = Bukkit.createInventory(null, 54, Component.text("Demandes en attente"));
        int i = 0;
        for (UUID playerUuid : teamManager.getInviteManager().getListOfRequestToTeam(teamManager.getPlayerTeam(p.getUniqueId()).getName())){
            Player target = Bukkit.getServer().getPlayer(playerUuid);
            if (target!=null){
                ItemStack item = createItem(Material.GREEN_WOOL, target.getName(), "accept_request");
                item.setData(DataComponentTypes.LORE, ItemLore.lore().addLine(Component.text("Accepter la demande")).build());
                if (i < 45)
                    inv.setItem(i, item);
                i++;
            }
        }
        inv.setItem(45, createBackItem());
        p.openInventory(inv);
    }
    public static void openManageMenu(Player p) {
        TeamManager teamManager = TeamManager.getInstance();
        Inventory inv = Bukkit.createInventory(null, 54, Component.text("Kick des membres"));
        int i = 0;
        if (teamManager.getPlayerTeam(p.getUniqueId())!=null){
            for (UUID playerUuid : teamManager.getPlayerTeam(p.getUniqueId()).getMembers()){
                Player target = Bukkit.getServer().getPlayer(playerUuid);
                if (target!=null && !target.getName().equals(p.getName())){
                    ItemStack item = createItem(Material.RED_WOOL, target.getName(), "kick");
                    item.setData(DataComponentTypes.LORE, ItemLore.lore().addLine(Component.text("Cliquer pour expulser le joueur")).build());
                    if (i < 45)
                        inv.setItem(i, item);
                    i++;
                }
            }
            inv.setItem(45, createBackItem());
            p.openInventory(inv);
        }
    }
    public static void openAskJoinMenu(Player p) {
        TeamManager teamManager = TeamManager.getInstance();
        Inventory inv = Bukkit.createInventory(null, 54, Component.text("Demander à rejoindre une équipe"));
        int i = 0;
        for ( String teamName : teamManager.getTeams()){
            TeamData team = teamManager.getTeam(teamName);
            if (team !=null){
                ItemStack item = createItem(BlockUtils.getWoolBlockByNamedTextColor(team.getColor()),team.getName());
                ItemLore.Builder lore = ItemLore.lore();
                if (teamManager.getInviteManager().hasInvite(p.getUniqueId(), team.getName())){
                    lore.addLine(Component.text("Cliquer pour annuler la demande").color(TextColor.fromHexString("#FF5555")));
                }
                for (UUID memberUuid : team.getMembers()){
                    Player member = Bukkit.getPlayer(memberUuid);
                    if (member != null){
                        lore.addLine(Component.text("- "+member.getName()));
                    }
                }
                item.setData(DataComponentTypes.LORE, lore.build());
                if (i < 45)
                    inv.setItem(i, item);
            }
            i++;
        }
        inv.setItem(45, createBackItem());
        p.openInventory(inv);
    }

    private static ItemStack createItem(Material mat, String name) {
        return createItem(mat, name, null, "");
    }

    private static ItemStack createItem(Material mat, String name, String customModelData) {
        return createItem(mat, name, null, customModelData);
    }

    private static ItemStack createItem(Material mat, String name, @SuppressWarnings("SameParameterValue") String id, String customModelData) {
        ItemStack it = new ItemStack(mat);
        if (!customModelData.isBlank()){
            it.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addString(customModelData).build());
        }
        ItemMeta m = it.getItemMeta();
        m.displayName(Component.text(name).color(TextColor.fromHexString("#FFFF55")));
        if (id != null)
            m.getPersistentDataContainer().set(new NamespacedKey(JavaPlugin.getPlugin(TheFloorIsLavaManager.class), "buttonId"), PersistentDataType.STRING, id);
        it.setItemMeta(m);
        return it;
    }

    private static ItemStack createBackItem() {
        ItemStack it = new ItemStack(Material.ARROW);
        it.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addString("back").build());
        ItemMeta m = it.getItemMeta();
        m.displayName(Component.text("Retour").color(TextColor.fromHexString("#FF5555")));
        it.setItemMeta(m);
        return it;
    }

    private static boolean isBackItem(ItemStack stack) {
        if (stack.getType() == Material.ARROW) {
            ItemMeta meta = stack.getItemMeta();
            if (meta != null && meta.hasCustomModelDataComponent()) {
                String customModelData = meta.getCustomModelDataComponent().getStrings().getFirst();
                return customModelData.equals("back");
            }
        }
        return false;
    }


    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (plugin.getGameManager().getState() == GameState.RUNNING) return;
        Player player = (Player) event.getWhoClicked();
        if (plainText(event.getView().title()).equals("Menu d'équipe")) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) return;
            Component nameComponent = event.getCurrentItem().getItemMeta().displayName();
            if (nameComponent == null) return;
            String name = plainText(nameComponent);


            if (name.contains("Créer")) {
                TeamManager.getInstance().createTeamForPlayer(player);
                plugin.getLogger().info( player.getName() + " a créé une équipe");
                player.sendMessage(TextUtils.validationMessage("Équipe créée."));
                openMainMenu(player);
            }

            if (name.contains("Gérer")) {
                openManageMenu(player);
            }

            if (name.contains("Rejoindre")) {
                openAskJoinMenu(player);
            }

            if (name.contains("Demandes")) {
                openRequestsMenu(player);
            }

            if (name.contains("Quitter")) {
                openConfirmLeaveMenu(player);
            }
        }


        if (plainText(event.getView().title()).equals("Quitter l'équipe ?")) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) return;
            Component displayName = event.getCurrentItem().getItemMeta().displayName();
            if (displayName == null) return;
            String name = plainText(displayName);


            if (name.contains("Oui")) {
                TeamManager.getInstance().removePlayerFromTeam(player);
                player.sendMessage(TextUtils.errorMessage("Vous avez quitté votre équipe."));
                openMainMenu(player);
            } else if (name.contains("Non")) {
                openMainMenu(player);
            }
        }


        if (plainText(event.getView().title()).equals("Demandes en attente")) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) return;

            if (isBackItem(event.getCurrentItem())) {
                openMainMenu(player);
                return;
            }

            String targetName = plainText(event.getCurrentItem().getItemMeta().displayName());
            Player target = Bukkit.getPlayer(targetName);
            plugin.getLogger().info(targetName);
            if (target == null) return;




            TeamData team = TeamManager.getInstance().getPlayerTeam(player.getUniqueId());
            if (team == null) return;

            if (!TeamManager.getInstance().getInviteManager().hasInvite(target.getUniqueId(), team.getName())) {
                plugin.getLogger().info("no request found for " + targetName);
                player.sendMessage(TextUtils.errorMessage("Demande expirée."));
                openRequestsMenu(player);
                return;
            }

            if (event.isRightClick()){
                TeamManager.getInstance().getInviteManager().remove(target.getUniqueId());
                target.sendMessage(TextUtils.errorMessage("La demande pour l'équipe de " + team.getName() + " a été refusée !"));
                player.sendMessage(TextUtils.errorMessage("Demande refusée."));
                openRequestsMenu(player);
                return;
            }
            TeamManager.broadcastTeamMessage(TextUtils.validationMessage(target.getName() + " ajouté à l'équipe."), team);
            team.acceptRequest(target.getUniqueId());
            TeamManager.getInstance().addPlayerToVanillaTeam(target, team.getName());
            TeamManager.getInstance().getInviteManager().remove(target.getUniqueId());

            target.sendMessage(TextUtils.validationMessage("Votre demande a été acceptée !"));
            openRequestsMenu(player);
        }


        if (plainText(event.getView().title()).equals("Demander à rejoindre une équipe")) {
            plugin.getLogger().info("Demander à rejoindre une équipe");
            event.setCancelled(true);
            if (event.getCurrentItem() == null) return;
            plugin.getLogger().info("currentItem" + event.getCurrentItem().getItemMeta().displayName());


            if (isBackItem(event.getCurrentItem())) {
                openMainMenu(player);
                return;
            }

            String targetName = plainText(event.getCurrentItem().getItemMeta().displayName());


            TeamData teamAsked = TeamManager.getInstance().getTeam(targetName);

            if (teamAsked == null) {
                plugin.getLogger().info("teamAsked is null pour " + targetName);
                return;
            }

            if (TeamManager.getInstance().getInviteManager().hasInvite(player.getUniqueId(), teamAsked.getName())) {
                TeamManager.getInstance().getInviteManager().remove(player.getUniqueId());
                player.sendMessage(TextUtils.errorMessage("Demande annulée"));
                openAskJoinMenu(player);
                return;
            }

            plugin.getLogger().info(player.getName() + " à demandé de rejoindre " + teamAsked.getName());

            TeamManager.getInstance().getInviteManager().sendRequest(player.getUniqueId(), teamAsked.getName());


            player.sendMessage(TextUtils.validationMessage("La demande a été envoyé"));
            TeamManager.broadcastTeamMessage(TextUtils.infoMessage("demande reçu de " + player.getName()), teamAsked);
            openAskJoinMenu(player);
        }


        if (plainText(event.getView().title()).equals("Kick des membres")) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) return;


            if (isBackItem(event.getCurrentItem())) {
                openMainMenu(player);
                return;
            }

            String targetName = plainText(event.getCurrentItem().getItemMeta().displayName());


            Player kickedPlayer = plugin.getServer().getPlayer(targetName);
            if (kickedPlayer == null) return;
            plugin.getLogger().info("kick" + kickedPlayer.getName());

            TeamData team = TeamManager.getInstance().getTeam(player.getName());
            if (team == null) return;
            TeamManager.getInstance().removePlayerFromTeam(kickedPlayer);
            kickedPlayer.sendMessage(TextUtils.errorMessage("Vous avez été expulsé de l'équipe de " + team.getName()));
            TeamManager.broadcastTeamMessage(TextUtils.errorMessage( kickedPlayer.getName() + " a été expulsé de l'équipe"), team);

            player.closeInventory();
        }
    }
}
