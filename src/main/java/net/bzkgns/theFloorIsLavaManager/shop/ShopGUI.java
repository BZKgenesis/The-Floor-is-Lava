package net.bzkgns.theFloorIsLavaManager.shop;

import net.bzkgns.theFloorIsLavaManager.TheFloorIsLavaManager;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import net.bzkgns.theFloorIsLavaManager.items.ItemManager;
import net.bzkgns.theFloorIsLavaManager.lang.Messages;
import net.bzkgns.theFloorIsLavaManager.utils.MenuHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.*;
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
import static net.bzkgns.theFloorIsLavaManager.utils.TextUtils.plainText;

@SuppressWarnings("UnstableApiUsage")
public class ShopGUI implements Listener {

    private static final int SIZE = 54;
    private static final int[] RESULT_SLOTS = {
            0, 9, 18, 27, 36
    };

    private static final List<ShopRecipe> RECIPES = new ArrayList<>();
    //private static final Map<Inventory, BukkitRunnable> RUNNABLES = new HashMap<>();

    @SuppressWarnings("unused")
    public static Map<ItemStack, List<RecipeChoice>> getAvailableRecipes(TheFloorIsLavaManager plugin){
        Map<ItemStack, List<RecipeChoice>> recipes = new HashMap<>();
        for (String recipe_key : TheFloorIsLavaManager.RECIPES_KEY){
            NamespacedKey key = new NamespacedKey(plugin, recipe_key);
            Recipe recipe = Bukkit.getRecipe(key);
            if (recipe !=null){
                List<RecipeChoice> choices = new ArrayList<>();
                ItemStack result = recipe.getResult();
                if (recipe instanceof ShapedRecipe recipeShaped){
                    choices.addAll(recipeShaped.getChoiceMap().values());
                }
                if (recipe instanceof ShapelessRecipe recipeShaped){
                    choices.addAll(recipeShaped.getChoiceList());
                }
                recipes.put(result,choices);


            }

        }
        return recipes;
    }

