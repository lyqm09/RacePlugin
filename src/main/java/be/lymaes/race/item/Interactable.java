package be.lymaes.race.item;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public interface Interactable {

    void onInteract(PlayerInteractEvent e, Player player, ItemStack item);

}
