package be.lymaes.race.listener;

import be.lymaes.race.Race;
import be.lymaes.race.RaceProfile;
import be.lymaes.race.manager.RaceManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class DeathListener implements Listener {

    private final RaceManager manager;

    public DeathListener(Race plugin) {
        this.manager = plugin.getRaceManager();
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        RaceProfile profile = manager.getProfile(e.getPlayer());

        manager.getRaceModel(profile.race).reloadEffect(profile);
    }

}
