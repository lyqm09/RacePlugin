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

    public long getTimeInForest() {
        return timeInForest;
    }

    public void setTimeInForest(long time) {
        timeInForest = time;
    }

    @Override
    protected void saveSpecificData(ObjectNode node) {
        node.put("time_in_forest", timeInForest);
    }

    public static KitsuneData loadProfileData(JsonNode rootNode, RaceType.PrimaryData primaryData) {
        RaceType race = RaceType.KITSUNE;

        if (rootNode != null && rootNode.has(race.name())) {
            JsonNode raceNode = rootNode.get(race.name());

            RaceType.PrimaryData data = loadProfileData(raceNode, race, -1);

            long time = raceNode.path("time_in_forest").asLong(0);

            return new KitsuneData(data.rank(), data.exp(), time);
        }

        return new KitsuneData(primaryData.rank(), primaryData.exp());
    }

}
