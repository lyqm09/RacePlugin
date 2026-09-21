package be.lymaes.race.item.model;

import be.lymaes.race.RaceProfile;
import be.lymaes.race.data.IRaceData;
import be.lymaes.race.item.ARaceItem;
import be.lymaes.race.item.Consumable;
import be.lymaes.race.item.Droppable;
import be.lymaes.race.item.RaceItem;
import be.lymaes.race.model.IRace;
import be.lymaes.race.model.Karyu;
import org.bukkit.Sound;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.bukkit.inventory.meta.components.consumable.ConsumableComponent;

import java.util.concurrent.ThreadLocalRandom;

public class EnderEye extends ARaceItem implements Consumable, Droppable {

    public static final double TOL = 0.0005;

    @Override
    public RaceItem getType() {
        return RaceItem.ENDER_EYE;
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
        if(!(e.getEntity() instanceof Enderman)) return;

        double random = ThreadLocalRandom.current().nextDouble();
        if(random > TOL) return;

        ItemStack enderEye = this.getItem();
        e.getDrops().add(enderEye);
    }

    @Override
    public void onConsume(Player player, RaceProfile profile, IRace model) {
        if(model instanceof Karyu karyu) {
            IRaceData raceData = profile.getRaceData();

            int nextRank = raceData.getRank() + 1;
            if(nextRank < Karyu.Rank.BIG.rank) return;

            int expRequired = karyu.getExpRequired(nextRank);
            if(expRequired < 0) return;
            if (raceData.getExp() < expRequired) return;

            raceData.subExp(expRequired);
            profile.rankUp();

            profile.updateTabInfo();
        } else {
            player.damage(2.0);
            player.sendMessage("Aïe... Tu t'es cassé une dent");
        }
    }

}
