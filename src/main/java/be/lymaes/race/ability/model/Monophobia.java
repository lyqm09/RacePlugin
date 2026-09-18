package be.lymaes.race.ability.model;

import be.lymaes.race.RaceProfile;
import be.lymaes.race.ability.Taskable;
import be.lymaes.race.data.IRaceData;
import be.lymaes.race.data.TamashiData;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class Monophobia implements Taskable {

    private final double tolerance;

    public Monophobia(double tolerance) {
        this.tolerance = tolerance;
    }

    public void run(Player player, RaceProfile profile, long currentTime) {
        TamashiData tamashiData = profile.getRaceData(TamashiData.class);

        Location playerLoc = player.getLocation();
        Location home = tamashiData != null ? tamashiData.getHome() : player.getRespawnLocation();

        if(home != null && home.getWorld() != null) {
            if(home.getWorld() == player.getWorld()) {
                if(playerLoc.distanceSquared(home) <= tolerance) return;
            }
        }

        for(Player other : player.getWorld().getPlayers()) {
            if(other.equals(player)) continue;
            if(other.getLocation().distanceSquared(playerLoc) <= tolerance) return;
        }

        applyEffect(player, PotionEffectType.HUNGER, 2 * 20, 0);
        applyEffect(player, PotionEffectType.WEAKNESS, 2 * 20, 0);
    }

}
