package be.lymaes.race.ability;

import org.bukkit.event.player.PlayerToggleFlightEvent;

public interface Fly extends Ability {

    public void onToggleFly(PlayerToggleFlightEvent e);

}
