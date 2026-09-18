package be.lymaes.race.ability;

import be.lymaes.race.RaceProfile;
import org.bukkit.event.entity.EntityDamageEvent;

public interface Defender extends Ability {

    void onDefend(EntityDamageEvent e, RaceProfile profile);

}
