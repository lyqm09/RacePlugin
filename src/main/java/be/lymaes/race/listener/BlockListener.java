package be.lymaes.race.listener;

import be.lymaes.race.Race;
import be.lymaes.race.RaceProfile;
import be.lymaes.race.ability.AbilityType;
import be.lymaes.race.ability.BlockBreaker;
import be.lymaes.race.ability.BlockPlacer;
import be.lymaes.race.data.IRaceData;
import be.lymaes.race.data.KitsuneData;
import be.lymaes.race.manager.RaceManager;
import be.lymaes.race.manager.StructureManager;
import be.lymaes.race.structure.KitsuneVillage;
import be.lymaes.race.structure.StructureType;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.List;
import java.util.Set;

public class BlockListener implements Listener {

    private final RaceManager raceManager;
    private final StructureManager structureManager;

    public BlockListener(Race plugin) {
        this.raceManager = plugin.getRaceManager();
        this.structureManager = plugin.getStructureManager();
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        RaceProfile profile = raceManager.getProfile(e.getPlayer());
        if(profile == null) return;

        // Kitsune Villages
        if(e.getBlock().getType() == Material.BELL) {
            List<KitsuneVillage> kitsuneVillages = structureManager.getStructures(StructureType.KITSUNE_VILLAGE);
            for (KitsuneVillage village : kitsuneVillages) {
                if (!village.isVillageBell(e.getBlock().getLocation())) continue;

                KitsuneData kitsuneData = profile.getRaceData(KitsuneData.class);
                if(kitsuneData != null && kitsuneData.hasVillage()) {
                    if(village.getUuid().equals(kitsuneData.getVillageUuid())) { // owner
                        structureManager.removeStructure(village);
                        kitsuneData.setVillage(null);
                        break;
                    }
                }

                e.setCancelled(true);
                break;
            }
        }

        // Abilities
        Set<BlockBreaker> abilities = profile.getEventAbilities(AbilityType.BLOCK_BREAKER);
        for(BlockBreaker breaker : abilities) {
            breaker.onBreak(e, profile);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        // Abilities
        RaceProfile profile = raceManager.getProfile(e.getPlayer());
        if(profile == null) return;

        Set<BlockPlacer> abilities = profile.getEventAbilities(AbilityType.BLOCK_PLACER);
        for(BlockPlacer placer : abilities) {
            placer.onPlace(e, profile);
        }
    }

}
