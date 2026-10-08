package be.lymaes.race.ability;

import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;

public interface ProjectileLauncher extends Ability {

    void onLaunch(ProjectileLaunchEvent e);
    void onHit(ProjectileHitEvent e);

}
