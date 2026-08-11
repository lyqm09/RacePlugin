package be.lymaes.race.listener;

import be.lymaes.race.Race;
import be.lymaes.race.item.IStaticItem;
import be.lymaes.race.manager.ItemManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class StaticItemListener implements Listener {

    private final ItemManager manager;

    public StaticItemListener(Race plugin) {
        manager = plugin.getItemManager();
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if(manager.getItem(e.getCurrentItem()) instanceof IStaticItem) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrag(InventoryMoveItemEvent e) {
        if(manager.getItem(e.getItem()) instanceof IStaticItem) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onSwapHand(PlayerSwapHandItemsEvent e) {
        if(manager.getItem(e.getOffHandItem()) instanceof IStaticItem) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        if(manager.getItem(e.getItemDrop().getItemStack()) instanceof IStaticItem) {
            e.setCancelled(true);
        }
    }

}
