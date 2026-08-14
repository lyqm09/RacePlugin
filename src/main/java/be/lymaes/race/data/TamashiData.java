package be.lymaes.race.data;

import be.lymaes.race.Race;
import be.lymaes.race.model.RaceType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class TamashiData extends RaceData {

    private record SimpleLocation(String world, double x, double y, double z) {}

    private Location home;

    public TamashiData(int subrace, int rank, int exp, Location home) {
        super(RaceType.TAMASHI, subrace, rank, exp);

        this.home = home;
    }

    public TamashiData(int subrace, int rank, int exp) {
        this(subrace, rank, exp, null);
    }

    public void setHome(Location newHome) {
        this.home = newHome;
    }

    public Location getHome() {
        if(home == null) {
            return Bukkit.getWorlds().getFirst().getSpawnLocation();
        }
        return home;
    }

    @Override
    protected void saveSpecificData(ObjectNode node) {
        if(home != null && home.getWorld() != null) {
            SimpleLocation simpleLocation = new SimpleLocation(home.getWorld().getName(), home.getX(), home.getY(), home.getZ());

            try {
                node.put("home", Race.MAPPER.writeValueAsString(simpleLocation));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static TamashiData loadProfileData(JsonNode rootNode, RaceType.PrimaryData primaryData) {
        RaceType race = RaceType.KITSUNE;

        if (rootNode != null && rootNode.has(race.name())) {
            JsonNode raceNode = rootNode.get(race.name());

            RaceType.PrimaryData data = loadProfileData(raceNode, race, primaryData.subrace());

            Location home = null;
            String stringLocation = raceNode.get("home").asText();

            if(stringLocation != null && !stringLocation.isEmpty()) {
                try {
                    SimpleLocation simpleLocation = Race.MAPPER.readValue(stringLocation, SimpleLocation.class);
                    home = new Location(Bukkit.getWorld(simpleLocation.world()), simpleLocation.x(), simpleLocation.y(), simpleLocation.z());
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }

            return new TamashiData(data.subrace(), data.rank(), data.exp(), home);
        }

        return new TamashiData(Math.max(primaryData.subrace(), 0), primaryData.rank(), primaryData.exp());
    }

}
