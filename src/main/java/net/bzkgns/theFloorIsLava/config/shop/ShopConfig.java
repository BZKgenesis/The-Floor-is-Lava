package net.bzkgns.theFloorIsLava.config.shop;

import net.bzkgns.theFloorIsLava.config.ConfigKey;
import net.bzkgns.theFloorIsLava.config.ConfigSection;
import net.bzkgns.theFloorIsLava.config.ListConfigKey;
import net.bzkgns.theFloorIsLava.currency.Price;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShopConfig implements ConfigSection<ShopConfig> {

    private List<ShopSellItem> shopSellableItems = new ArrayList<>(List.of(
            new ShopSellItem("OAK_PLANKS",     0, 2),
            new ShopSellItem("BIRCH_PLANKS",   0, 2),
            new ShopSellItem("DARK_OAK_PLANKS",0, 2),
            new ShopSellItem("SPRUCE_PLANKS",  0, 2),
            new ShopSellItem("JUNGLE_PLANKS",  0, 2),
            new ShopSellItem("MANGROVE_PLANKS",0, 5),
            new ShopSellItem("BAMBOO_PLANKS",  0, 5),
            new ShopSellItem("PALE_OAK_PLANKS",0,10),
            new ShopSellItem("ACACIA_PLANKS",  0, 2),
            new ShopSellItem("CHERRY_PLANKS",  0, 7),
            new ShopSellItem("COBBLESTONE",    0, 1),
            new ShopSellItem("DIRT",           1, 0),
            new ShopSellItem("SAND",           1, 0),
            new ShopSellItem("RED_SAND",       1, 0),
            new ShopSellItem("GRANITE",        0, 3),
            new ShopSellItem("DIORITE",        0, 3),
            new ShopSellItem("ANDESITE",       0, 3),
            new ShopSellItem("COAL",           2, 0),
            new ShopSellItem("COPPER_INGOT",   3, 0),
            new ShopSellItem("IRON_INGOT",     5, 0),
            new ShopSellItem("REDSTONE",       2, 0),
            new ShopSellItem("LAPIS_LAZULI",   3, 0),
            new ShopSellItem("GOLD_INGOT",     8, 0),
            new ShopSellItem("DIAMOND",       15, 0),
            new ShopSellItem("EMERALD",       50, 0)
    ));

    private List<ShopBuyCustomItem> shopBuyableCustomItems = new ArrayList<>(List.of(
            new ShopBuyCustomItem("batte",                 45,   0),
            new ShopBuyCustomItem("shears",                20,  10),
            new ShopBuyCustomItem("egg_bridge",            45,  30),
            new ShopBuyCustomItem("popup_tower",           25,  45),
            new ShopBuyCustomItem("snowball_plate",        15,  35),
            new ShopBuyCustomItem("team_respawn_anchor",  150, 100),
            new ShopBuyCustomItem("infinite_wool",         20,  60),
            new ShopBuyCustomItem("feather_falling_boots", 35,  10),
            new ShopBuyCustomItem("fireball",              45,  20),
            new ShopBuyCustomItem("tnt",                   35,  25),
            new ShopBuyCustomItem("parachute",             18,  12),
            new ShopBuyCustomItem("heal_camp",             90,  40),
            new ShopBuyCustomItem("gambling",              50,  50),
            new ShopBuyCustomItem("throwable_iron_golem",  50,  50),
            new ShopBuyCustomItem("team_inventory",        70,  80)
    ));

    private List<ShopBuyVanillaItem> shopBuyableVanillaItems = new ArrayList<>(List.of(
            new ShopBuyVanillaItem("ENDER_PEARL",           45,  10),
            new ShopBuyVanillaItem("OBSIDIAN",           10,0,  500)
    ));

    public static final ListConfigKey<ShopConfig, ShopSellItem> SELLABLE_ITEMS = new ListConfigKey<>(
            "sellable_items",
            "config.sellable_items.description",
            ShopConfig::getShopSellableItems,
            ShopConfig::setShopSellableItems,

            item -> Map.of(
                    "id", item.id(),
                    "resource", item.resource(),
                    "material", item.material()
            ),

            map -> new ShopSellItem(
                    String.valueOf(map.get("id")),
                    ((Number) map.get("resource")).intValue(),
                    ((Number) map.get("material")).intValue()
            ),
            ShopSellItem::id,
            List.of(
                    new ListConfigKey.ElementField<>(
                            "config.sellable_items.field.resource.description",
                            ShopSellItem::resource,
                            (item, value) -> new ShopSellItem(item.id(), (int) Math.round(value), item.material())
                    ),
                    new ListConfigKey.ElementField<>(
                            "config.sellable_items.field.material.description",
                            ShopSellItem::material,
                            (item, value) -> new ShopSellItem(item.id(), item.resource(), (int) Math.round(value))
                    )
            )
    );

    public static final ListConfigKey<ShopConfig, ShopBuyCustomItem> BUYABLE_ITEMS_CUSTOM = new ListConfigKey<>(
            "buyable_items.custom",
            "config.buyable_items.custom.description",
            ShopConfig::getShopBuyableCustomItems,
            ShopConfig::setShopBuyableCustomItems,

            item -> Map.of(
                    "id", item.id(),
                    "quantity", item.quantity(),
                    "resource", item.resource(),
                    "material", item.material()
            ),

            map -> new ShopBuyCustomItem(
                    String.valueOf(map.get("id")),
                    ((Number) map.get("quantity")).intValue(),
                    ((Number) map.get("resource")).intValue(),
                    ((Number) map.get("material")).intValue()
            ),
            ShopBuyCustomItem::id,
            List.of(
                    new ListConfigKey.ElementField<>(
                            "config.buyable_items.custom.field.quantity.description",
                            ShopBuyCustomItem::quantity,
                            (item, value) -> new ShopBuyCustomItem(item.id(), (int) Math.round(value), item.resource(), item.material())
                    ),
                    new ListConfigKey.ElementField<>(
                            "config.buyable_items.custom.field.resource.description",
                            ShopBuyCustomItem::resource,
                            (item, value) -> new ShopBuyCustomItem(item.id(), item.quantity(), (int) Math.round(value), item.material())
                    ),
                    new ListConfigKey.ElementField<>(
                            "config.buyable_items.custom.field.material.description",
                            ShopBuyCustomItem::material,
                            (item, value) -> new ShopBuyCustomItem(item.id(), item.quantity(), item.resource(), (int) Math.round(value))
                    )
            )
    );

    public static final ListConfigKey<ShopConfig, ShopBuyVanillaItem> BUYABLE_ITEMS_VANILLA = new ListConfigKey<>(
            "buyable_items.vanilla",
            "config.buyable_items.vanilla.description",
            ShopConfig::getShopBuyableVanillaItems,
            ShopConfig::setShopBuyableVanillaItems,

            item -> Map.of(
                    "id", item.id(),
                    "quantity", item.quantity(),
                    "resource", item.resource(),
                    "material", item.material()
            ),

            map -> new ShopBuyVanillaItem(
                    String.valueOf(map.get("id")),
                    ((Number) map.get("quantity")).intValue(),
                    ((Number) map.get("resource")).intValue(),
                    ((Number) map.get("material")).intValue()
            ),
            ShopBuyVanillaItem::id,
            List.of(
                    new ListConfigKey.ElementField<>(
                            "config.buyable_items.vanilla.field.quantity.description",
                            ShopBuyVanillaItem::quantity,
                            (item, value) -> new ShopBuyVanillaItem(item.id(), (int) Math.round(value), item.resource(), item.material())
                    ),
                    new ListConfigKey.ElementField<>(
                            "config.buyable_items.vanilla.field.resource.description",
                            ShopBuyVanillaItem::resource,
                            (item, value) -> new ShopBuyVanillaItem(item.id(), item.quantity(), (int) Math.round(value), item.material())
                    ),
                    new ListConfigKey.ElementField<>(
                            "config.buyable_items.vanilla.field.material.description",
                            ShopBuyVanillaItem::material,
                            (item, value) -> new ShopBuyVanillaItem(item.id(), item.quantity(), item.resource(), (int) Math.round(value))
                    )
            )
    );

    @Override
    public String getName() {
        return "shop";
    }

    @Override
    public List<ConfigKey<ShopConfig, ?>> getKeys() {
        return List.of(SELLABLE_ITEMS, BUYABLE_ITEMS_CUSTOM, BUYABLE_ITEMS_VANILLA);
    }

    public List<ShopSellItem> getShopSellableItems() {
        return shopSellableItems;
    }

    public void setShopSellableItems(List<ShopSellItem> shopSellableItems) {
        this.shopSellableItems = shopSellableItems;
    }

    public List<ShopBuyCustomItem> getShopBuyableCustomItems() {
        return shopBuyableCustomItems;
    }

    public void setShopBuyableCustomItems(List<ShopBuyCustomItem> shopBuyableCustomItems) {
        this.shopBuyableCustomItems = shopBuyableCustomItems;
    }

    public List<ShopBuyVanillaItem> getShopBuyableVanillaItems() {
        return shopBuyableVanillaItems;
    }

    public void setShopBuyableVanillaItems(List<ShopBuyVanillaItem> shopBuyableVanillaItems) {
        this.shopBuyableVanillaItems = shopBuyableVanillaItems;
    }

    public Price getBuyPriceCustomItem(String itemId) {
        return shopBuyableCustomItems.stream()
                .filter(item -> item.id().equals(itemId))
                .findFirst()
                .map(item -> new Price(Math.round(item.resource()), Math.round(item.material()),0))
                .orElse(null);
    }
}
