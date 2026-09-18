package be.lymaes.race.ability.model;

import be.lymaes.race.RaceProfile;
import be.lymaes.race.ability.Defender;
import org.bukkit.event.entity.EntityDamageEvent;

public class FeatherFall implements Defender {

    public void onDefend(EntityDamageEvent e, RaceProfile profile) {
        if(e.getCause() != EntityDamageEvent.DamageCause.FALL) return;

        e.setCancelled(true);
    }

}
