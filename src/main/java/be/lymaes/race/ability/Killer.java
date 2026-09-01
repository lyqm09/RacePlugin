package be.lymaes.race.ability;

import be.lymaes.race.RaceProfile;
import org.bukkit.event.entity.EntityDeathEvent;

public interface Killer extends Ability {

    void onKill(EntityDeathEvent e, RaceProfile profile);

}
