package be.lymaes.race.listener;

import be.lymaes.race.Race;
import be.lymaes.race.RaceProfile;
import be.lymaes.race.ability.AbilityKey;
import be.lymaes.race.item.RaceItem;
import be.lymaes.race.manager.RaceManager;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.Recipe;

public class CraftListener implements Listener {

    private final RaceManager raceManager;

    public CraftListener(Race plugin) {
        this.raceManager = plugin.getRaceManager();
    }

    @EventHandler
    public void onCraft(PrepareItemCraftEvent e) {
        Recipe recipe = e.getRecipe();
        if (recipe == null) return;

        if(!(recipe instanceof Keyed keyedRecipe)) return;
        NamespacedKey key = keyedRecipe.getKey();

        if(!key.getNamespace().equals("race")) return;

        if(key.getKey().equals(RaceItem.PRIMORDIAL_ONI_BLOOD.id)) {
            if(canCraft(e.getView().getPlayer())) return;

            e.getInventory().setResult(null);
        }
    }

    public boolean canCraft(HumanEntity viewer) {
        if(!(viewer instanceof Player player)) return false;

        RaceProfile profile = raceManager.getProfile(player);
        if(profile == null) return false;

        return profile.hasAbility(AbilityKey.CRAFT_PRIMORDIAL_ONI_BLOOD);
    }

}
