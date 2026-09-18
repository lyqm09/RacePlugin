package be.lymaes.race.ability.model;

import be.lymaes.race.RaceProfile;
import be.lymaes.race.ability.Damager;
import be.lymaes.race.data.IRaceData;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class FireAspect implements Damager {

    private final int[] times;
    private final Class<? extends IRaceData> dataClass;

    public FireAspect(int[] times, Class<? extends IRaceData> dataClass) {
        this.times = times;
        this.dataClass = dataClass;
    }

    public void onDamage(EntityDamageByEntityEvent e, Player damager, RaceProfile profile) {
        IRaceData data = profile.getRaceData(dataClass);
        if(data == null) return;

        int rank = data.getRank();
        if(rank < 0 || rank >= times.length) return;

        e.getEntity().setFireTicks(times[rank] * 20);
    }

}
