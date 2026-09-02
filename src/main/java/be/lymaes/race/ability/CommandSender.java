package be.lymaes.race.ability;

import org.bukkit.entity.Player;

public interface CommandSender extends Ability {

    void addPermission(Player player);
    void removePermission(Player player);

}
