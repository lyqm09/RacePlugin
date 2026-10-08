package be.lymaes.race.ability.model;

import be.lymaes.race.ability.ProjectileDamage;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;

public class HardSnowball implements ProjectileDamage {

    @Override
    public void onDamage(EntityDamageByEntityEvent e, Projectile projectile, ProjectileSource shooter) {
        if(!(projectile instanceof Snowball)) return;

        e.setDamage(2.0);
    }

}
