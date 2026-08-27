package be.lymaes.race.item;

import be.lymaes.race.RaceProfile;
import be.lymaes.race.data.IRaceData;
import org.bukkit.entity.Player;

import java.util.List;

public interface Splashable {

    void onSplash(List<Player> players, List<IRaceData> data);

}
