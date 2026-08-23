package be.lymaes.race.listener;

import be.lymaes.race.Race;
import be.lymaes.race.RaceProfile;
import be.lymaes.race.ability.Helder;
import be.lymaes.race.item.Heldable;
import be.lymaes.race.item.IRaceItem;
import be.lymaes.race.item.IStaticItem;
import be.lymaes.race.manager.ItemManager;
import be.lymaes.race.manager.RaceManager;
import be.lymaes.race.model.IRace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

public class InteractListener implements Listener {

    private final RaceManager raceManager;
    private final ItemManager itemManager;

    public InteractListener(Race plugin) {
        this.raceManager = plugin.getRaceManager();
        this.itemManager = plugin.getItemManager();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();

        heldModelInteract(e, player);
        heldItemInteract(e, player);
    }

    private void heldModelInteract(PlayerInteractEvent e, Player player) {
        RaceProfile profile = raceManager.getProfile(player);
        if(profile == null) return;

        IRace model = raceManager.getRaceModel(profile.raceData.getRace());
        if(model instanceof be.lymaes.race.ability.Interactable interactable) {
            interactable.onInteract(e, player, profile.raceData);
        }
    }

    private void heldItemInteract(PlayerInteractEvent e, Player player) {
        ItemStack i = e.getItem();
        IRaceItem item = itemManager.getItem(i);
        if(item instanceof be.lymaes.race.item.Interactable interactable) {
            interactable.onInteract(e, player, i);
        }
    }

    @EventHandler
    public void onItemSwitch(PlayerItemHeldEvent e) {
        Player player = e.getPlayer();

        ItemStack prev = player.getInventory().getItem(e.getPreviousSlot());
        ItemStack current = player.getInventory().getItem(e.getNewSlot());

        heldModelItemSwitch(player, prev, current);
        heldItemItemSwitch(player, prev, current);
    }

    private void heldModelItemSwitch(Player player, ItemStack prev, ItemStack current) {
        RaceProfile profile = raceManager.getProfile(player);
        if(profile == null) return;

        IRace model = raceManager.getRaceModel(profile.raceData.getRace());
        if(model instanceof Helder helder) {
            helder.onSwitchOff(profile.raceData, prev);
            helder.onSwitchOn(profile.raceData, current);
        }
    }

    private void heldItemItemSwitch(Player player, ItemStack prev, ItemStack current) {
        IRaceItem prevItem = itemManager.getItem(prev);
        if(prevItem instanceof Heldable prevHeld) {
            prevHeld.onSwitchOff(player);
        }
        IRaceItem currentItem = itemManager.getItem(current);
        if(currentItem instanceof Heldable currentHeld) {
            currentHeld.onSwitchOn(player);
        }
    }

    @EventHandler
    public void onSwapHand(PlayerSwapHandItemsEvent e) {
        if(itemManager.getItem(e.getOffHandItem()) instanceof IStaticItem) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        if(itemManager.getItem(e.getItemDrop().getItemStack()) instanceof IStaticItem) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent e) {
        if(!(e.getEntity() instanceof Player player)) return;

        RaceProfile profile = raceManager.getProfile(player);
        if(profile == null) return;

        IRace model = raceManager.getRaceModel(profile.raceData.getRace());
        if(model instanceof Helder helder) {
            helder.onSwitchOn(profile.raceData, e.getItem().getItemStack());
        }
    }

}
