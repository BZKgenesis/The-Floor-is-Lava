package net.bzkgns.theFloorIsLavaManager.shop;

import net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaManager;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.*;

public class ShopGUI implements Listener {

    private static final int SIZE = 54;
    private static final int[] RESULT_SLOTS = {
            0, 9, 18, 27, 36
    };

    private static final List<ShopRecipe> RECIPES = new ArrayList<>();
    private static final Map<Inventory, BukkitRunnable> RUNNABLES = new HashMap<>();

    public static Map<ItemStack, List<RecipeChoice>> getAvailableRecipes(TheFloorIsLavaManager plugin){
        Map<ItemStack, List<RecipeChoice>> recipes = new HashMap<>();
        for (String recipe_key : TheFloorIsLavaManager.RECIPES_KEY){
            NamespacedKey key = new NamespacedKey(plugin, recipe_key);
            Recipe recipe = Bukkit.getRecipe(key);
            if (recipe !=null){
                List<RecipeChoice> choices = new ArrayList<>();
                ItemStack result = recipe.getResult();
                if (recipe instanceof ShapedRecipe recipeShaped){
                    for (RecipeChoice recipeChoice : recipeShaped.getChoiceMap().values()){
                        choices.add(recipeChoice);
                    }
                }
                if (recipe instanceof ShapelessRecipe recipeShaped){
                    for (RecipeChoice recipeChoice : recipeShaped.getChoiceList()){
                        choices.add(recipeChoice);
                    }
                }
                recipes.put(result,choices);


            }

        }
        return recipes;
    }

    public static void loadRecipes() {
        RECIPES.clear();
        TheFloorIsLavaManager plugin = JavaPlugin.getPlugin(TheFloorIsLavaManager.class);

        for (String recipe_key : TheFloorIsLavaManager.RECIPES_KEY){
            plugin.getLogger().info("recipe : "+recipe_key);
            NamespacedKey key = new NamespacedKey(plugin, recipe_key);
            Recipe recipe = Bukkit.getRecipe(key);
            if (recipe !=null){
                plugin.getLogger().info("recipe valide : "+recipe_key);
                IngredientEntries choices = new IngredientEntries();
                ItemStack result = recipe.getResult();
                if (recipe instanceof ShapedRecipe recipeShaped){
                    plugin.getLogger().info("recipe shaped : "+recipe_key);
                    for (Map.Entry<Character,RecipeChoice> recipeChoice : recipeShaped.getChoiceMap().entrySet()){
                        if (recipeChoice.getValue() !=null){
                            plugin.getLogger().info("recipeChoice : "+recipe_key+"  "+recipeChoice.toString());
                            if (choices.containsRecipeChoice(recipeChoice.getValue())){
                                choices.addAmount(recipeChoice.getValue(),1);
                            }else{
                                IngredientEntry ingredientEntry = new IngredientEntry(recipeChoice.getValue(),1);
                                choices.put(ingredientEntry);
                            }
                        }
                    }
                }
                if (recipe instanceof ShapelessRecipe recipeShaped){
                    plugin.getLogger().info("recipe shapeless : "+recipe_key);
                    for (RecipeChoice recipeChoice : recipeShaped.getChoiceList()){
                        plugin.getLogger().info("recipeChoice : "+recipe_key+"  "+recipeChoice.toString());
                        if (choices.containsRecipeChoice(recipeChoice)){
                            choices.addAmount(recipeChoice,1);
                        }else{
                            IngredientEntry ingredientEntry = new IngredientEntry(recipeChoice,1);
                            choices.put(ingredientEntry);
                        }
                    }
                }
                RECIPES.add(new ShopRecipe(
                        result,
                        choices
                ));
            }
        }
        plugin.getLogger().info("recipe loaded "+RECIPES.size());
    }
    public static void open(Player p, int page) {
        Inventory inv = Bukkit.createInventory(null, SIZE, "§6Shop §7(Page " + (page+1) + ")");
        Map<Integer, IngredientDisplay> animated = new HashMap<>();
        TheFloorIsLavaManager plugin = JavaPlugin.getPlugin(TheFloorIsLavaManager.class);

        int start = page * RESULT_SLOTS.length;

        for (int i = 0; i < RESULT_SLOTS.length; i++) {
            if (start + i >= RECIPES.size()) break;

            ShopRecipe recipe = RECIPES.get(start + i);
            ItemStack display = recipe.result.clone();
            display.setLore(List.of("§aClique pour échanger"));
            inv.setItem(RESULT_SLOTS[i], display);

            int base = RESULT_SLOTS[i];
            int slot = base;

            ItemStack resultItem = new ItemStack(Material.PAPER);
            resultItem.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addString("result").build());
            resultItem.setData(DataComponentTypes.ITEM_NAME, Component.empty());
            inv.setItem(slot+1, resultItem);

            for (IngredientEntry ing : recipe.ingredients) {
                IngredientDisplay d = buildDisplay(ing);
                animated.put(slot, d);
                inv.setItem(slot+2, d.options.get(0));
                slot++;
            }
        }

        // boutons
        inv.setItem(45, navItem("§e← Précédent", ArrowDirection.LEFT));
        inv.setItem(53, navItem("§eSuivant →", ArrowDirection.RIGHT));

