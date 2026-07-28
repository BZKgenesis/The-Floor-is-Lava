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
import net.bzkgns.theFloorIsLavaManager.lang.Messages;
import net.bzkgns.theFloorIsLavaManager.managers.GameState;
import net.bzkgns.theFloorIsLavaManager.utils.BlockUtils;
import net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaManager;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.bzkgns.theFloorIsLavaManager.utils.menu.MenuHolder;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static net.bzkgns.theFloorIsLavaManager.utils.GuiUtils.*;
import static net.bzkgns.theFloorIsLavaManager.utils.TextUtils.*;

@SuppressWarnings("UnstableApiUsage")
public class TeamGUI implements Listener {

    private final TheFloorIsLavaManager plugin;


    public TeamGUI(TheFloorIsLavaManager plugin) {
        this.plugin = plugin;
    }

    public static void openMainMenu(Player p) {
        MenuHolder holder = new MenuHolder(MenuHolder.MenuType.TEAM_MAIN);
        Inventory inv = Bukkit.createInventory(holder, 27, Messages.component(p, "gui.main_title"));
        holder.setInventory(inv);

        TeamManager tm = TeamManager.getInstance();
        TeamData team = tm.getPlayerTeam(p.getUniqueId());


        if (team == null) {
            inv.setItem(11, createItem(Material.GREEN_WOOL, Messages.component(p, "button.create_team"), "create_team"));
            inv.setItem(15, createItem(Material.LIGHT_BLUE_WOOL, Messages.component(p, "button.join_team"), "join_team"));
        } else {
            if (p.getName().equals(team.getId())) {
                inv.setItem(13, createItem(Material.BLUE_WOOL, Messages.component(p, "button.manage_team"), "manage_team"));
                inv.setItem(22, createItem(Material.PAPER, Messages.component(p, "button.request_menu"), "request_menu"));
                ItemStack renameItem = createItem(Material.NAME_TAG, Messages.component(p, "button.rename_menu"), "rename_menu");
                renameItem.setData(DataComponentTypes.LORE, ItemLore.lore()
                        .addLine(Messages.component(p, "item_lore.current_name", Placeholder.component("name", team.getName())))
                        .build());
                inv.setItem(3, renameItem);
                inv.setItem(5, createItem(Material.BRUSH, Messages.component(p, "button.recolor_menu"), "recolor_menu"));
                ItemStack leaveItem = createItem(Material.BARRIER, Messages.component(p, "button.leave_team"), "leave_team");
                ItemLore.Builder lore = ItemLore.lore();
                lore.addLine(Messages.component(p, "item_lore.leave_warning_line1"));
                lore.addLine(Messages.component(p, "item_lore.leave_warning_line2"));
                leaveItem.setData(DataComponentTypes.LORE, lore.build());
                inv.setItem(11, leaveItem);
            } else {
                inv.setItem(11, createItem(Material.BARRIER, Messages.component(p, "button.leave_team"), "leave_team"));
            }
            ItemStack memberItem = createItem(Material.BLUE_WOOL, Messages.component(p, "item.info_team"), "info_team");
            ItemLore.Builder lore = ItemLore.lore();
            lore.addLine(Messages.component(p, "item_lore.team_name", Placeholder.component("name", team.getName())));
            for (UUID memberUuid : team.getMembers()) {
                Player member = Bukkit.getPlayer(memberUuid);
                if (member != null) {
                    if (member.getName().equals(team.getId())) {
                        lore.addLine(Messages.component(p, "item_lore.owner_member", Placeholder.unparsed("name", member.getName())));
                    } else {
                        lore.addLine(Messages.component(p, "item_lore.member", Placeholder.unparsed("name", member.getName())));
                    }
                }
            }
            memberItem.setData(DataComponentTypes.LORE, lore.build());
            inv.setItem(15, memberItem);
        }
        p.openInventory(inv);
    }


    public static void openConfirmLeaveMenu(Player p) {
        MenuHolder holder = new MenuHolder(MenuHolder.MenuType.TEAM_CONFIRM_LEAVE);
        Inventory inv = Bukkit.createInventory(holder, 27, Messages.component(p, "gui.confirm_leave_title"));
        holder.setInventory(inv);

        TeamData team = TeamManager.getInstance().getPlayerTeam(p.getUniqueId());
        if (team != null && Objects.equals(team.getId(), p.getName())) {
            ItemStack leaveItem = createItem(Material.GREEN_WOOL, Messages.component(p, "button.yes"), "confirm_leaving");
            ItemLore.Builder lore = ItemLore.lore();
            lore.addLine(Messages.component(p, "item_lore.leave_warning_line1"));
            lore.addLine(Messages.component(p, "item_lore.leave_warning_line2"));
            leaveItem.setData(DataComponentTypes.LORE, lore.build());
            inv.setItem(11, leaveItem);
        } else {
            inv.setItem(11, createItem(Material.GREEN_WOOL, Messages.component(p, "button.yes"), "confirm_leaving"));
        }
        inv.setItem(15, createItem(Material.RED_WOOL, Messages.component(p, "button.no"), "deny_leaving"));
        p.openInventory(inv);
    }

