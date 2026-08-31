package be.lymaes.race.listener;

import be.lymaes.race.Race;
import be.lymaes.race.RaceProfile;
import be.lymaes.race.manager.RaceManager;
import be.lymaes.race.model.IRace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.raid.RaidFinishEvent;

public class RaidListener implements Listener {

    private final RaceManager raceManager;

    public RaidListener(Race plugin) {
        this.raceManager = plugin.getRaceManager();
    }

    @EventHandler
    public void onRaidFinish(RaidFinishEvent e) {
        for(Player player : e.getWinners()) {

            RaceProfile profile = raceManager.getProfile(player);
            if(profile == null) continue;

            IRace model = raceManager.getRaceModel(profile.raceData.getRace());
            if(!(model instanceof RaidFinisher raidFinisher)) continue;

            raidFinisher.onRaidFinish(profile);
        }
    }

}
