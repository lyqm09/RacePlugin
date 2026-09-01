package be.lymaes.race.ability.model;

import be.lymaes.race.ability.Interact;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class Fireball implements Interact {

    public void onInteract(PlayerInteractEvent e, Player player, int rank) {
        ItemStack item = e.getItem();
        if(item == null || item.getType() != Material.BLAZE_POWDER) return;

        if (e.getAction() != Action.RIGHT_CLICK_AIR) return;

        int amount = item.getAmount();
        if (amount > 1) {
            item.setAmount(amount - 1);
        } else {
            player.getInventory().setItemInMainHand(null);
        }

        player.launchProjectile(org.bukkit.entity.Fireball.class);
        e.setCancelled(true);
    }

}
