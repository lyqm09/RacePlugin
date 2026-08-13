package be.lymaes.race.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public interface IRaceData {



    int getRank();
    void rankUp();

    int getExp();
    void addExp(int n);
    void subExp(int n);

    void saveProfileData(ObjectNode rootNode);
}
