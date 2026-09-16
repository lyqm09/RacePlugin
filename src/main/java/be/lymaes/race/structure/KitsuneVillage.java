package be.lymaes.race.structure;

import be.lymaes.race.util.SimpleBlockLocation;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class KitsuneVillage implements Structure {

    private static final StructureType STRUCT_TYPE = StructureType.KITSUNE_VILLAGE;
    private static final int RADIUS = 20;
    private static final int MAX_VILLAGERS = 20;
    private static final int MAX_FOXES = 20;

    public final UUID uuid;
    public final SimpleBlockLocation bell;

    @JsonCreator
    public KitsuneVillage(@JsonProperty("uuid") UUID uuid, @JsonProperty("bell") SimpleBlockLocation bell) {
        this.uuid = uuid;
        this.bell = bell;
    }

    public KitsuneVillage(Location bellLocation) {
        this(UUID.randomUUID(), new SimpleBlockLocation(bellLocation.getWorld().getUID(), bellLocation.getBlockX(), bellLocation.getBlockY(), bellLocation.getBlockZ()));
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public StructureType getType() {
        return STRUCT_TYPE;
    }

    private Collection<Entity> getEntities(EntityType type) {
        World world = Bukkit.getWorld(bell.worldUuid());
        if(world == null) return Collections.emptyList();

        Location center = new Location(world, bell.x(), bell.y(), bell.z());
        return world.getNearbyEntities(center, RADIUS, RADIUS, RADIUS, (entity) -> entity.getType() == type);
    }

    private int count(EntityType type) {
        World world = Bukkit.getWorld(bell.worldUuid());
        if(world == null) return -1;

        Location center = new Location(world, bell.x(), bell.y(), bell.z());
        return world.getNearbyEntities(center, RADIUS, RADIUS, RADIUS, (entity) -> entity.getType() == type).size();
    }

    public boolean canVillagerSpawn() {
        if(!hasBell()) return false;

        int amount = count(EntityType.VILLAGER);
        if(amount < 0) return false;

        return amount <= MAX_VILLAGERS;
    }

    public boolean canFoxSpawn() {
        if(!hasBell()) return false;

        int amount = count(EntityType.FOX);
        if(amount < 0) return false;

        return amount <= MAX_FOXES;
    }

    public boolean isInVillage(Location location) {
        World world = location.getWorld();
        if(world == null) return false;
        if(!world.getUID().equals(bell.worldUuid())) return false;

        Location center = new Location(world, bell.x(), bell.y(), bell.z());
        return center.distanceSquared(location) <= RADIUS * RADIUS;
    }

    private boolean hasBell() {
        World world = Bukkit.getWorld(bell.worldUuid());
        if(world == null) return false;

        Block block = world.getBlockAt(bell.x(), bell.y(), bell.z());
        return block.getType() == Material.BELL;
    }

    public boolean isVillageBell(Location location) {
        World world = location.getWorld();
        if(world == null) return false;

        return world.getUID().equals(bell.worldUuid())
                && location.getBlockX() == bell.x()
                && location.getBlockY() == bell.y()
                && location.getBlockZ() == bell.z();
    }

    public static List<Structure> load(ObjectMapper mapper, File file) throws IOException {
        Map<UUID, KitsuneVillage> map = mapper.readValue(file, new TypeReference<>() {});
        return new ArrayList<>(map.values());
    }

}
