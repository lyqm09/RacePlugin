package be.lymaes.race.ability;

import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public interface Helder extends Ability {

    void onSwapOn(ItemStack item);
    void onSwapOff(ItemStack item);

    void onPickup(ItemStack itemInHand, ItemStack pickupItem);
    void onDrops(EntityDeathEvent e);

}
