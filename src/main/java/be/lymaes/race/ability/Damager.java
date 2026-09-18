package be.lymaes.race.ability;

import be.lymaes.race.RaceProfile;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public interface Damager extends Ability {

    void onDamage(EntityDamageByEntityEvent e, Player damager, RaceProfile profile);

}
