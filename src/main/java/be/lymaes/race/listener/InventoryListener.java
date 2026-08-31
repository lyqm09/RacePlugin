package be.lymaes.race.listener;

import be.lymaes.race.Race;
import be.lymaes.race.RaceProfile;
import be.lymaes.race.gui.IRaceGUI;
import be.lymaes.race.gui.RaceInventoryHolder;
import be.lymaes.race.item.IStaticItem;
import be.lymaes.race.manager.GUIManager;
import be.lymaes.race.manager.ItemManager;
import be.lymaes.race.manager.RaceManager;
import be.lymaes.race.model.IRace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.MerchantInventory;

public class InventoryListener implements Listener {

    private final GUIManager guiManager;
    private final RaceManager raceManager;
    private final ItemManager itemManager;

    public InventoryListener(Race plugin) {
        this.guiManager = plugin.getGuiManager();
        this.raceManager = plugin.getRaceManager();
        this.itemManager = plugin.getItemManager();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Inventory inventory = e.getClickedInventory();
        if(inventory == null)
            return;

        if(!(e.getWhoClicked() instanceof Player player)) return;

        if(inventory.getHolder() instanceof RaceInventoryHolder) {
            IRaceGUI gui = guiManager.getGUI(inventory);
            if (gui == null) return;

            gui.onClick(e);
        }
        else if(inventory instanceof MerchantInventory merchantInventory) {
            RaceProfile profile = raceManager.getProfile(player);
            if(profile == null) return;

            IRace model = raceManager.getRaceModel(profile.raceData.getRace());
            if(!(model instanceof Merchant merchant)) return;

            merchant.onTrade(e, merchantInventory, profile);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if(itemManager.getItem(e.getCurrentItem()) instanceof IStaticItem) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrag(InventoryMoveItemEvent e) {
        if(itemManager.getItem(e.getItem()) instanceof IStaticItem) {
            e.setCancelled(true);
        }
    }

}
