package be.lymaes.race.item.model;

import be.lymaes.race.RaceProfile;
import be.lymaes.race.item.ARaceItem;
import be.lymaes.race.item.Consumable;
import be.lymaes.race.item.Droppable;
import be.lymaes.race.item.RaceItem;
import be.lymaes.race.model.IRace;
import org.bukkit.Sound;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.bukkit.inventory.meta.components.consumable.ConsumableComponent;

import java.util.concurrent.ThreadLocalRandom;

public class TamashiHeart extends ARaceItem implements Consumable, Droppable {

    private static final double TOL = 0.001;
    private static final int BONUS_EXP = 500;

    @Override
    public RaceItem getType() {
        return RaceItem.TAMASHI_HEART;
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
        if(!(e.getEntity() instanceof Monster)) return;

        double random = ThreadLocalRandom.current().nextDouble();
        if(random > TOL)
            return;

        ItemStack tamashiHeart = this.getItem();
        e.getDrops().add(tamashiHeart);
    }

    @Override
    public void onConsume(Player player, RaceProfile profile, IRace model) {
        profile.addExp(BONUS_EXP);
    }

}
