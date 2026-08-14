package be.lymaes.race.data;

import be.lymaes.race.RaceProfile;
import be.lymaes.race.model.RaceType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class OniData extends RaceData {

    private final IRaceData overlay;

    public OniData(int rank, int exp, IRaceData overlay) {
        super(RaceType.ONI, -1, rank, exp);

        this.overlay = overlay;
    }

    public OniData(int rank, int exp) {
        this(rank, exp, null);
    }

    @Override
    protected void saveSpecificData(ObjectNode node) {
        ObjectNode overlayNode = node.withObjectProperty("overlay");

        if(overlay == null)
            return;

        overlay.saveProfileData(overlayNode);
    }

    public static OniData loadProfileData(JsonNode rootNode, RaceType.PrimaryData primaryData) {
        RaceType race = RaceType.ONI;

        if (rootNode.has(race.name())) {
            JsonNode raceNode = rootNode.get(race.name());

            RaceType.PrimaryData data = loadProfileData(raceNode, race, -1);
            RaceData overlay = null;

            if(raceNode.has("overlay")) {
                // TODO load overlay profile
            }

            return new OniData(data.rank(), data.exp(), overlay);
        }

        return new OniData(primaryData.rank(), primaryData.exp());
    }

}
