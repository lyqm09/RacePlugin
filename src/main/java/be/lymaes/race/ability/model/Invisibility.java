package be.lymaes.race.ability.model;

import be.lymaes.race.ability.Sneaker;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Invisibility implements Sneaker {

    public void onToggleSneak(PlayerToggleSneakEvent e) {
        Player player = e.getPlayer();
        if(e.isSneaking()) {
            if(!player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, PotionEffect.INFINITE_DURATION, 0, true, false, true));
            }
        }
        else {
            PotionEffect effect = player.getPotionEffect(PotionEffectType.INVISIBILITY);
            if(effect != null && effect.isInfinite()) {
                player.removePotionEffect(PotionEffectType.INVISIBILITY);
            }
        }
    }

}
