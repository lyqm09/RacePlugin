package be.lymaes.race.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jspecify.annotations.NonNull;

public interface IRaceGUI {

    void open(Player player);
    void onClick(InventoryClickEvent e);
    @NonNull Inventory getInventory();
}
