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

import java.util.UUID;

public class KitsuneData extends RaceData {

    public static final RaceType RACE_TYPE = RaceType.KITSUNE;

    private long timeInForest;
    private SimpleBlockLocation kamiBlockLoc;
    private UUID villageUuid;

    public KitsuneData(int rank, int exp, long timeInForest, SimpleBlockLocation kamiBlockLocation, UUID kitsuneVillageUuid) {
        super(RACE_TYPE, -1, rank, exp);

        this.timeInForest = timeInForest;
        this.kamiBlockLoc = kamiBlockLocation;
        this.villageUuid = kitsuneVillageUuid;
    }

    public KitsuneData(int rank, int exp) {
        this(rank, exp, 0L, null, null);
    }

    public long getTimeInForest() {
        return timeInForest;
    }

    public void setTimeInForest(long time) {
        timeInForest = time;
    }

    public SimpleBlockLocation getKamiBlockLocation() {
        return kamiBlockLoc;
    }

    public void setKamiBlockLocation(Block block) {
        if(block == null) {
            kamiBlockLoc = null;
            return;
        }
        kamiBlockLoc = new SimpleBlockLocation(block.getWorld().getUID(), block.getX(), block.getY(), block.getZ());
    }

    public void setVillage(UUID uuid) {
        this.villageUuid = uuid;
    }

    public UUID getVillageUuid() {
        return villageUuid;
    }

    public boolean hasVillage() {
        return villageUuid != null;
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
        } else {
            if(node.has("kami_block")) {
                node.remove("kami_block");
            }
        }

        if(villageUuid != null) {
            node.put("village_uuid", villageUuid.toString());
        } else if(node.has("village_uuid")) {
            node.remove("village_uuid");
        }
    }

    public static KitsuneData loadProfileData(JsonNode rootNode, RaceType.PrimaryData primaryData) {
        if (rootNode != null && rootNode.has(RACE_TYPE.name())) {
            JsonNode raceNode = rootNode.get(RACE_TYPE.name());

            Race plugin = Race.getInstance();

            RaceType.PrimaryData data = loadProfileData(raceNode, RACE_TYPE, -1);

            long time = raceNode.path("time_in_forest").asLong(0);
            long enterTime = time == 0 ? 0 : System.currentTimeMillis() - time;

            SimpleBlockLocation kamiBlockLoc = null;
            String kamiBlock = raceNode.path("kami_block").asText();
            if (kamiBlock != null && !kamiBlock.isEmpty()) {
                try {
                    SimpleBlockLocation blocLoc = Race.MAPPER.readValue(kamiBlock, SimpleBlockLocation.class);

                    if(plugin.getAbilityManager().getAbility(AbilityKey.PERM_SETKAMI) instanceof Offering offering) {

                        if(!offering.hasKamiBlock(blocLoc)) {
                            kamiBlockLoc = blocLoc;
                            Bukkit.getScheduler().runTask(plugin, () -> offering.setKamiBlock(blocLoc));
                        }

                    }
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }

            String villageUuidText = raceNode.path("village_uuid").asText(null);
            UUID villageUuid = null;
            if(villageUuidText != null) {
                try {
                    villageUuid = UUID.fromString(villageUuidText);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("UUID invalide trouvé dans le JSON : " + villageUuidText);
                }
            }

            return new KitsuneData(data.rank(), data.exp(), enterTime, kamiBlockLoc, villageUuid);
        }

        return new KitsuneData(primaryData.rank(), primaryData.exp());
    }

}
