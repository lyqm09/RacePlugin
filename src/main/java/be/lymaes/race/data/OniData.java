package be.lymaes.race.data;

import be.lymaes.race.model.RaceType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class OniData extends RaceData {

    public static final RaceType RACE_TYPE = RaceType.ONI;

    private final IRaceData overlay;

    public OniData(int rank, int exp, IRaceData overlay) {
        super(RACE_TYPE, -1, rank, exp);

        this.overlay = overlay;
    }

    public OniData(int rank, int exp) {
        this(rank, exp, null);
    }

    public IRaceData getOverlay() {
        return overlay;
    }

    @Override
    protected void saveSpecificData(ObjectNode node) {
        ObjectNode overlayNode = node.withObjectProperty("overlay");

        if(overlay == null)
            return;

        if(!overlayNode.isEmpty())
            return;

        overlay.saveProfileData(overlayNode);
    }

    public static OniData loadProfileData(JsonNode rootNode, RaceType.PrimaryData primaryData) {
        if (rootNode != null && rootNode.has(RACE_TYPE.name())) {
            JsonNode node = rootNode.get(RACE_TYPE.name());

            RaceType.PrimaryData data = loadProfileData(node, RACE_TYPE, -1);
            RaceData overlayData = null;

            node = node.get("overlay");
            if(node != null && !node.isEmpty()) {
                String name = node.fieldNames().next();
                RaceType overlay = RaceType.fromName(name);

                overlay.loadData.apply(node, new RaceType.PrimaryData(-1, 0, 0));
            }

            return new OniData(data.rank(), data.exp(), overlayData);
        }

        return new OniData(primaryData.rank(), primaryData.exp());
    }

}
