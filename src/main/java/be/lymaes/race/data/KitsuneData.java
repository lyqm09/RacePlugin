package be.lymaes.race.data;

import be.lymaes.race.Race;
import be.lymaes.race.ability.AbilityKey;
import be.lymaes.race.ability.model.Offering;
import be.lymaes.race.model.RaceType;
import be.lymaes.race.util.SimpleBlockLocation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;

public class KitsuneData extends RaceData {

    public static final RaceType RACE_TYPE = RaceType.KITSUNE;

    private long timeInForest;
    private SimpleBlockLocation kamiBlockLoc;

    public KitsuneData(int rank, int exp, long timeInForest, SimpleBlockLocation kamiBlockLocation) {
        super(RACE_TYPE, -1, rank, exp);

        this.timeInForest = timeInForest;
        this.kamiBlockLoc = kamiBlockLocation;
    }

    public KitsuneData(int rank, int exp) {
        this(rank, exp, 0L, null);
    }

    public long getTimeInForest() {
        return timeInForest;
    }

    public void setTimeInForest(long time) {
        timeInForest = time;
    }

    public void setKamiBlockLocation(Block block) {
        kamiBlockLoc = new SimpleBlockLocation(block.getWorld().getUID(), block.getX(), block.getY(), block.getZ());
    }

    @Override
    protected void saveSpecificData(ObjectNode node) {
        node.put("time_in_forest", System.currentTimeMillis() - timeInForest);
        if(kamiBlockLoc != null) {
            try {
                node.put("kami_block", Race.MAPPER.writeValueAsString(kamiBlockLoc));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            Race plugin = Race.getInstance();
            if(plugin.getAbilityManager().getAbility(AbilityKey.PERM_SETKAMI) instanceof Offering offering) {
                if(plugin.isEnabled()) {
                    Bukkit.getScheduler().runTask(plugin, () -> offering.removeKamiBlock(kamiBlockLoc));
                } else {
                    offering.removeKamiBlock(kamiBlockLoc);
                }
            }
        }
    }

    public static KitsuneData loadProfileData(JsonNode rootNode, RaceType.PrimaryData primaryData) {
        if (rootNode != null && rootNode.has(RACE_TYPE.name())) {
            JsonNode raceNode = rootNode.get(RACE_TYPE.name());

            RaceType.PrimaryData data = loadProfileData(raceNode, RACE_TYPE, -1);

            long time = raceNode.path("time_in_forest").asLong(0);
            long enterTime = time == 0 ? 0 : System.currentTimeMillis() - time;

            SimpleBlockLocation simpleLocation = null;
            String stringLocation = raceNode.path("kami_block").asText();
            if (stringLocation != null && !stringLocation.isEmpty()) {
                try {
                    SimpleBlockLocation blocLoc = Race.MAPPER.readValue(stringLocation, SimpleBlockLocation.class);

                    Race plugin = Race.getInstance();
                    if(plugin.getAbilityManager().getAbility(AbilityKey.PERM_SETKAMI) instanceof Offering offering) {

                        if(!offering.hasKamiBlock(blocLoc)) {
                            simpleLocation = blocLoc;
                            Bukkit.getScheduler().runTask(plugin, () -> offering.setKamiBlock(blocLoc));
                        }

                    }
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }

            return new KitsuneData(data.rank(), data.exp(), enterTime, simpleLocation);
        }

        return new KitsuneData(primaryData.rank(), primaryData.exp());
    }

}
