package be.lymaes.race.ability;

import be.lymaes.race.data.IRaceData;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public interface Consumer {

    void onConsume(PlayerItemConsumeEvent e, Player player, IRaceData raceData);

}
