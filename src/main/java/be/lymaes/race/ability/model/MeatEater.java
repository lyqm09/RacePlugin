package be.lymaes.race.ability.model;

import be.lymaes.race.ability.Consumer;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.EnumSet;

public class MeatEater implements Consumer {

    private static final EnumSet<Material> MEATS = EnumSet.of(
            Material.BEEF, Material.COOKED_BEEF,
            Material.PORKCHOP, Material.COOKED_PORKCHOP,
            Material.CHICKEN, Material.COOKED_CHICKEN,
            Material.MUTTON, Material.COOKED_MUTTON,
            Material.RABBIT, Material.COOKED_RABBIT,
            Material.COD, Material.COOKED_COD,
            Material.SALMON, Material.COOKED_SALMON,
            Material.TROPICAL_FISH, Material.PUFFERFISH,
            Material.ROTTEN_FLESH
    );

    private static final EnumSet<Material> IGNORED_CONSUMABLES = EnumSet.of(
            Material.POTION,
            Material.MILK_BUCKET,
            Material.HONEY_BOTTLE,

            Material.CHARCOAL,
            Material.GLOWSTONE_DUST
    );

    public void onConsume(PlayerItemConsumeEvent e) {
        ItemStack item = e.getItem();
        Material consumedItem = item.getType();

        if (IGNORED_CONSUMABLES.contains(consumedItem)) return;
        if (MEATS.contains(consumedItem)) return;

        e.getPlayer().sendMessage("Beurk !");
        e.setCancelled(true);
    }

}
