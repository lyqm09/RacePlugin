package be.lymaes.race.ability.model;

import be.lymaes.race.RaceProfile;
import be.lymaes.race.ability.Ability;
import be.lymaes.race.ability.Defender;
import be.lymaes.race.data.IRaceData;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class Absorption implements Defender {

    private final double[] factors;
    private final Class<? extends IRaceData> dataClass;

    public Absorption(double[] factors, Class<? extends IRaceData> dataClass) {
        this.factors = factors;
        this.dataClass = dataClass;
    }

    public void onDefend(EntityDamageEvent e, RaceProfile profile) {
        if(!(e instanceof EntityDamageByEntityEvent e1)) return;

        IRaceData data = profile.getRaceData(dataClass);
        if(data == null) return;

        int rank = data.getRank();
        if(rank < 0 || rank >= factors.length) return;

        e1.setDamage(e.getFinalDamage() * (1.0 - factors[rank]));
    }

}
