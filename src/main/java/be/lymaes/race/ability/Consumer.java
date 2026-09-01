package be.lymaes.race.ability;

import org.bukkit.event.player.PlayerItemConsumeEvent;

public interface Consumer extends Ability {

    void onConsume(PlayerItemConsumeEvent e);

}
