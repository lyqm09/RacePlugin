package be.lymaes.race.ability.model;

import be.lymaes.race.ability.Ability;
import be.lymaes.race.ability.Damager;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class AquaticStrength implements Damager {

    private final double[] factors;

    public AquaticStrength(double[] factors) {
        this.factors = factors;
    }

    public void onDamage(EntityDamageByEntityEvent e, Player damager, int rank) {
        if(!damager.isInWater()) return;

        if(rank < 0 || rank >= factors.length) return;
        double factor = factors[rank];

        e.setDamage(e.getFinalDamage() * (1.0 + factor));
    }

}
