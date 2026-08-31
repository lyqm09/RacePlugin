package be.lymaes.race.listener;

import be.lymaes.race.Race;
import be.lymaes.race.RaceProfile;
import be.lymaes.race.manager.RaceManager;
import be.lymaes.race.model.IRace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class SneakListener implements Listener {

    private final RaceManager raceManager;

    public SneakListener(Race plugin) {
        this.raceManager = plugin.getRaceManager();
    }

    @EventHandler
    public void onToggleSneak(PlayerToggleSneakEvent e) {
        Player player = e.getPlayer();

        RaceProfile profile = raceManager.getProfile(player);
        if(profile == null) return;

        IRace model = raceManager.getRaceModel(profile.raceData.getRace());
        if(!(model instanceof SneakyCharacter sneakyCharacter)) return;

        sneakyCharacter.onToggleSneak(e, player, profile.raceData);
    }

}