    public static void openRequestsMenu(Player p) {
        TeamManager teamManager = TeamManager.getInstance();
        MenuHolder holder = new MenuHolder(MenuHolder.MenuType.TEAM_REQUESTS);
        Inventory inv = Bukkit.createInventory(holder, 54, Messages.component(p, "gui.requests_title"));
        holder.setInventory(inv);

        int i = 0;
        TeamData playerTeam = teamManager.getPlayerTeam(p.getUniqueId());
        if (playerTeam != null) {
            for (UUID playerUuid : teamManager.getInviteManager().getListOfRequestToTeam(playerTeam.getId())) {
                Player target = Bukkit.getServer().getPlayer(playerUuid);
                if (target != null) {
                    ItemStack item = createItem(Material.GREEN_WOOL, target.getName(), "accept_request");
                    item.setData(DataComponentTypes.LORE, ItemLore.lore()
                            .addLine(Messages.component(p, "item_lore.accept_request"))
                            .build());
                    if (i < 45)
                        inv.setItem(i, item);
                    i++;
                }
            }
        }
        inv.setItem(45, createBackItem(p));
        p.openInventory(inv);
    }

    public static void openManageMenu(Player p) {
        TeamManager teamManager = TeamManager.getInstance();
        MenuHolder holder = new MenuHolder(MenuHolder.MenuType.TEAM_MANAGE);
        Inventory inv = Bukkit.createInventory(holder, 54, Messages.component(p, "gui.manage_title"));
        holder.setInventory(inv);

        int i = 0;
        if (teamManager.getPlayerTeam(p.getUniqueId()) != null) {
            for (UUID playerUuid : teamManager.getPlayerTeam(p.getUniqueId()).getMembers()) {
                Player target = Bukkit.getServer().getPlayer(playerUuid);
                if (target != null && !target.getName().equals(p.getName())) {
                    ItemStack item = createItem(Material.RED_WOOL, target.getName(), "kick");
                    item.setData(DataComponentTypes.LORE, ItemLore.lore()
                            .addLine(Messages.component(p, "item_lore.click_kick"))
                            .build());
                    if (i < 45)
                        inv.setItem(i, item);
                    i++;
                }
            }
            inv.setItem(45, createBackItem(p));
            p.openInventory(inv);
        }
    }

    public static void openAskJoinMenu(Player p) {
        TeamManager teamManager = TeamManager.getInstance();
        MenuHolder holder = new MenuHolder(MenuHolder.MenuType.TEAM_JOIN);
        Inventory inv = Bukkit.createInventory(holder, 54, Messages.component(p, "gui.join_title"));
        holder.setInventory(inv);

        int i = 0;
        for (String teamName : teamManager.getTeams()) {
            TeamData team = teamManager.getTeam(teamName);
            if (team != null) {
                ItemStack item = createItem(BlockUtils.getWoolBlockByNamedTextColor(team.getColor()), team.getNameText(), "join_team_request", team.getId());
                ItemLore.Builder lore = ItemLore.lore();
                if (teamManager.getInviteManager().hasInvite(p.getUniqueId(), team.getId())) {
                    lore.addLine(Messages.component(p, "item_lore.cancel_request"));
                }
                for (UUID memberUuid : team.getMembers()) {
                    Player member = Bukkit.getPlayer(memberUuid);
                    if (member != null) {
                        lore.addLine(Messages.component(p, "item_lore.member", Placeholder.unparsed("name", member.getName())));
                    }
                }
                item.setData(DataComponentTypes.LORE, lore.build());
                if (i < 45)
                    inv.setItem(i, item);
            }
            i++;
        }
        inv.setItem(45, createBackItem(p));
        p.openInventory(inv);
    }

