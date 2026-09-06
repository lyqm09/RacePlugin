package be.lymaes.race.listener;

import be.lymaes.race.Race;
import be.lymaes.race.RaceProfile;
import be.lymaes.race.ability.AbilityType;
import be.lymaes.race.ability.BlockBreaker;
import be.lymaes.race.manager.RaceManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Set;

public class BlockListener implements Listener {

    private final RaceManager raceManager;

    public BlockListener(Race plugin) {
        this.raceManager = plugin.getRaceManager();
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        RaceProfile profile = raceManager.getProfile(e.getPlayer());
        if(profile == null) return;

        Set<BlockBreaker> abilities = profile.getEventAbilities(AbilityType.BLOCK_BREAKER);
        for(BlockBreaker breaker : abilities) {
            breaker.onBreak(e, profile.raceData);
        }
    }

}
