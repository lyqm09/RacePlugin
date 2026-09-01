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

public class TamashiExp implements Taskable {

    private final double tolerance;

    public TamashiExp(double tolerance) {
        this.tolerance = tolerance;
    }

    public void run(Player player, RaceProfile profile, IRaceData data, long currentTime) {
        if((currentTime / 1000) % 60 != 0) return;

        if(!(data instanceof TamashiData tamashiData))  return; // TODO remove from list

        Location playerLoc = player.getLocation();
        Location home = tamashiData.getHome();

        int exp = 0;

        if(home != null && home.getWorld() != null) {

            // cas 1:
            if(home.getWorld() == player.getWorld()) {
                if(playerLoc.distanceSquared(home) <= tolerance) {
                    exp++;
                }

                for(Player other : player.getWorld().getPlayers()) {
                    if(other.equals(player)) continue;

                    Location otherLoc = other.getLocation();
                    if (otherLoc.distanceSquared(playerLoc) <= tolerance) {
                        exp++;
                    }
                    if(otherLoc.distanceSquared(home) <= tolerance) {
                        exp++;
                    }
                }
            }
            // cas 2:
            else {
                for(Player other : player.getWorld().getPlayers()) {
                    if(other.equals(player)) continue;

                    if(other.getLocation().distanceSquared(playerLoc) <= tolerance) {
                        exp++;
                    }
                }

                for(Player other : home.getWorld().getPlayers()) {
                    if(other.getLocation().distanceSquared(home) <= tolerance) {
                        exp++;
                    }
                }
            }
        }
        // cas 3:
        else {
            for(Player other : player.getWorld().getPlayers()) {
                if(other.equals(player)) continue;

                if(other.getLocation().distanceSquared(playerLoc) <= tolerance) {
                    exp++;
                }
            }
        }

        if(exp > 0) {
            profile.addExp(exp);
        }
    }

}
