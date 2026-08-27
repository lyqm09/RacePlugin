package be.lymaes.race.data;

import be.lymaes.race.model.RaceType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import static java.lang.System.currentTimeMillis;

public class KitsuneData extends RaceData {

    public static final RaceType RACE_TYPE = RaceType.KITSUNE;

    private long timeInForest;

    public KitsuneData(int rank, int exp, long timeInForest) {
        super(RACE_TYPE, -1, rank, exp);

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
        node.put("time_in_forest", System.currentTimeMillis() - timeInForest);
    }

    public static KitsuneData loadProfileData(JsonNode rootNode, RaceType.PrimaryData primaryData) {
        if (rootNode != null && rootNode.has(RACE_TYPE.name())) {
            JsonNode raceNode = rootNode.get(RACE_TYPE.name());

            RaceType.PrimaryData data = loadProfileData(raceNode, RACE_TYPE, -1);

            long time = raceNode.path("time_in_forest").asLong(0);
            long enterTime = time == 0 ? 0 : System.currentTimeMillis() - time;

            return new KitsuneData(data.rank(), data.exp(), enterTime);
        }

        return new KitsuneData(primaryData.rank(), primaryData.exp());
    }

}
