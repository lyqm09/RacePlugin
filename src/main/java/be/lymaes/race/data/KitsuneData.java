package be.lymaes.race.data;

import be.lymaes.race.model.RaceType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class KitsuneData extends RaceData {

    private long timeInForest;

    public KitsuneData(int rank, int exp, long timeInForest) {
        super(RaceType.KITSUNE, -1, rank, exp);

        this.timeInForest = timeInForest;
    }

    public KitsuneData(int rank, int exp) {
        this(rank, exp, 0L);
    }

    @Override
    protected void saveSpecificData(ObjectNode node) {
        node.put("time_in_forest", timeInForest);
    }

    public static KitsuneData loadProfileData(JsonNode rootNode, int rank, int exp) {
        RaceType race = RaceType.KITSUNE;

        if (rootNode.has(race.name())) {
            JsonNode raceNode = rootNode.get(race.name());

            int[] data = loadProfileData(raceNode, race, -1);

            long time = raceNode.get("time_in_forest").asLong(0);

            return new KitsuneData(data[1], data[2], time);
        }

        return new KitsuneData(rank, exp);
    }

}
