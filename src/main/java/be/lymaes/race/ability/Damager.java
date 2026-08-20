package be.lymaes.race.ability;

import be.lymaes.race.data.IRaceData;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public interface Damager {

    void onAttack(EntityDamageByEntityEvent e, Player player, IRaceData raceData);

}
