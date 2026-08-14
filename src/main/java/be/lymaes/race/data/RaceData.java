package be.lymaes.race.data;

import be.lymaes.race.Race;
import be.lymaes.race.model.ISubRaceable;
import be.lymaes.race.model.RaceType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.HashMap;

public abstract class RaceData implements IRaceData {

    private final RaceType race;
    private final int subrace;

    private int rank;
    private int exp;

    public RaceData(RaceType race, int subrace, int rank, int exp) {
        this.race = race;
        this.subrace = subrace;
        this.rank = rank;
        this.exp = exp;
    }

    public RaceType getRace() {
        return race;
    }

    public int getSubrace() {
        return subrace;
    }

    public int getRank() {
        return rank;
    }

    public void rankUp() {
        rank++;
    }

    public int getExp() {
        return exp;
    }

    public void addExp(int n) {
        exp += n;
    }

    public void subExp(int n) {
        exp -= n;
    }

    // save and load

    public final void saveProfileData(ObjectNode rootNode) {
        ObjectNode node = rootNode.withObjectProperty(race.name());

        if(Race.getInstance().getRaceManager().getRaceModel(race) instanceof ISubRaceable) {
            node.put("subrace", subrace);
            node = node.withObjectProperty(Integer.toString(subrace));
        }

        node.put("exp", exp);
        node.put("rank", rank);

        saveSpecificData(node);
    }

    protected abstract void saveSpecificData(ObjectNode node);

    protected static RaceType.PrimaryData loadProfileData(JsonNode raceNode, RaceType race, int subrace) {

        int sub = -1;
        int rank = 0;
        int exp = 0;

        if(Race.getInstance().getRaceManager().getRaceModel(race) instanceof ISubRaceable) {
            sub = subrace < 0 ? raceNode.path("subrace").asInt(0) : subrace;

            JsonNode subraceNode = raceNode.get(Integer.toString(sub));
            if(subraceNode != null) {
                rank = subraceNode.path("rank").asInt(0);
                exp = subraceNode.path("exp").asInt(0);
            }
        }
        else {
            rank = raceNode.path("rank").asInt(0);
            exp = raceNode.path("exp").asInt(0);
        }

        return new RaceType.PrimaryData(sub, rank, exp);
    }

}
