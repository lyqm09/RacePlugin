package be.lymaes.race.ability;

import be.lymaes.race.RaceProfile;
import org.bukkit.entity.Player;

public interface Taskable {

    void onTask(Player player, RaceProfile profile, long currentTime);

}
