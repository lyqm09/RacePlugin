package be.lymaes.race.ability;

import org.bukkit.event.player.PlayerToggleSneakEvent;

public interface Sneaker extends Ability {

    void onToggleSneak(PlayerToggleSneakEvent e);

}
