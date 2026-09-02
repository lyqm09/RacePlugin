package be.lymaes.race.listener;

import be.lymaes.race.Race;
import be.lymaes.race.RaceProfile;
import be.lymaes.race.ability.AbilityType;
import be.lymaes.race.ability.RaidWinner;
import be.lymaes.race.manager.RaceManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.raid.RaidFinishEvent;

import java.util.Set;

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

            Set<RaidWinner> abilities = profile.getEventAbilities(AbilityType.RAID_WINNER);
            for(RaidWinner winner : abilities) {
                winner.onRaidFinish(profile);
            }
        }
    }

}
