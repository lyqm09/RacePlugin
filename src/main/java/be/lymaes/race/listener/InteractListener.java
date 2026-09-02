package be.lymaes.race.listener;

import be.lymaes.race.Race;
import be.lymaes.race.RaceProfile;
import be.lymaes.race.ability.AbilityType;
import be.lymaes.race.ability.Helder;
import be.lymaes.race.ability.Interact;
import be.lymaes.race.item.IRaceItem;
import be.lymaes.race.item.IStaticItem;
import be.lymaes.race.manager.ItemManager;
import be.lymaes.race.manager.RaceManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

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

        Set<Interact> abilities = profile.getEventAbilities(AbilityType.INTERACT);
        for(Interact interact : abilities) {
            interact.onInteract(e, player, profile.raceData.getRank());
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
    }

    private void heldModelItemSwitch(Player player, ItemStack prev, ItemStack current) {
        RaceProfile profile = raceManager.getProfile(player);
        if(profile == null) return;

        Set<Helder> abilities = profile.getEventAbilities(AbilityType.HELDER);
        for(Helder helder : abilities) {
            helder.onSwapOff(prev);
            helder.onSwapOn(current);
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
        RaceProfile profile = raceManager.getProfile(e.getPlayer());
        if(profile == null) return;

        Set<Helder> abilities = profile.getEventAbilities(AbilityType.HELDER);
        for(Helder helder : abilities) {
            helder.onSwapOff(e.getItemDrop().getItemStack());
        }

        if(itemManager.getItem(e.getItemDrop().getItemStack()) instanceof IStaticItem) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent e) {
        if(!(e.getEntity() instanceof Player player)) return;

        RaceProfile profile = raceManager.getProfile(player);
        if(profile == null) return;

        Set<Helder> abilities = profile.getEventAbilities(AbilityType.HELDER);
        for(Helder helder : abilities) {
            helder.onPickup(player.getInventory().getItemInMainHand(), e.getItem().getItemStack());
        }
    }

}
