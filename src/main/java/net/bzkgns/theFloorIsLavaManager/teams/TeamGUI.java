package net.bzkgns.theFloorIsLavaManager.teams;

import io.papermc.paper.connection.PlayerGameConnection;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.event.player.PlayerCustomClickEvent;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.bzkgns.theFloorIsLavaManager.managers.GameState;
import net.bzkgns.theFloorIsLavaManager.utils.BlockUtils;
import net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaManager;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.bzkgns.theFloorIsLavaManager.utils.TextUtils;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
            if (p.getName().equals(team.getId())){
                inv.setItem(13, createItem(Material.BLUE_WOOL, "Gérer mon équipe", "manage_team"));
                inv.setItem(22, createItem(Material.PAPER, "Demandes reçues", "request_menu"));
                ItemStack renameItem = createItem(Material.NAME_TAG, "Renommer l'équipe", "rename_menu");
                renameItem.setData(DataComponentTypes.LORE, ItemLore.lore().addLine(Component.text("Nom actuel: ").append(team.getName())).build());
                inv.setItem(3, renameItem);
                inv.setItem(5, createItem(Material.BRUSH, "Changer la couleur de l'équipe", "recolor_menu"));
                ItemStack leaveItem = createItem(Material.BARRIER, "Quitter l'équipe", "leave_team");
                ItemLore.Builder lore = ItemLore.lore();
                lore.addLine(Component.text("ATTENTION si vous quittez,"));
                lore.addLine(Component.text("votre équipe sera dissoute"));
                leaveItem.setData(DataComponentTypes.LORE, lore.build());
                inv.setItem(11, leaveItem);
            }else{
                inv.setItem(11, createItem(Material.BARRIER, "Quitter l'équipe", "leave_team"));
            }
            ItemStack memberItem = createItem(Material.BLUE_WOOL, "Information de l'équipe", "info_team");
            ItemLore.Builder lore = ItemLore.lore();
            lore.addLine(Component.text("Nom de l'équipe: ").append(team.getName()));
            for (UUID memberUuid : team.getMembers()){
                Player member = Bukkit.getPlayer(memberUuid);
                if (member != null){
                    if (member.getName().equals(team.getId())){
                        lore.addLine(Component.text("- \uD83D\uDC51"+member.getName()).color(TextColor.fromHexString("#55FF55")));
                    } else {
                    lore.addLine(Component.text("- "+member.getName()));
                    }
                }
            }
            memberItem.setData(DataComponentTypes.LORE, lore.build());
            inv.setItem(15, memberItem);
        }
        p.openInventory(inv);
    }


    public static void openConfirmLeaveMenu(Player p) {
        Inventory inv = Bukkit.createInventory(null, 27, Component.text("Quitter l'équipe ?"));
        if (Objects.equals(TeamManager.getInstance().getPlayerTeam(p.getUniqueId()).getId(), p.getName())){
            ItemStack leaveItem = createItem(Material.GREEN_WOOL, "Oui", "confirm_leaving");
            ItemLore.Builder lore = ItemLore.lore();
            lore.addLine(Component.text("ATTENTION si vous quittez,"));
            lore.addLine(Component.text("votre équipe sera dissoute"));
            leaveItem.setData(DataComponentTypes.LORE, lore.build());
            inv.setItem(11, leaveItem);
        }else{
            inv.setItem(11, createItem(Material.GREEN_WOOL, "Oui", "confirm_leaving"));
        }
        inv.setItem(15, createItem(Material.RED_WOOL, "Non", "deny_leaving"));
        p.openInventory(inv);
    }
    public static void openRequestsMenu(Player p) {
        TeamManager teamManager = TeamManager.getInstance();
        Inventory inv = Bukkit.createInventory(null, 54, Component.text("Demandes en attente"));
        int i = 0;
        for (UUID playerUuid : teamManager.getInviteManager().getListOfRequestToTeam(teamManager.getPlayerTeam(p.getUniqueId()).getId())){
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
                ItemStack item = createItem(BlockUtils.getWoolBlockByNamedTextColor(team.getColor()),team.getNameText());
                ItemLore.Builder lore = ItemLore.lore();
                if (teamManager.getInviteManager().hasInvite(p.getUniqueId(), team.getId())){
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

    public static void openRenameMenu(Player p){
        TeamData team = TeamManager.getInstance().getPlayerTeam(p.getUniqueId());
        if (team != null){
            Dialog dialog = Dialog.create(builder -> builder.empty()
                    .base(DialogBase.builder(Component.text("Renommer l'équipe"))
                            .inputs(List.of(
                                    DialogInput.text("team_name", Component.text("Nom d'équipe", team.getColor()))
                                            .initial(team.getNameText())
                                            .build()
                            ))
                            .build()
                    )
                    .type(DialogType.confirmation(
                            ActionButton.create(
                                    Component.text("Renommer", TextColor.color(0xAEFFC1)),
                                    Component.text("Cliquer pour renommer votre équipe."),
                                    100,
                                    DialogAction.customClick(Key.key("tfl:user_input/confirm"), null)
                            ),
                            ActionButton.create(
                                    Component.text("Annuler", TextColor.color(0xFFA0B1)),
                                    Component.text("Cliquer pour annuler le renommage de votre équipe."),
                                    100,
                                    null // If we set the action to null, it doesn't do anything and closes the dialog
                            )
                    ))
            );
            p.showDialog(dialog);
        }
    }

    public static void openRecolorMenu(Player p){
        TeamData team = TeamManager.getInstance().getPlayerTeam(p.getUniqueId());
        if (team != null){
            List<ActionButton> actions = new ArrayList<>();

            for (NamedTextColor color : TeamManager.getInstance().getAvailableColors()){
                String key = "tfl:user_input/recolor/"+ color;
                actions.add(ActionButton.create(
                        Component.text(color.toString(), color),
                        Component.text("Click to change your team color."),
                        100,
                        DialogAction.customClick(Key.key(key), null )
                ));
            }

            Dialog dialog = Dialog.create(builder -> builder.empty()
                    .base(DialogBase.builder(Component.text("Changer la couleur de l'équipe")).build())
                    .type(DialogType.multiAction(actions)
                            .exitAction(ActionButton.create(
                                    Component.text("Annuler", TextColor.color(0xFFA0B1)),
                                    Component.text("Cliquer pour annuler le changement de couleur de votre équipe."),
                                    100,
                                    null // If we set the action to null, it doesn't do anything and closes the dialog
                            )).build()
                    )
            );
            p.showDialog(dialog);
        }
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
    void handleRenameDialog(PlayerCustomClickEvent event) {
        if (!event.getIdentifier().equals(Key.key("tfl:user_input/confirm"))) {
            return;
        }

        DialogResponseView view = event.getDialogResponseView();
        if (view == null) {
            return;
        }

        String teamName = view.getText("team_name");
        if (teamName == null || teamName.isBlank()) {
            if (event.getCommonConnection() instanceof PlayerGameConnection conn) {
                Player player = conn.getPlayer();
                player.sendMessage(TextUtils.errorMessage("Le nom de l'équipe ne peut pas être vide."));
                openRenameMenu(player);
            }
            return;
        }

        if (event.getCommonConnection() instanceof PlayerGameConnection conn) {
            Player player = conn.getPlayer();
            TeamData team = TeamManager.getInstance().getPlayerTeam(player.getUniqueId());
            if (team != null) {
                team.rename(teamName);
                player.sendMessage(TextUtils.validationMessage("L'équipe a été renommée en ").append(Component.text(teamName, team.getColor()) ));
                openMainMenu(player);
            }
        }
    }

    @EventHandler
    void handleRecolorDialog(PlayerCustomClickEvent event) {
        if (!event.getIdentifier().asString().startsWith("tfl:user_input/recolor/")) {
            return;
        }

        DialogResponseView view = event.getDialogResponseView();
        if (view == null) {
            return;
        }

        String colorString = event.getIdentifier().asString().substring("tfl:user_input/recolor/".length());
        NamedTextColor newColor = NamedTextColor.NAMES.value(colorString);
        if (newColor == null) {
            if (event.getCommonConnection() instanceof PlayerGameConnection conn) {
                Player player = conn.getPlayer();
                player.sendMessage(TextUtils.errorMessage("Couleur invalide."));
                openRecolorMenu(player);
            }
            return;
        }

        if (event.getCommonConnection() instanceof PlayerGameConnection conn) {
            Player player = conn.getPlayer();
            TeamData team = TeamManager.getInstance().getPlayerTeam(player.getUniqueId());
            if (team != null) {
                team.changeColor(newColor);
                player.sendMessage(TextUtils.validationMessage("La couleur de l'équipe à été changée en ").append(Component.text(newColor.toString(), team.getColor()) ));
                openMainMenu(player);
            }
        }
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

            if (name.contains("Renommer")) {
                openRenameMenu(player);
            }

            if (name.contains("Changer la couleur")) {
                openRecolorMenu(player);
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

            if (!TeamManager.getInstance().getInviteManager().hasInvite(target.getUniqueId(), team.getId())) {
                plugin.getLogger().info("no request found for " + targetName);
                player.sendMessage(TextUtils.errorMessage("Demande expirée."));
                openRequestsMenu(player);
                return;
            }

            if (event.isRightClick()){
                TeamManager.getInstance().getInviteManager().remove(target.getUniqueId());
                target.sendMessage(TextUtils.errorMessage("La demande pour l'équipe de ").append(team.getName()).append(TextUtils.errorMessage(" a été refusée !",false)));
                player.sendMessage(TextUtils.errorMessage("Demande refusée."));
                openRequestsMenu(player);
                return;
            }
            TeamManager.broadcastTeamMessage(TextUtils.validationMessage(target.getName() + " ajouté à l'équipe."), team);
            team.acceptRequest(target.getUniqueId());
            TeamManager.getInstance().addPlayerToVanillaTeam(target, team.getId());
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

            if (TeamManager.getInstance().getInviteManager().hasInvite(player.getUniqueId(), teamAsked.getId())) {
                TeamManager.getInstance().getInviteManager().remove(player.getUniqueId());
                player.sendMessage(TextUtils.errorMessage("Demande annulée"));
                openAskJoinMenu(player);
                return;
            }

            plugin.getLogger().info(player.getName() + " à demandé de rejoindre " + teamAsked.getId());

            TeamManager.getInstance().getInviteManager().sendRequest(player.getUniqueId(), teamAsked.getId());


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
            kickedPlayer.sendMessage(TextUtils.errorMessage("Vous avez été expulsé de l'équipe de ").append(team.getName()));
            TeamManager.broadcastTeamMessage(TextUtils.errorMessage( kickedPlayer.getName() + " a été expulsé de l'équipe"), team);

            player.closeInventory();
        }
    }
}
