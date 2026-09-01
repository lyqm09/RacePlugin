package be.lymaes.race.ability;

import be.lymaes.race.RaceProfile;
import be.lymaes.race.data.IRaceData;
import org.bukkit.entity.Player;

public interface Taskable extends Ability {

    void run(Player player, RaceProfile profile, IRaceData data, long currentTime);

}
