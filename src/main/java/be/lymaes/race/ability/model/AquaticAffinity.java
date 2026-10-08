package be.lymaes.race.ability.model;

import be.lymaes.race.RaceProfile;
import be.lymaes.race.ability.Taskable;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import static be.lymaes.race.ability.model.Hydrophobia.isUnderRain;

public class AquaticAffinity implements Taskable {

    private static final int DURATION = 2 * 20;

    @Override
    public void run(Player player, RaceProfile profile, long currentTime) {
        if(player.isInWater() || isUnderRain(player)) {
            applyEffect(player, PotionEffectType.RESISTANCE, DURATION, 2);
            applyEffect(player, PotionEffectType.REGENERATION, DURATION, 0);
        }
    }

}
