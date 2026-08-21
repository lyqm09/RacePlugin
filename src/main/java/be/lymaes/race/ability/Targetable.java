package be.lymaes.race.ability;

import be.lymaes.race.data.IRaceData;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

public interface Targetable {

    void onTarget(EntityTargetLivingEntityEvent e, Player player, IRaceData raceData);

}
