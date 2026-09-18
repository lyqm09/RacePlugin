package be.lymaes.race.ability;

import be.lymaes.race.RaceProfile;
import be.lymaes.race.data.IRaceData;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public interface Taskable extends Ability {

    void run(Player player, RaceProfile profile, long currentTime);
    default void terminate() {}

    default void applyEffect(Player player, PotionEffectType type, int duration, int amplifier) {
        PotionEffect effect = player.getPotionEffect(type);
        if(effect != null) {
            if (effect.isInfinite() || effect.getDuration() > duration) return;
        }

        player.addPotionEffect(new PotionEffect(type, duration, amplifier, true, false, true));
    }

}
