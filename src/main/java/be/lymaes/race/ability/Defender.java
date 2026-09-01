package be.lymaes.race.ability;

import org.bukkit.event.entity.EntityDamageEvent;

public interface Defender extends Ability {

    void onDefend(EntityDamageEvent e, int rank);

}
