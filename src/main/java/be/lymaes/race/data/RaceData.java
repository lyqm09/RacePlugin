package be.lymaes.race.data;

import be.lymaes.race.model.RaceType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

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

    public final void updateProfileData(ObjectNode rootNode) {
        ObjectNode raceNode = rootNode.withObjectProperty(race.name());

        raceNode.put("subrace", subrace);
        ObjectNode subraceNode = raceNode.withObjectProperty(Integer.toString(subrace));

        subraceNode.put("exp", exp);
        subraceNode.put("rank", rank);

        writeSpecificData(subraceNode);
    }

    protected abstract void writeSpecificData(ObjectNode subraceNode);

    public final void loadProfileData(JsonNode rootNode) {
        
    }

}
