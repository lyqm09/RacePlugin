package be.lymaes.race.data;

import be.lymaes.race.model.RaceType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class HumanData implements IRaceData {

    public static final RaceType RACE_TYPE = RaceType.HUMAN;

    @Override
    public RaceType getRace() {
        return RACE_TYPE;
    }

    @Override
    public int getSubrace() {
        return -1;
    }

    @Override
    public int getRank() {
        return 0;
    }

    @Override
    public void rankUp() {

    }

    @Override
    public void setRank(int n) {

    }

    @Override
    public int getExp() {
        return 0;
    }

    @Override
    public void addExp(int n) {

    }

    @Override
    public void subExp(int n) {

    }

    @Override
    public void saveProfileData(ObjectNode rootNode) {

    }

    public static HumanData loadProfileData(JsonNode rootNode, RaceType.PrimaryData primaryData) {
        return new HumanData();
    }

}
