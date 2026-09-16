package be.lymaes.race.ability.model;

import be.lymaes.race.Race;
import be.lymaes.race.RaceProfile;
import be.lymaes.race.ability.AbilityKey;
import be.lymaes.race.ability.BlockPlacer;
import be.lymaes.race.data.IRaceData;
import be.lymaes.race.data.KitsuneData;
import be.lymaes.race.item.model.VillageHeart;
import be.lymaes.race.manager.ItemManager;
import be.lymaes.race.manager.StructureManager;
import be.lymaes.race.structure.KitsuneVillage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.event.block.BlockPlaceEvent;

public class VillageFounder implements BlockPlacer {

    @Override
    public void onPlace(BlockPlaceEvent e, IRaceData data) {
        if(e.getBlockPlaced().getType() != Material.BELL) return;

        if(e.isCancelled() || !e.canBuild()) return;

        Race plugin = Race.getInstance();
        ItemManager itemManager = plugin.getItemManager();
        if(!(itemManager.getItem(e.getItemInHand()) instanceof VillageHeart)) return;

        Location location = e.getBlockPlaced().getLocation();
        StructureManager structureManager = plugin.getStructureManager();
        KitsuneVillage village = new KitsuneVillage(location);

        structureManager.addStructure(village);

        World world = location.getWorld();
        location = location.add(0,1,0);
        world.spawnEntity(location, EntityType.VILLAGER);
        world.spawnEntity(location, EntityType.VILLAGER);
        world.spawnEntity(location, EntityType.FOX);
        world.spawnEntity(location, EntityType.FOX);

        if(data instanceof KitsuneData kitsuneData) {
            kitsuneData.setVillage(village.getUuid());
            RaceProfile profile = plugin.getRaceManager().getProfile(e.getPlayer());
            profile.removeAbility(AbilityKey.VILLAGE_FOUNDER);
        }
    }
}
