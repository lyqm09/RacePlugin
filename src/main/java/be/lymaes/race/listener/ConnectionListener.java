package be.lymaes.race.listener;

import be.lymaes.race.Race;
import be.lymaes.race.manager.RaceManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectionListener implements Listener {

    private final RaceManager raceManager;

    public ConnectionListener(Race plugin) {
        this.raceManager = plugin.getRaceManager();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        raceManager.load(e.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        raceManager.unload(e.getPlayer());
    }

}
