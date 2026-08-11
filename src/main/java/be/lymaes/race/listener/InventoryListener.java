package be.lymaes.race.listener;

import be.lymaes.race.Race;
import be.lymaes.race.gui.IRaceGUI;
import be.lymaes.race.gui.RaceInventoryHolder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class InventoryListener implements Listener {

    private final Race plugin;

    public InventoryListener(Race plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Inventory inventory = e.getClickedInventory();
        if(inventory == null)
            return;

        if(!(inventory.getHolder() instanceof RaceInventoryHolder))
            return;

        IRaceGUI gui = plugin.getGuiManager().getGUI(e.getInventory());
        if(gui == null)
            return;

        gui.onClick(e);
    }

}
