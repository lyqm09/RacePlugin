package be.lymaes.race.ability.model;

import be.lymaes.race.RaceProfile;
import be.lymaes.race.ability.Ability;
import be.lymaes.race.ability.Merchant;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.MerchantRecipe;

public class KaryuMerchantExp implements Merchant {

    public void onTrade(InventoryClickEvent e, MerchantInventory inventory, RaceProfile profile) {
        if(e.getSlotType() != InventoryType.SlotType.RESULT)
            return;

        ItemStack item = e.getCurrentItem();
        if(item == null || item.getType() == Material.AIR)
            return;

        MerchantRecipe recipe = inventory.getSelectedRecipe();
        if(recipe == null)
            return;

        int tradeCount = e.isShiftClick() ? calculateRealTradeCount(inventory, recipe) : 1;

        profile.addExp(10 * tradeCount);
    }

    private int calculateRealTradeCount(MerchantInventory inv, MerchantRecipe recipe) {
        ItemStack firstSlot = inv.getItem(0);
        ItemStack firstIngredient = recipe.getAdjustedIngredient1();
        int firstMaxTrades = (firstSlot != null && firstIngredient != null) ? firstSlot.getAmount() / firstIngredient.getAmount() : 0;

        int secondMaxTrades = Integer.MAX_VALUE;
        if (recipe.getIngredients().size() > 1) {
            ItemStack secondSlot = inv.getItem(1);
            ItemStack secondIngredient = recipe.getIngredients().get(1);
            secondMaxTrades = (secondSlot != null) ? secondSlot.getAmount() / secondIngredient.getAmount() : 0;
        }

        int remainingUses = recipe.getMaxUses() - recipe.getUses();

        return Math.min(Math.min(firstMaxTrades, secondMaxTrades), remainingUses);
    }

}
