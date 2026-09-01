package be.lymaes.race.listener;

import be.lymaes.race.Race;
import be.lymaes.race.item.RaceItem;
import be.lymaes.race.item.model.PrimordialOniBlood;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.Recipe;

public class CraftListener implements Listener {

    public CraftListener(Race plugin) {

    }

    @EventHandler
    public void onCraft(PrepareItemCraftEvent e) {
        Recipe recipe = e.getRecipe();
        if (recipe == null) return;

        if(!(recipe instanceof Keyed keyedRecipe)) return;
        NamespacedKey key = keyedRecipe.getKey();

        if(!key.getNamespace().equals("race")) return;

        // TODO

        if(key.getKey().equals(RaceItem.PRIMORDIAL_ONI_BLOOD.id)) {
            HumanEntity viewer = e.getView().getPlayer();

            if(viewer.hasPermission(PrimordialOniBlood.PERM_CRAFT)) return;

            e.getInventory().setResult(null);
        }
    }

}
