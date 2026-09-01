package be.lymaes.race.ability;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public interface Interact extends Ability {

    void onInteract(PlayerInteractEvent e, Player player, int rank);

}
