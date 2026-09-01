package be.lymaes.race.ability.model;

import be.lymaes.race.RaceProfile;
import be.lymaes.race.ability.Ability;
import be.lymaes.race.ability.Taskable;
import be.lymaes.race.data.IRaceData;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
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

    public void run(Player player, RaceProfile profile, IRaceData data, long currentTime) {
        if(player.isInWater() || isUnderRain(player)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 2 * 20, 1, true, false, true));
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 2 * 20, 1, true, false, true));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 2 * 20, 1, true, false, true));
        }
    }

}
