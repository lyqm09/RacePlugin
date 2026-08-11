package be.lymaes.race;

import be.lymaes.race.model.IRace;
import be.lymaes.race.model.ISubRaceable;
import be.lymaes.race.model.RaceType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class RaceProfile {

    public final Player player;

    public final RaceType race;
    public final int subRace;

    private int exp;
    private int rank;

    private Map<String, Long> times;

    public RaceProfile(Player player, RaceType race, int subRace, int exp, int rank, Map<String, Long> times) {
        this.player = player;
        this.race = race;
        this.subRace = subRace;
        this.exp = exp;
        this.rank = rank;
        if(times != null) {
            this.times = times;
        } else {
            this.times = new HashMap<>();
        }
    }

    public RaceProfile(Player player, RaceType race) {
        this(player, race, 0, 0, 0, new HashMap<>());
    }

    public void setExp(int n) {
        exp = n;
    }

    public void addExp(int n) {
        exp += n;
        updateTabInfo();
    }

    public void subExp(int n) {
        exp -= n;
        updateTabInfo();
    }

    public int getExp() {
        return exp;
    }

    public void rankUp() {
        rank++;
        updateTabInfo();
    }

    public int getRank() {
        return rank;
    }

    public long getTime(String key) {
        Long time = times.get(key);
        return time != null ? time : 0L;
    }

    public void putTime(String key, Long value) {
        times.put(key, value);
    }

    public void removeTime(String key) {
        times.remove(key);
    }

    // utils functions

    public void setTabName() {
        Component tabNameComponent = Component.empty()
                .append(Component.text("[").color(NamedTextColor.WHITE))
                .append(Component.text(race.name).color(race.color))
                .append(Component.text("] ").color(NamedTextColor.WHITE))
                .append(Component.text(player.getName()).color(race.color));
        LegacyComponentSerializer traducteur = LegacyComponentSerializer.builder()
                .character('§')
                .hexColors()
                .build();

        String tabName = traducteur.serialize(tabNameComponent);

        player.setPlayerListName(tabName);
    }

    public void updateTabInfo() {
        IRace irace = Race.getInstance().getRaceManager().getRaceModel(race);

        String info;

        if(irace instanceof ISubRaceable) {
            info = ((ISubRaceable) irace).getSubraceName(subRace) + " - " + irace.getRankName(rank);
        } else {
            info = irace.getRankName(rank);
        }

        if(info == null) {
            player.setPlayerListFooter(null);
            return;
        }

        int expRequired = irace.getExpRequired(rank + 1);
        if (expRequired > 0) {
            int totalBars = 20;

            int completedBars = (totalBars * exp) / expRequired;
            int emptyBars = totalBars - completedBars;

            if(emptyBars < 0) {
                completedBars = totalBars;
                emptyBars = 0;
            }

            info += "\n[" + "■".repeat(completedBars) + "-".repeat(emptyBars) + "] (" + exp + "/" + expRequired + ")";
        }

        player.setPlayerListFooter("\n" + info);

    }

    // sauvegarde

    public static CompletableFuture<RaceProfile> loadProfile(Player player, RaceType race, int subrace) {
        CompletableFuture<RaceProfile> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(Race.getInstance(), () -> {
            Path file = Paths.get(Race.getInstance().getDataFolder().toPath() + "Race/profiles/" + player.getUniqueId() + ".json");

            if (Files.exists(file)) {

                JsonNode rootNode;

                try {
                    rootNode = Race.MAPPER.readTree(file.toFile());
                } catch (IOException e) {
                    System.err.println("Invalid Json... (Empty)");
                    rootNode = Race.MAPPER.createObjectNode();
                }

                String raceName;
                RaceType raceType;
                if (race != null) {
                    raceName = race.name();
                    raceType = race;
                } else {
                    raceName = rootNode.path("current").asText(RaceType.HUMAN.name());
                    raceType = RaceType.fromName(raceName);
                }

                if (rootNode.has(raceName)) {
                    JsonNode raceNode = rootNode.get(raceName);

                    int sub;
                    int exp = 0;
                    int rank = 0;
                    HashMap<String, Long> times = null;

                    if (Race.getInstance().getRaceManager().getRaceModel(raceType) instanceof ISubRaceable) {
                        sub = subrace < 0 ? raceNode.path("subrace").asInt(0) : subrace;

                        JsonNode subraceNode = raceNode.get(Integer.toString(sub));
                        if(subraceNode != null) {
                            exp = subraceNode.path("exp").asInt(0);
                            rank = subraceNode.path("rank").asInt(0);

                            JsonNode timesNode = subraceNode.get("times");
                            if (timesNode != null) {
                                times = Race.MAPPER.convertValue(timesNode, new TypeReference<HashMap<String, Long>>() {
                                });
                            }
                        }

                    } else {
                        sub = -1;
                        exp = raceNode.path("exp").asInt(0);
                        rank = raceNode.path("rank").asInt(0);

                        JsonNode timesNode = raceNode.get("times");
                        if(timesNode != null) {
                            times = Race.MAPPER.convertValue(timesNode, new TypeReference<HashMap<String, Long>>(){});
                        }
                    }

                    RaceProfile profile = new RaceProfile(player, raceType, sub, exp, rank, times);
                    Bukkit.getScheduler().runTask(Race.getInstance(), () -> future.complete(profile));
                }
            }

            RaceProfile defaultProfile = new RaceProfile(player, race != null ? race : RaceType.HUMAN);
            Bukkit.getScheduler().runTask(Race.getInstance(), () -> future.complete(defaultProfile));
        });

        return future;
    }

    public static CompletableFuture<RaceProfile> loadProfile(Player player) {
        return loadProfile(player, null, -1);
    }

    public void save() {
        Bukkit.getScheduler().runTaskAsynchronously(Race.getInstance(), () -> {
            Path file = Paths.get(Race.getInstance().getDataFolder().toPath() + "Race/profiles/" + player.getUniqueId() + ".json");

            try {
                Files.createDirectories(file.getParent());

                ObjectNode rootNode;
                if (Files.exists(file) && Files.size(file) > 0) {
                    rootNode = (ObjectNode) Race.MAPPER.readTree(file.toFile());
                } else {
                    rootNode = Race.MAPPER.createObjectNode();
                }

                rootNode.put("current", race.name());

                ObjectNode raceNode = rootNode.withObjectProperty(race.name());

                JsonNode timesNode = Race.MAPPER.valueToTree(times);

                if (Race.getInstance().getRaceManager().getRaceModel(race) instanceof ISubRaceable) {
                    raceNode.put("subrace", subRace);

                    ObjectNode subraceNode = raceNode.withObjectProperty(Integer.toString(subRace));
                    subraceNode.put("exp", exp);
                    subraceNode.put("rank", rank);
                    subraceNode.set("times", timesNode);
                } else {
                    raceNode.put("exp", exp);
                    raceNode.put("rank", rank);
                    raceNode.set("times", timesNode);
                }

                Race.MAPPER.writerWithDefaultPrettyPrinter().writeValue(file.toFile(), rootNode);

            } catch (IOException e) {
                throw new RuntimeException("Unable to save the profile file for " + player.getUniqueId(), e);
            }
        });
    }

}
