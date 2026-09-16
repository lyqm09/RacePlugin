package be.lymaes.race.item.model;

import be.lymaes.race.item.ARaceItem;
import be.lymaes.race.item.Craftable;
import be.lymaes.race.item.RaceItem;
import be.lymaes.race.manager.ItemManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

public class VillageHeart extends ARaceItem implements Craftable {

    @Override
    public RaceItem getType() {
        return RaceItem.VILLAGE_HEART;
    }

    @Override
    public void craft(ItemManager itemManager) {
        NamespacedKey key = NamespacedKey.fromString("race:" + getType().id);
        if(key == null) {
            System.err.println("Impossible to add the craft of " + getType().name);
            return;
        }

        ShapedRecipe recipe = new ShapedRecipe(key, getItem());
        recipe.shape(
                "SSS",
                "SCS",
                "STS"
        );

        ItemStack tamashiHeart = itemManager.getItem(RaceItem.TAMASHI_HEART.id).getItem();
        ItemStack sanctuaryHeart = itemManager.getItem(RaceItem.SANCTUARY_HEART.id).getItem();

        recipe.setIngredient('C', new RecipeChoice.ExactChoice(sanctuaryHeart));
        recipe.setIngredient('T', new RecipeChoice.ExactChoice(tamashiHeart));
        recipe.setIngredient('S', Material.STONE);

        Bukkit.addRecipe(recipe);
    }
}
