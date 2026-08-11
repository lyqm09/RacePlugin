package be.lymaes.race.gui;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jspecify.annotations.NonNull;

public class RaceInventoryHolder implements InventoryHolder {

    private final Inventory inventory;

    public RaceInventoryHolder(int row, String title) {
        this.inventory = Bukkit.createInventory(this, row * 9, title);
    }

    @Override
    public @NonNull Inventory getInventory() {
        return inventory;
    }
}
