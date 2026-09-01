package be.lymaes.race.ability.model;

import be.lymaes.race.ability.Ability;
import be.lymaes.race.ability.Defender;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class Absorption implements Defender {

    private final double[] factors;

    public Absorption(double[] factors) {
        this.factors = factors;
    }

    public void onDefend(EntityDamageEvent e, int rank) {
        if(!(e instanceof EntityDamageByEntityEvent e1)) return;
        if(rank < 0 || rank >= factors.length) return;

        e1.setDamage(e.getFinalDamage() * (1.0 - factors[rank]));
    }

}
