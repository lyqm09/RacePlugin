package be.lymaes.race.ability;

import be.lymaes.race.data.IRaceData;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public interface SneakyCharacter {

    void onToggleSneak(PlayerToggleSneakEvent e, Player player, IRaceData raceData);

}
