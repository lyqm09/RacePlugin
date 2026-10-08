package be.lymaes.race.ability;

import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;

public interface ProjectileDamage extends Ability {

    void onDamage(EntityDamageByEntityEvent e, Projectile projectile, ProjectileSource shooter);

}
