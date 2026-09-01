package be.lymaes.race.ability.model;

import be.lymaes.race.RaceProfile;
import be.lymaes.race.ability.Ability;
import be.lymaes.race.ability.Killer;
import org.bukkit.entity.Mob;
import org.bukkit.event.entity.EntityDeathEvent;

public class OniExp implements Killer {

    public void onKill(EntityDeathEvent e, RaceProfile profile) {
        if(!(e.getEntity() instanceof Mob)) return;

        profile.addExp(1);
    }

}
