package be.lymaes.race.ability.model;

import be.lymaes.race.Race;
import be.lymaes.race.ability.Ability;
import be.lymaes.race.ability.Interact;
import be.lymaes.race.item.model.FlyChargeBall;
import be.lymaes.race.manager.ItemManager;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class FlyCharge implements Interact {

    private final int[] times;

    public FlyCharge(int[] times) {
        this.times = times;
    }

    public void onInteract(PlayerInteractEvent e, Player player, int rank) {
        ItemStack item = e.getItem();
        ItemManager itemManager = Race.getInstance().getItemManager();
        if(!(itemManager.getItem(item) instanceof FlyChargeBall)) return;

        e.setCancelled(true);

        if(e.getAction() != Action.RIGHT_CLICK_AIR) return;

        if(rank < 0 || rank >= times.length) return;
        int time = times[rank];

        if(!player.hasCooldown(item)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, time * 20, 0, true, false, true));
            player.setCooldown(item, (time + 10) * 20);
        }
        else if(player.hasPotionEffect(PotionEffectType.LEVITATION)) {
            player.removePotionEffect(PotionEffectType.LEVITATION);
            player.setCooldown(item, 10 * 20);
        }
    }

}
