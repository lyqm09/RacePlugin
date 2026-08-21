package be.lymaes.race.item;

import be.lymaes.race.RaceProfile;
import be.lymaes.race.model.IRace;
import org.bukkit.entity.Player;

public interface Consumable {

    void onConsume(Player player, RaceProfile profile, IRace model);

}
