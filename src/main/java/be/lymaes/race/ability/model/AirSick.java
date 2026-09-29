package be.lymaes.race.ability.model;

import be.lymaes.race.ability.Fly;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class AirSick implements Fly {

    private static final PotionEffectType EFFECT_TYPE = PotionEffectType.HUNGER;
    private static final int AMPLIFIER = 3; // lvl 4

    @Override
    public void onToggleFly(PlayerToggleFlightEvent e) {
        Player player = e.getPlayer();

        GameMode gameMode = player.getGameMode();
        if(gameMode == GameMode.CREATIVE || gameMode == GameMode.SPECTATOR) return;

        if(e.isFlying()) {
            onFly(player);
        } else {
            offFly(player);
        }
    }

    public void onFly(Player player) {
        PotionEffect effect = player.getPotionEffect(EFFECT_TYPE);
        if(effect != null && effect.getAmplifier() < AMPLIFIER) {
            player.removePotionEffect(EFFECT_TYPE);
        }
        player.addPotionEffect(new PotionEffect(EFFECT_TYPE, PotionEffect.INFINITE_DURATION, AMPLIFIER, false, false, false));
    }

    public void offFly(Player player) {
        PotionEffect effect = player.getPotionEffect(EFFECT_TYPE);
        if(effect != null && effect.getAmplifier() == AMPLIFIER && effect.isInfinite()) {
            player.removePotionEffect(EFFECT_TYPE);
        }
    }
}
