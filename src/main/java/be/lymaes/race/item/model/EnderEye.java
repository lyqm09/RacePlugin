package be.lymaes.race.item.model;

import be.lymaes.race.item.ARaceItem;
import be.lymaes.race.item.Droppable;
import be.lymaes.race.item.RaceItem;
import org.bukkit.entity.Enderman;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.concurrent.ThreadLocalRandom;

public class EnderEye extends ARaceItem implements Droppable {

    public static final double TOL = 0.0005;

    @Override
    public RaceItem getType() {
        return RaceItem.ENDER_EYE;
    }

    @Override
    protected void applyMeta(ItemMeta meta) {
        meta.setEnchantmentGlintOverride(true);
    }

    @Override
    public void onDrop(EntityDeathEvent e) {
        if(!(e.getEntity() instanceof Enderman)) return;

        double random = ThreadLocalRandom.current().nextDouble();
        if(random > TOL) return;

        ItemStack enderEye = this.getItem();
        e.getDrops().add(enderEye);
    }

}
