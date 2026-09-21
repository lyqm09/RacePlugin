package be.lymaes.race.ability.model;

import be.lymaes.race.RaceProfile;
import be.lymaes.race.ability.Defender;
import be.lymaes.race.data.IRaceData;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageModifier implements Defender {

    private final double[] factors;
    private final Class<? extends IRaceData> dataClass;

    public DamageModifier(double[] factors, Class<? extends IRaceData> dataClass) {
        this.factors = factors;
        this.dataClass = dataClass;
    }

    public void onDefend(EntityDamageEvent e, RaceProfile profile) {
        if(!(e instanceof EntityDamageByEntityEvent e1)) return;

        IRaceData data = profile.getRaceData(dataClass);
        if(data == null) return;

        int rank = data.getRank() >= factors.length ? factors.length-1 : data.getRank();
        e1.setDamage(e.getFinalDamage() * (1.0 - factors[rank]));
    }

}
