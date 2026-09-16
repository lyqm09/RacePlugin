package be.lymaes.race.ability;

import be.lymaes.race.data.IRaceData;
import org.bukkit.event.block.BlockPlaceEvent;

public interface BlockPlacer extends Ability {

    void onPlace(BlockPlaceEvent e, IRaceData data);
}
