package be.lymaes.race.ability.model;

import be.lymaes.race.ability.Helder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.bukkit.inventory.meta.components.consumable.ConsumableComponent;

public class DirtEater implements Helder {

    public void onSwapOn(ItemStack item) {
        if(item == null || item.getType() != Material.DIRT) return;

        ItemMeta meta = item.getItemMeta();
        if(meta == null) return;

        ConsumableComponent consumable = meta.getConsumable();
        consumable.setAnimation(ConsumableComponent.Animation.EAT);
        consumable.setConsumeSeconds(1.6f);
        consumable.setSound(Sound.ENTITY_GENERIC_EAT);
        meta.setConsumable(consumable);

        FoodComponent food = meta.getFood();
        food.setNutrition(1);
        food.setSaturation(0.5f);
        food.setCanAlwaysEat(true);
        meta.setFood(food);

        item.setItemMeta(meta);
    }

    public void onSwapOff(ItemStack item) {
        if(item == null || item.getType() != Material.DIRT) return;

        ItemMeta meta = item.getItemMeta();
        if(meta == null) return;

        meta.setConsumable(null);
        meta.setFood(null);

        item.setItemMeta(meta);
    }

    public void onPickup(ItemStack itemInHand, ItemStack pickupItem) {
        if(pickupItem == null || itemInHand == null) return;
        if(pickupItem == itemInHand) {
            onSwapOn(itemInHand);
        }
    }

    public void onDrops(EntityDeathEvent e) {
        for(ItemStack item : e.getDrops()) {
            if(item.getType() != Material.DIRT) continue;

            ItemMeta meta = item.getItemMeta();
            if(meta == null || !meta.hasFood()) continue;

            meta.setFood(null);
            item.setItemMeta(meta);
        }
    }

}
