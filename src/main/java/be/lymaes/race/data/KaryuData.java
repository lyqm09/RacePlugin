package be.lymaes.race.data;

import be.lymaes.race.model.RaceType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class KaryuData extends RaceData {

    public static final RaceType RACE_TYPE = RaceType.KARYU;

    private long villagerCMDTime;
    private long blessCMDTime;

    public KaryuData(int subrace, int rank, int exp, long villagerCMDTime, long blessCMDTime) {
        super(RACE_TYPE, subrace, rank, exp);

        this.villagerCMDTime = villagerCMDTime;
        this.blessCMDTime = blessCMDTime;
    }

    public KaryuData(int subrace, int rank, int exp) {
        this(subrace, rank, exp, 0L, 0L);
    }

    public long getVillagerCMDTime() {
        return villagerCMDTime;
    }

    public void setVillagerCMDTime(long time) {
        this.villagerCMDTime = time;
    }

    public long getBlessCMDTime() {
        return blessCMDTime;
    }

    public void setBlessCMDTime(long time) {
        this.blessCMDTime = time;
    }

    @Override
    protected void saveSpecificData(ObjectNode node) {
        node.put("time_villager_cmd", villagerCMDTime);
        node.put("time_bless_cmd", blessCMDTime);
    }

    public static KaryuData loadProfileData(JsonNode rootNode, RaceType.PrimaryData primaryData) {
        if (rootNode != null && rootNode.has(RACE_TYPE.name())) {
            JsonNode raceNode = rootNode.get(RACE_TYPE.name());

            RaceType.PrimaryData data = loadProfileData(raceNode, RACE_TYPE, primaryData.subrace());

            long villagerCMDTime = raceNode.path("time_villager_cmd").asLong(0);
            long blessCMDTime = raceNode.path("time_bless_cmd").asLong(0);

            return new KaryuData(data.subrace(), data.rank(), data.exp(), villagerCMDTime, blessCMDTime);
        }

        return new KaryuData(Math.max(primaryData.subrace(), 0), primaryData.rank(), primaryData.exp());
    }

}
