package be.lymaes.race.ability;

import be.lymaes.race.RaceProfile;
import be.lymaes.race.data.IRaceData;
import org.bukkit.event.block.BlockBreakEvent;

public interface BlockBreaker extends Ability {

    void onBreak(BlockBreakEvent e, RaceProfile profile);

}
