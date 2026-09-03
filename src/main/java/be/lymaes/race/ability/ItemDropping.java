package be.lymaes.race.ability;

import org.bukkit.event.player.PlayerDropItemEvent;

public interface ItemDropping extends Ability {

    void onDrop(PlayerDropItemEvent e);

}
