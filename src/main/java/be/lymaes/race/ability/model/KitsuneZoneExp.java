package be.lymaes.race.ability.model;

import be.lymaes.race.Race;
import be.lymaes.race.RaceProfile;
import be.lymaes.race.ability.Ability;
import be.lymaes.race.ability.Taskable;
import be.lymaes.race.data.IRaceData;
import be.lymaes.race.data.KitsuneData;
import be.lymaes.race.data.OniData;
import be.lymaes.race.model.IRace;
import be.lymaes.race.model.Kitsune;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class KitsuneZoneExp implements Taskable {

    private static final Set<Biome> AVAILABLE_BIOMES = Set.of(
            Biome.JUNGLE,
            Biome.SPARSE_JUNGLE,
            Biome.BAMBOO_JUNGLE,
            Biome.BIRCH_FOREST,
            Biome.OLD_GROWTH_BIRCH_FOREST,
            Biome.DARK_FOREST,
            Biome.FOREST,
            Biome.FLOWER_FOREST,
            Biome.CHERRY_GROVE
    );

    public void run(Player player, RaceProfile profile, IRaceData data, long currentTime) {
        IRace<? extends IRaceData> irace = Race.getInstance().getRaceManager().getRaceModel(data.getRace());
        if(!(irace instanceof Kitsune kitsune)) return;
        if(!(data instanceof KitsuneData kitsuneData)) return; // TODO remove from list

        long time = kitsuneData.getTimeInForest();

        Biome currentBiome = player.getWorld().getBiome(player.getLocation());
        if(AVAILABLE_BIOMES.contains(currentBiome)) {
            if (time > 0 && currentTime - time >= 1000 * 60 * 60) {
                if (ThreadLocalRandom.current().nextDouble() <= Kitsune.TOLERENCE) {
                    if(kitsune.getExpRequired(kitsuneData.getRank()+1) != -1) {
                        profile.rankUp();
                    }
                }
                kitsuneData.setTimeInForest(currentTime);
            }
            else if(time <= 0) {
                kitsuneData.setTimeInForest(currentTime);
            }
        }
        else {
            if(time > 0) {
                kitsuneData.setTimeInForest(0L);
            }
        }
    }

}
