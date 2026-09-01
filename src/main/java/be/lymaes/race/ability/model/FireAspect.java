package be.lymaes.race.ability.model;

import be.lymaes.race.ability.Ability;
import be.lymaes.race.ability.Damager;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class FireAspect implements Damager {

    private final int[] times;

    public FireAspect(int[] times) {
        this.times = times;
    }

    public void onDamage(EntityDamageByEntityEvent e, Player damager, int rank) {
        if(rank < 0 || rank >= times.length) return;

        e.getEntity().setFireTicks(times[rank] * 20);
    }

}
