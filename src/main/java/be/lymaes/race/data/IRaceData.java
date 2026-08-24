package be.lymaes.race.data;

import be.lymaes.race.model.RaceType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public interface IRaceData {

    RaceType getRace();
    int getSubrace();

    int getRank();
    void rankUp();
    void setRank(int n);

    int getExp();
    void addExp(int n);
    void subExp(int n);

    void saveProfileData(ObjectNode rootNode);
}
