package be.lymaes.race.item.model;

import be.lymaes.race.RaceProfile;
import be.lymaes.race.item.ARaceItem;
import be.lymaes.race.item.Consumable;
import be.lymaes.race.item.Droppable;
import be.lymaes.race.item.RaceItem;
import be.lymaes.race.model.IRace;
import be.lymaes.race.model.Oni;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.bukkit.inventory.meta.components.consumable.ConsumableComponent;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.ThreadLocalRandom;

public class KazanStone extends ARaceItem implements Consumable, Droppable {

    private static final double TOL = 0.0005;

    @Override
    public RaceItem getType() {
        return RaceItem.KAZAN_STONE;
    }

    @Override
    protected void applyMeta(ItemMeta meta) {
        meta.setEnchantmentGlintOverride(true);

        ConsumableComponent consumable = meta.getConsumable();
        consumable.setAnimation(ConsumableComponent.Animation.EAT);
        consumable.setConsumeSeconds(1.6f);
        consumable.setSound(Sound.ENTITY_ITEM_BREAK);
        meta.setConsumable(consumable);

        FoodComponent food = meta.getFood();
        food.setNutrition(0);
        food.setSaturation(0);
        food.setCanAlwaysEat(true);
        meta.setFood(food);
    }

    @Override
    public void onDrop(EntityDeathEvent e) {
        if(!(e.getEntity() instanceof WitherSkeleton))
            return;

        double random = ThreadLocalRandom.current().nextDouble();
        if(random > TOL)
            return;

        ItemStack kazanStone = this.getItem();
        e.getDrops().add(kazanStone);
    }

    @Override
    public void onConsume(Player player, RaceProfile profile, IRace model) {
        if(model instanceof Oni oni) {
            int nextRank = profile.raceData.getRank() + 1;
            if(nextRank < Oni.Rank.GENERAL.rank) return;

            int expRequired = oni.getExpRequired(nextRank);
            if(expRequired < 0) return;
            if (profile.raceData.getExp() < expRequired) return;

            profile.raceData.subExp(expRequired);
            profile.rankUp();

            profile.updateTabInfo();
        } else {
            player.addPotionEffect(PotionEffectType.POISON.createEffect(10 * 20, 1));
        }
    }

}
