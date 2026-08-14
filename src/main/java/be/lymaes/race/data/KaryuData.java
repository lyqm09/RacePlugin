package be.lymaes.race.data;

import be.lymaes.race.model.RaceType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class KaryuData extends RaceData {

    private long villagerCMDTime;
    private long blessCMDTime;

    public KaryuData(int subrace, int rank, int exp, long villagerCMDTime, long blessCMDTime) {
        super(RaceType.KARYU, subrace, rank, exp);

        this.villagerCMDTime = villagerCMDTime;
        this.blessCMDTime = blessCMDTime;
    }

    public KaryuData(int subrace, int rank, int exp) {
        this(subrace, rank, exp, 0L, 0L);
    }

    @Override
    protected void saveSpecificData(ObjectNode node) {
        node.put("time_villager_cmd", villagerCMDTime);
        node.put("time_bless_cmd", blessCMDTime);
    }

    public static KaryuData loadProfileData(JsonNode rootNode, RaceType.PrimaryData primaryData) {
        RaceType race = RaceType.KITSUNE;

        if (rootNode.has(race.name())) {
            JsonNode raceNode = rootNode.get(race.name());

            RaceType.PrimaryData data = loadProfileData(raceNode, race, primaryData.subrace());

            long villagerCMDTime = raceNode.get("time_villager_cmd").asLong(0);
            long blessCMDTime = raceNode.get("time_bless_cmd").asLong(0);

            return new KaryuData(data.subrace(), data.rank(), data.exp(), villagerCMDTime, blessCMDTime);
        }

        return new KaryuData(Math.max(primaryData.subrace(), 0), primaryData.rank(), primaryData.exp());
    }

}