    public static void openRenameMenu(Player p) {
        TeamData team = TeamManager.getInstance().getPlayerTeam(p.getUniqueId());
        if (team != null) {
            Dialog dialog = Dialog.create(builder -> builder.empty()
                    .base(DialogBase.builder(Messages.component(p, "dialog.rename_title"))
                            .inputs(List.of(
                                    DialogInput.text("team_name", Messages.component(p, "dialog.team_name_label").color(team.getColor()))
                                            .initial(team.getNameText())
                                            .build()
                            ))
                            .build()
                    )
                    .type(DialogType.confirmation(
                            ActionButton.create(
                                    Messages.component(p, "dialog.rename_button").color(TextColor.color(0xAEFFC1)),
                                    Messages.component(p, "dialog.rename_button_desc"),
                                    100,
                                    DialogAction.customClick(Key.key("tfl:user_input/confirm"), null)
                            ),
                            ActionButton.create(
                                    Messages.component(p, "dialog.cancel_button").color(TextColor.color(0xFFA0B1)),
                                    Messages.component(p, "dialog.cancel_rename_desc"),
                                    100,
                                    null // If we set the action to null, it doesn't do anything and closes the dialog
                            )
                    ))
            );
            p.showDialog(dialog);
        }
    }

    public static void openRecolorMenu(Player p) {
        TeamData team = TeamManager.getInstance().getPlayerTeam(p.getUniqueId());
        if (team != null) {
            List<ActionButton> actions = new ArrayList<>();

            for (NamedTextColor color : TeamManager.getInstance().getAvailableColors()) {
                String key = "tfl:user_input/recolor/" + color;
                actions.add(ActionButton.create(
                        Component.text(color.toString(), color),
                        Messages.component(p, "dialog.recolor_button_desc"),
                        100,
                        DialogAction.customClick(Key.key(key), null)
                ));
            }

            Dialog dialog = Dialog.create(builder -> builder.empty()
                    .base(DialogBase.builder(Messages.component(p, "dialog.recolor_title")).build())
                    .type(DialogType.multiAction(actions)
                            .exitAction(ActionButton.create(
                                    Messages.component(p, "dialog.cancel_button").color(TextColor.color(0xFFA0B1)),
                                    Messages.component(p, "dialog.cancel_recolor_desc"),
                                    100,
                                    null // If we set the action to null, it doesn't do anything and closes the dialog
                            )).build()
                    )
            );
            p.showDialog(dialog);
        }
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

        if (!(event.getCommonConnection() instanceof PlayerGameConnection conn)) {
            return;
        }
        Player player = conn.getPlayer();

        String teamName = view.getText("team_name");
        if (teamName == null || teamName.isBlank()) {
            Messages.sendError(player, "error.empty_name");
            openRenameMenu(player);
            return;
        }

        TeamData team = TeamManager.getInstance().getPlayerTeam(player.getUniqueId());
        if (team != null) {
            team.rename(teamName);
            Messages.sendPing(player, "team.renamed", Placeholder.component("new_name", Component.text(teamName, team.getColor())));
            openMainMenu(player);
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

        if (!(event.getCommonConnection() instanceof PlayerGameConnection conn)) {
            return;
        }
        Player player = conn.getPlayer();

        String colorString = event.getIdentifier().asString().substring("tfl:user_input/recolor/".length());
        NamedTextColor newColor = NamedTextColor.NAMES.value(colorString);
        if (newColor == null) {
            Messages.sendError(player, "error.invalid_color");
            openRecolorMenu(player);
            return;
        }

        TeamData team = TeamManager.getInstance().getPlayerTeam(player.getUniqueId());
        if (team != null) {
            team.changeColor(newColor);
            Messages.sendPing(player, "team.recolored", Placeholder.component("new_color", Component.text(newColor.toString(), team.getColor())));
            openMainMenu(player);
        }
    }


    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (plugin.getGameManager().getState() == GameState.RUNNING) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!(event.getInventory().getHolder() instanceof MenuHolder holder)) return;

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null) return;

        String buttonId = getButtonCustomModelData(clicked);
        boolean eventCancelled = event.isCancelled();
        event.setCancelled(true);

        switch (holder.getType()) {
            case TEAM_MAIN -> handleMainMenuClick(player, buttonId);
            case TEAM_CONFIRM_LEAVE -> handleConfirmLeaveClick(player, buttonId);
            case TEAM_REQUESTS -> handleRequestsClick(player, clicked, buttonId, event.isRightClick());
            case TEAM_MANAGE -> handleManageClick(player, clicked, buttonId);
            case TEAM_JOIN -> handleJoinClick(player, clicked);
            default -> event.setCancelled(eventCancelled); // Allow other inventories to be clicked
        }
    }

    private void handleMainMenuClick(Player player, String buttonId) {
        if (buttonId == null) return;

        switch (buttonId) {
            case "create_team" -> {
                TeamManager.getInstance().createTeamForPlayer(player);
                plugin.getLogger().info(player.getName() + " a créé une équipe");
                Messages.sendPing(player, "validation.team_created");
                openMainMenu(player);
            }
            case "manage_team" -> openManageMenu(player);
            case "join_team" -> openAskJoinMenu(player);
            case "request_menu" -> openRequestsMenu(player);
            case "leave_team" -> openConfirmLeaveMenu(player);
            case "rename_menu" -> openRenameMenu(player);
            case "recolor_menu" -> openRecolorMenu(player);
            default -> {}
        }
    }

    private void handleConfirmLeaveClick(Player player, String buttonId) {
        if ("confirm_leaving".equals(buttonId)) {
            TeamManager.getInstance().removePlayerFromTeam(player);
            Messages.send(player, "team.left_team");
            openMainMenu(player);
        } else if ("deny_leaving".equals(buttonId)) {
            openMainMenu(player);
        }
    }

    private void handleRequestsClick(Player player, ItemStack clicked, String buttonId, boolean rightClick) {
        if (isBackItem(clicked)) {
            openMainMenu(player);
            return;
        }
        if (!"accept_request".equals(buttonId)) return;
        if (clicked.getItemMeta() == null || clicked.getItemMeta().displayName() == null) return;

        String targetName = plainText(clicked.getItemMeta().displayName());
        Player target = Bukkit.getPlayer(targetName);
        plugin.getLogger().info(targetName);
        if (target == null) return;

        TeamData team = TeamManager.getInstance().getPlayerTeam(player.getUniqueId());
        if (team == null) return;

        if (!TeamManager.getInstance().getInviteManager().hasInvite(target.getUniqueId(), team.getId())) {
            plugin.getLogger().info("no request found for " + targetName);
            Messages.send(player, "team.request_expired");
            openRequestsMenu(player);
            return;
        }

        if (rightClick) {
            TeamManager.getInstance().getInviteManager().remove(target.getUniqueId());
            Messages.send(target, "team.request_denied", Placeholder.component("team_name", team.getName()));
            Messages.send(player, "team.request_declined_notice");
            openRequestsMenu(player);
            return;
        }

        Messages.broadcastTeamPing(team,
                "team.member_joined_broadcast",
                Placeholder.unparsed("player_name", target.getName())
        );
        team.acceptRequest(target.getUniqueId());
        TeamManager.getInstance().addPlayerToVanillaTeam(target, team.getId());
        TeamManager.getInstance().getInviteManager().remove(target.getUniqueId());

        Messages.sendPing(target, "team.request_accepted");
        openRequestsMenu(player);
    }

    private void handleJoinClick(Player player, ItemStack clicked) {
        plugin.getLogger().info("Demander à rejoindre une équipe");

        if (isBackItem(clicked)) {
            openMainMenu(player);
            return;
        }
        String teamId = getButtonCustomModelData(clicked);
        String buttonIdFromItem = getButtonId(clicked);
        if (!"join_team_request".equals(buttonIdFromItem)) return;
        if (clicked.getItemMeta() == null || teamId == null) return;

        plugin.getLogger().info("currentItem " + teamId);

        TeamData teamAsked = TeamManager.getInstance().getTeam(teamId);

        if (teamAsked == null) {
            plugin.getLogger().info("teamAsked is null pour " + teamId);
            return;
        }

        if (TeamManager.getInstance().getInviteManager().hasInvite(player.getUniqueId(), teamAsked.getId())) {
            TeamManager.getInstance().getInviteManager().remove(player.getUniqueId());
            Messages.send(player, "team.request_cancelled");
            openAskJoinMenu(player);
            return;
        }

        plugin.getLogger().info(player.getName() + " à demandé de rejoindre " + teamAsked.getId());

        TeamManager.getInstance().getInviteManager().sendRequest(player.getUniqueId(), teamAsked.getId());

        Messages.sendPing(player, "team.request_sent");
        Player teamOwner = teamAsked.getOwner();
        Messages.sendPing(teamOwner, "team.request_received_broadcast", Placeholder.unparsed("player_name", player.getName())
        );
        openAskJoinMenu(player);
    }

    private void handleManageClick(Player player, ItemStack clicked, String buttonId) {
        if (isBackItem(clicked)) {
            openMainMenu(player);
            return;
        }
        if (!"kick".equals(buttonId)) return;
        if (clicked.getItemMeta() == null || clicked.getItemMeta().displayName() == null) return;

        String targetName = plainText(clicked.getItemMeta().displayName());

        Player kickedPlayer = plugin.getServer().getPlayer(targetName);
        if (kickedPlayer == null) return;
        plugin.getLogger().info("kick" + kickedPlayer.getName());

        TeamData team = TeamManager.getInstance().getTeam(player.getName());
        if (team == null) return;
        TeamManager.getInstance().removePlayerFromTeam(kickedPlayer);
        Messages.send(kickedPlayer, "team.kicked", Placeholder.component("team_name", team.getName()));
        Messages.broadcastTeamPing(
                team,
                "team.kicked_broadcast",
                Placeholder.unparsed("player_name", kickedPlayer.getName())
        );

        player.closeInventory();
    }
}