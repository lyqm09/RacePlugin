package be.lymaes.race.ability;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public interface Damager extends Ability {

    void onDamage(EntityDamageByEntityEvent e, Player damager, int rank);

}
