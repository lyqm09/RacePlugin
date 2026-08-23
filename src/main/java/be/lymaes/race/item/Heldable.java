package be.lymaes.race.item;

import org.bukkit.entity.Player;

public interface Heldable {

    void onSwitchOn(Player player);
    void onSwitchOff(Player player);

}
