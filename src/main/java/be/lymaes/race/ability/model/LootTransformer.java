package be.lymaes.race.ability.model;

import be.lymaes.race.ability.Interact;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

public class LootTransformer implements Interact {

    private final Material[] list;

    public LootTransformer(Material[] list) {
        this.list = list;
    }

    public void onInteract(PlayerInteractEvent e, Player player, int rank) {
        ItemStack item = e.getItem();
        if (item == null || item.getType() != Material.EMERALD) return;

        if (e.getAction() != Action.RIGHT_CLICK_AIR) return;

        Material mineral = list[ThreadLocalRandom.current().nextInt(list.length)];

        int amount = item.getAmount();
        if (amount > 1) {
            item.setAmount(amount - 1);
        } else {
            player.getInventory().setItemInMainHand(null);
        }

        if(!player.getInventory().addItem(new ItemStack(mineral)).isEmpty()) {
            item.setAmount(amount);
            player.sendMessage(Color.RED + "Erreur : Il n'y a pas de place dans ton inventaire.");
        }

        e.setCancelled(true);
    }

}
