package be.lymaes.race.ability.model;

import be.lymaes.race.RaceProfile;
import be.lymaes.race.ability.Taskable;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.Set;

public class Hydrophobia implements Taskable {

    private static final Set<Biome> NOT_RAINING_BIOMES = Set.of(
            Biome.DESERT,
            Biome.SAVANNA,
            Biome.SAVANNA_PLATEAU,
            Biome.WINDSWEPT_SAVANNA,
            Biome.BASALT_DELTAS,
            Biome.BADLANDS,
            Biome.ERODED_BADLANDS,
            Biome.WOODED_BADLANDS
    );

    private boolean isUnderRain(Player player) {
        World world = player.getWorld();
        if(!world.hasStorm()) return false;

        Location location = player.getLocation();
        Biome biome = world.getBiome(location);
        if(NOT_RAINING_BIOMES.contains(biome)) return false;

        int highestBlockY = world.getHighestBlockYAt(location);
        return highestBlockY < location.getBlockY();
    }

    public void run(Player player, RaceProfile profile, long currentTime) {
        if(player.isInWater() || isUnderRain(player)) {
            applyEffect(player, PotionEffectType.WEAKNESS, 2 * 20, 1);
            applyEffect(player, PotionEffectType.SLOWNESS, 2 * 20, 1);
            applyEffect(player, PotionEffectType.BLINDNESS, 2 * 20, 0);
        }
    }

}
