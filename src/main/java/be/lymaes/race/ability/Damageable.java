package be.lymaes.race.ability;

import be.lymaes.race.data.IRaceData;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

public interface Damageable {

    void onDefend(EntityDamageEvent e, Player player, IRaceData raceData);

}
