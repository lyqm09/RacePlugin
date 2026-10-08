package be.lymaes.race.ability;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.PlayerInventory;

public interface InteractOwnInventory extends Ability {

    void onClick(InventoryClickEvent e, PlayerInventory inventory, Player player);

}
