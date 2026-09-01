package be.lymaes.race.ability.model;

import be.lymaes.race.RaceProfile;
import be.lymaes.race.ability.Ability;
import be.lymaes.race.ability.Taskable;
import be.lymaes.race.data.IRaceData;
import be.lymaes.race.data.KitsuneData;
import be.lymaes.race.data.OniData;
import be.lymaes.race.data.TamashiData;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Monophobia implements Taskable {

    private final double tolerance;

    public Monophobia(double tolerance) {
        this.tolerance = tolerance;
    }

    public void run(Player player, RaceProfile profile, IRaceData data, long currentTime) {
        TamashiData tamashiData = null;
        if(data instanceof TamashiData d) {
            tamashiData = d;
        }
        else {
            if(data instanceof OniData oniData && oniData.getOverlay() instanceof TamashiData d) {
                tamashiData = d;
            }
        }

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

        player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 2 * 20, 0, true, false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 2 * 20, 0, true, false, true));
    }

}