        p.openInventory(inv);

        BukkitRunnable r = animationRunnable(inv, animated);
        r.runTaskTimer(plugin, 10L, 10L);
        RUNNABLES.put(inv, r);
    }
    private static IngredientDisplay buildDisplay(IngredientEntry ing) {
        IngredientDisplay d = new IngredientDisplay();
        d.amount = ing.amount;
        d.options = new ArrayList<>();

        if (ing.choice instanceof RecipeChoice.MaterialChoice mat) {
            for (Material m : mat.getChoices()) {
                ItemStack it = new ItemStack(m);
                it.setAmount(ing.amount);
                d.options.add(it);
            }
        }

        if (ing.choice instanceof RecipeChoice.ExactChoice exact) {
            for (ItemStack it : exact.getChoices()) {
                ItemStack clone = it.clone();
                clone.setAmount(ing.amount);
                d.options.add(clone);
            }
        }
        return d;
    }

    private static List<ItemStack> getOptions(IngredientEntry ing) {
        List<ItemStack> options = new ArrayList<>();

        if (ing.choice instanceof RecipeChoice.MaterialChoice mat) {
            for (Material m : mat.getChoices()) {
                options.add(new ItemStack(m));
            }
        }

        if (ing.choice instanceof RecipeChoice.ExactChoice exact) {
            for (ItemStack it : exact.getChoices()) {
                options.add(it.clone());
            }
        }
        return options;
    }

    enum ArrowDirection {
        LEFT, RIGHT
    }

    private static ItemStack navItem(String name, ArrowDirection direction) {
        ItemStack it = new ItemStack(Material.ARROW);
        switch (direction){
            case LEFT -> it.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addString("left"));
            case RIGHT -> it.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addString("right"));
        }

        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName(name);
        it.setItemMeta(meta);
        return it;
    }

    private static int getMaxPages(){
        return RECIPES.size()/ RESULT_SLOTS.length;
    }

    private static int page(InventoryClickEvent e) {
        String title = e.getView().getTitle();

        int start = title.indexOf("Page ") + 5;
        int end = title.indexOf(")", start);

        try {
            return max(0, Integer.parseInt(title.substring(start, end)) - 1);
        } catch (Exception ex) {
            return 0;
        }
    }

    private static int indexFromSlot(int slot, int page) {
        for (int i = 0; i < RESULT_SLOTS.length; i++) {
            if (RESULT_SLOTS[i] == slot) {
                return page * RESULT_SLOTS.length + i;
            }
        }
        return -1;
    }

    private static BukkitRunnable animationRunnable(Inventory inv, Map<Integer, IngredientDisplay> map) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                if (inv.getViewers().isEmpty()) {
                    cancel();
                    return;
                }

                for (var e : map.entrySet()) {
                    IngredientDisplay d = e.getValue();
                    if (d.options.size() <= 1) continue;

                    d.index = (d.index+1) % d.options.size();
                    ItemStack show = d.options.get(d.index).clone();
                    show.setAmount(d.amount);
                    inv.setItem(e.getKey()+2, show);
                }
            }
        };
    }
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getClickedInventory() instanceof PlayerInventory) return;
        if (!e.getView().getTitle().startsWith("§6Shop")) return;
        e.setCancelled(true);

        Player p = (Player) e.getWhoClicked();
        Inventory inv = e.getInventory();

        if (e.getSlot() == 45) open(p, max(page(e)-1,0));
        if (e.getSlot() == 53) open(p, min(getMaxPages(),page(e)+1));

        int idx = indexFromSlot(e.getSlot(), page(e));
        if (idx < 0 || idx >= RECIPES.size()) return;

        ShopRecipe recipe = RECIPES.get(idx);

        if (!canPay(p, recipe)) {
            p.sendMessage("§cIngrédients insuffisants.");
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 1f);
            return;
        }

        pay(p, recipe);
        p.getInventory().addItem(recipe.result.clone());
        p.sendMessage("§aÉchange réussi.");
        p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1f);
    }

    public static ItemStack giveShopItem(){
        ItemStack it = new ItemStack(Material.BOOK);
        it.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Shop");
        meta.setLore(List.of("§7Ouvre le menu du shop"));
        it.setItemMeta(meta);
        return it;
    }

    public static boolean isShopItem(ItemStack stack) {
        if (stack.getType() == Material.BOOK) {
            ItemMeta meta = stack.getItemMeta();
            if (meta != null && meta.hasDisplayName()) {
                String name = meta.getDisplayName();
                return name.contains("Shop");
            }
        }
        return false;
    }
    private static boolean canPay(Player p, ShopRecipe r) {
        for (IngredientEntry ing : r.ingredients) {
            boolean ok = false;
            for (ItemStack opt : getOptions(ing)) {
                if (InventoryUtils.hasEnough(p, opt, ing.amount)) {
                    ok = true; break;
                }
            }
            if (!ok) return false;
        }
        return true;
    }

    private static void pay(Player p, ShopRecipe r) {
        for (IngredientEntry ing : r.ingredients) {
            for (ItemStack opt : getOptions(ing)) {
                if (InventoryUtils.hasEnough(p, opt, ing.amount)) {
                    InventoryUtils.remove(p, opt, ing.amount);
                    break;
                }
            }
        }
    }
}