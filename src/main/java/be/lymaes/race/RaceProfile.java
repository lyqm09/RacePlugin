package be.lymaes.race;

import be.lymaes.race.data.IRaceData;
import be.lymaes.race.data.RaceData;
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
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class RaceProfile {

    public final UUID uuid;
    public final IRaceData raceData;

    public RaceProfile(UUID uuid, IRaceData raceData) {
        this.uuid = uuid;
        this.raceData = raceData;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    // RaceData

    public void rankUp() {
        raceData.rankUp();
        updateTabInfo();
    }

    public void addExp(int n) {
        raceData.addExp(n);
        updateTabInfo();
    }

    // utils functions

    public void setTabName() {
        RaceType race = raceData.getRace();
        Player player = getPlayer();

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
        Player player = getPlayer();
        int rank = raceData.getRank();
        int exp = raceData.getExp();

        IRace irace = Race.getInstance().getRaceManager().getRaceModel(raceData.getRace());

        String info;

        if(irace instanceof ISubRaceable) {
            info = ((ISubRaceable) irace).getSubraceName(raceData.getSubrace()) + " - " + irace.getRankName(rank);
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

    // save and load

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

                RaceType raceType;
                if (race != null) {
                    raceType = race;
                } else {
                    raceType = RaceType.fromName(rootNode.path("current").asText(RaceType.HUMAN.name()));
                }

                IRaceData data = raceType.loadData.apply(rootNode, new RaceType.PrimaryData(subrace, 0, 0));
                RaceProfile profile = new RaceProfile(player.getUniqueId(), data);
                Bukkit.getScheduler().runTask(Race.getInstance(), () -> future.complete(profile));
                return;
            }

            RaceProfile defaultProfile = new RaceProfile(player.getUniqueId(), race != null ? race.loadData.apply(null, new RaceType.PrimaryData(-1, 0, 0)) : RaceType.HUMAN.loadData.apply(null, null));
            Bukkit.getScheduler().runTask(Race.getInstance(), () -> future.complete(defaultProfile));
        });

        return future;
    }

    public static CompletableFuture<RaceProfile> loadProfile(Player player) {
        return loadProfile(player, null, -1);
    }

    public void save() {
        Bukkit.getScheduler().runTaskAsynchronously(Race.getInstance(), this::saveSynchronously);
    }

    public void saveSynchronously() {
        Path file = Paths.get(Race.getInstance().getDataFolder().toPath() + "Race/profiles/" + uuid + ".json");

        try {
            Files.createDirectories(file.getParent());

            ObjectNode rootNode;
            if (Files.exists(file) && Files.size(file) > 0) {
                rootNode = (ObjectNode) Race.MAPPER.readTree(file.toFile());
            } else {
                rootNode = Race.MAPPER.createObjectNode();
            }

            raceData.saveProfileData(rootNode);
            Race.MAPPER.writerWithDefaultPrettyPrinter().writeValue(file.toFile(), rootNode);

        } catch (IOException e) {
            throw new RuntimeException("Unable to save the profile file for " + uuid, e);
        }
    }

}