    public static void loadRecipes() {
        RECIPES.clear();
        TheFloorIsLavaManager plugin = JavaPlugin.getPlugin(TheFloorIsLavaManager.class);

        for (String recipe_key : ItemManager.getAllCraftableItemKeys(Bukkit.getServer())){
            //plugin.getLogger().info("recipe : "+recipe_key);
            NamespacedKey key = new NamespacedKey(plugin, recipe_key);
            Recipe recipe = Bukkit.getRecipe(key);
            if (recipe !=null){
                //plugin.getLogger().info("recipe valide : "+recipe_key);
                IngredientEntries choices = new IngredientEntries();
                ItemStack result = recipe.getResult();
                if (recipe instanceof ShapedRecipe recipeShaped){
                    //plugin.getLogger().info("recipe shaped : "+recipe_key);
                    for (Map.Entry<Character,RecipeChoice> recipeChoice : recipeShaped.getChoiceMap().entrySet()){
                        if (recipeChoice.getValue() !=null){
                            //plugin.getLogger().info("recipeChoice : "+recipe_key+"  "+recipeChoice.toString());
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
                    //plugin.getLogger().info("recipe shapeless : "+recipe_key);
                    for (RecipeChoice recipeChoice : recipeShaped.getChoiceList()){
                        //plugin.getLogger().info("recipeChoice : "+recipe_key+"  "+recipeChoice.toString());
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
        MenuHolder holder = new MenuHolder(MenuHolder.MenuType.SHOP, page);
        Inventory inv = Bukkit.createInventory(holder, SIZE, Messages.component(p,"gui.shop.title", Placeholder.unparsed("page", String.valueOf(page+1))));
        holder.setInventory(inv);
        Map<Integer, IngredientDisplay> animated = new HashMap<>();
        TheFloorIsLavaManager plugin = JavaPlugin.getPlugin(TheFloorIsLavaManager.class);

        int start = page * RESULT_SLOTS.length;

        for (int i = 0; i < RESULT_SLOTS.length; i++) {
            if (start + i >= RECIPES.size()) break;

            ShopRecipe recipe = RECIPES.get(start + i);
            ItemStack display = recipe.result(p).clone();
            List<Component> display_lore = display.lore();
            if (display_lore == null) display_lore = new ArrayList<>();
            display_lore.add(Messages.component(p, "gui.shop.click_to_exchange"));
            display.lore(display_lore);
            inv.setItem(RESULT_SLOTS[i], display);

            int slot = RESULT_SLOTS[i];

            ItemStack resultItem = new ItemStack(Material.PAPER);
            resultItem.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addString("result").build());
            resultItem.setData(DataComponentTypes.ITEM_NAME, Component.empty());
            inv.setItem(slot+1, resultItem);

            for (IngredientEntry ing : recipe.ingredients()) {
                IngredientDisplay d = buildDisplay(ing);
                animated.put(slot, d);
                inv.setItem(slot+2, d.options.getFirst());
                slot++;
            }
        }

        // boutons
        inv.setItem(45, navItem(Messages.component(p, "gui.shop.previous_page"), ArrowDirection.LEFT));
        inv.setItem(53, navItem(Messages.component(p, "gui.shop.next_page"), ArrowDirection.RIGHT));

        p.openInventory(inv);

        BukkitRunnable r = animationRunnable(inv, animated);
        r.runTaskTimer(plugin, 10L, 10L);
        //RUNNABLES.put(inv, r);
    }
    private static IngredientDisplay buildDisplay(IngredientEntry ingredientEntry) {
        IngredientDisplay ingredientDisplay = new IngredientDisplay();
        ingredientDisplay.amount = ingredientEntry.amount;
        ingredientDisplay.options = new ArrayList<>();

        if (ingredientEntry.choice instanceof RecipeChoice.MaterialChoice materialChoice) {
            for (Material m : materialChoice.getChoices()) {
                ItemStack itemStack = new ItemStack(m);
                itemStack.setAmount(ingredientEntry.amount);
                ingredientDisplay.options.add(itemStack);
            }
        }

        if (ingredientEntry.choice instanceof RecipeChoice.ExactChoice exact) {
            for (ItemStack it : exact.getChoices()) {
                ItemStack clone = it.clone();
                clone.setAmount(ingredientEntry.amount);
                ingredientDisplay.options.add(clone);
            }
        }
        return ingredientDisplay;
    }

    private static List<ItemStack> getOptions(IngredientEntry ingredientEntry) {
        List<ItemStack> options = new ArrayList<>();

        if (ingredientEntry.choice instanceof RecipeChoice.MaterialChoice mat) {
            for (Material material : mat.getChoices()) {
                options.add(new ItemStack(material));
            }
        }

        if (ingredientEntry.choice instanceof RecipeChoice.ExactChoice exact) {
            for (ItemStack itemStack : exact.getChoices()) {
                options.add(itemStack.clone());
            }
        }
        return options;
    }

    enum ArrowDirection {
        LEFT, RIGHT
    }

    private static ItemStack navItem(Component name, ArrowDirection direction) {
        ItemStack it = new ItemStack(Material.ARROW);
        switch (direction){
            case LEFT -> it.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addString("left"));
            case RIGHT -> it.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addString("right"));
        }

        ItemMeta meta = it.getItemMeta();
        meta.displayName(name);
        it.setItemMeta(meta);
        return it;
    }

    private static int getMaxPages(){
        return RECIPES.size()/ RESULT_SLOTS.length;
    }

    private static int page(InventoryClickEvent event) {
        String title = plainText(event.getView().title());

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

    private static BukkitRunnable animationRunnable(Inventory inventory, Map<Integer, IngredientDisplay> integerIngredientDisplayMap) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                if (inventory.getViewers().isEmpty()) {
                    cancel();
                    return;
                }

                for (var ingredientDisplayEntry : integerIngredientDisplayMap.entrySet()) {
                    IngredientDisplay d = ingredientDisplayEntry.getValue();
                    if (d.options.size() <= 1) continue;

                    d.index = (d.index+1) % d.options.size();
                    ItemStack show = d.options.get(d.index).clone();
                    show.setAmount(d.amount);
                    inventory.setItem(ingredientDisplayEntry.getKey()+2, show);
                }
            }
        };
    }
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getClickedInventory() instanceof PlayerInventory) return;
        if (!(event.getView().getTopInventory().getHolder() instanceof MenuHolder holder)) return;
        if (holder.getType() != MenuHolder.MenuType.SHOP) return;
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;

        if (event.getSlot() == 45) open(player, max(page(event)-1,0));
        if (event.getSlot() == 53) open(player, min(getMaxPages(),page(event)+1));

        int idx = indexFromSlot(event.getSlot(), page(event));
        if (idx < 0 || idx >= RECIPES.size()) return;

        ShopRecipe recipe = RECIPES.get(idx);


        if (player.getInventory().firstEmpty() == -1) {
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 1f);
            return;
        }

        if (player.getGameMode() != GameMode.CREATIVE){
            if (!canPay(player, recipe)) {
                Messages.send(player, "shop.not_enough_ingredients");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 1f);
                return;
            }else{
                pay(player, recipe);
            }
        }

        player.getInventory().addItem(recipe.result().clone());
        Messages.send(player, "shop.exchange_success");
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1f);
    }

    private static boolean canPay(Player p, ShopRecipe r) {
        for (IngredientEntry ing : r.ingredients()) {
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
        for (IngredientEntry ing : r.ingredients()) {
            for (ItemStack opt : getOptions(ing)) {
                if (InventoryUtils.hasEnough(p, opt, ing.amount)) {
                    InventoryUtils.remove(p, opt, ing.amount);
                    break;
                }
            }
        }
    }
}