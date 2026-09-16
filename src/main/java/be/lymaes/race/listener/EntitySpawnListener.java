package be.lymaes.race.listener;

import be.lymaes.race.Race;
import be.lymaes.race.manager.StructureManager;
import be.lymaes.race.structure.KitsuneVillage;
import be.lymaes.race.structure.StructureType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.List;

public class EntitySpawnListener implements Listener {

    private final StructureManager structureManager;

    public EntitySpawnListener(Race plugin) {
        this.structureManager = plugin.getStructureManager();
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent e) {
        EntityType type = e.getEntityType();
        if(type != EntityType.VILLAGER && type != EntityType.FOX) return;

        KitsuneVillage kitsuneVillage = null;

        List<KitsuneVillage> villages = structureManager.getStructures(StructureType.KITSUNE_VILLAGE);
        for(KitsuneVillage village : villages) {
            if (village.isInVillage(e.getLocation())) {
                kitsuneVillage = village;
                break;
            }
        }

        if(kitsuneVillage == null) return;

        if(e.getEntity() instanceof Villager) {
            if(!kitsuneVillage.canVillagerSpawn()) e.setCancelled(true);
        }
        else if(e.getEntity() instanceof Fox) {
            if(!kitsuneVillage.canFoxSpawn()) e.setCancelled(true);
        }
    }

}
