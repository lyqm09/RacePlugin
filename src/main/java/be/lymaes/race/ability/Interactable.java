package be.lymaes.race.ability;

import be.lymaes.race.data.IRaceData;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public interface Interactable {

    void onInteract(PlayerInteractEvent e, Player player, IRaceData raceData);

}
