package be.lymaes.race.ability.model;

import be.lymaes.race.RaceProfile;
import be.lymaes.race.ability.Ability;
import be.lymaes.race.ability.Damager;
import be.lymaes.race.data.IRaceData;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class AquaticStrength implements Damager {

    private final double[] factors;
    private final Class<? extends IRaceData> dataClass;

    public AquaticStrength(double[] factors, Class<? extends IRaceData> dataClass) {
        this.factors = factors;
        this.dataClass = dataClass;
    }

    public void onDamage(EntityDamageByEntityEvent e, Player damager, RaceProfile profile) {
        if(!damager.isInWater()) return;

        IRaceData data = profile.getRaceData(dataClass);
        if(data == null) return;

        int rank = data.getRank();
        if(rank < 0 || rank >= factors.length) return;
        double factor = factors[rank];

        e.setDamage(e.getFinalDamage() * (1.0 + factor));
    }

}
