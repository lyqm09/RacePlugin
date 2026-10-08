package be.lymaes.race.ability.model;

import be.lymaes.race.RaceProfile;
import be.lymaes.race.ability.Taskable;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class FrostAffinity implements Taskable {

    @Override
    public void run(Player player, RaceProfile profile, long currentTime) {
        double temperature = player.getLocation().getBlock().getTemperature();
        if(temperature <= 0.0) {
            applyEffect(player, PotionEffectType.SPEED, 2 * 20, 0);
            applyEffect(player, PotionEffectType.JUMP_BOOST, 2 * 20, 0);
        } else
        if(temperature >= 2.0) {
            applyEffect(player, PotionEffectType.WEAKNESS, 2 * 20, 0);
            applyEffect(player, PotionEffectType.SLOWNESS, 2 * 20, 0);
        }
    }

}
