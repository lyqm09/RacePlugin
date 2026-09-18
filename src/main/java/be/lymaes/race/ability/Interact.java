package be.lymaes.race.ability;

import be.lymaes.race.RaceProfile;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public interface Interact extends Ability {

    void onInteract(PlayerInteractEvent e, Player player, RaceProfile profile);

}
