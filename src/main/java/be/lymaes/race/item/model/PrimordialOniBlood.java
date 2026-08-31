package be.lymaes.race.item.model;

import be.lymaes.race.Messager;
import be.lymaes.race.Race;
import be.lymaes.race.data.IRaceData;
import be.lymaes.race.data.OniData;
import be.lymaes.race.item.*;
import be.lymaes.race.manager.ItemManager;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.List;

public class PrimordialOniBlood extends ARaceItem implements Splashable, Craftable {

    @Override
    public RaceItem getType() {
        return RaceItem.PRIMORDIAL_ONI_BLOOD;
    }

    @Override
    protected void applyMeta(ItemMeta meta) {
        if(!(meta instanceof PotionMeta potion)) return;

        potion.setColor(Color.RED);
    }

    @Override
    public void onSplash(List<Player> players, List<IRaceData> data) {
        Messager messager = Race.getInstance().getMessager();
        for(int i = 0; i < players.size(); i++) {
            IRaceData raceData = data.get(i);
            if(raceData instanceof OniData) return;

            Player player = players.get(i);
            messager.sendOniChoice(player, raceData);
        }

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
                "NGN",
                "GKG",
                "NGN"
        );

        ItemStack kazanStone = itemManager.getItem(RaceItem.KAZAN_STONE.id).getItem();
        recipe.setIngredient('K', new RecipeChoice.ExactChoice(kazanStone));
        recipe.setIngredient('G', Material.GLASS);
        recipe.setIngredient('N', Material.NETHERITE_BLOCK);

        Bukkit.addRecipe(recipe);
    }

}
