package be.lymaes.race.listener;

import be.lymaes.race.Race;
import be.lymaes.race.RaceProfile;
import be.lymaes.race.ability.AbilityType;
import be.lymaes.race.ability.ProjectileLauncher;
import be.lymaes.race.manager.RaceManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;

import java.util.Set;

public class ProjectileListener implements Listener {

    private final RaceManager raceManager;

    public ProjectileListener(Race plugin) {
        this.raceManager = plugin.getRaceManager();
    }

    @EventHandler
    public void onLaunch(ProjectileLaunchEvent e) {
        if(e.getEntity().getShooter() instanceof Player player) {

            RaceProfile profile = raceManager.getProfile(player);
            if (profile == null) return;

            Set<ProjectileLauncher> abilities = profile.getEventAbilities(AbilityType.PROJECTILE_LAUNCHER);
            for (ProjectileLauncher launcher : abilities) {
                launcher.onLaunch(e);
            }

        }
    }

    @EventHandler
    public void onHit(ProjectileHitEvent e) {
        if(e.getEntity().getShooter() instanceof Player player) {

            RaceProfile profile = raceManager.getProfile(player);
            if (profile == null) return;

            Set<ProjectileLauncher> abilities = profile.getEventAbilities(AbilityType.PROJECTILE_LAUNCHER);
            for (ProjectileLauncher launcher : abilities) {
                launcher.onHit(e);
            }

        }
    }

}
