package be.lymaes.race;

import be.lymaes.race.ability.*;
import be.lymaes.race.ability.model.Targetable;
import be.lymaes.race.data.IRaceData;
import be.lymaes.race.manager.RaceManager;
import be.lymaes.race.model.IRace;
import be.lymaes.race.model.IRankable;
import be.lymaes.race.model.ISubRaceable;
import be.lymaes.race.model.RaceType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class RaceProfile {

    public final UUID uuid;
    public final IRaceData raceData;
    private transient Map<AbilityType, Set<Ability>> abilities;
    private transient Queue<Runnable> visualQueue;

    public RaceProfile(UUID uuid, IRaceData raceData) {
        this.uuid = uuid;
        this.raceData = raceData;
        this.abilities = new EnumMap<>(AbilityType.class);
        this.visualQueue = new LinkedList<>();
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public void addAbility(String key) {
        Ability ability = Race.getInstance().getAbilityManager().getAbility(key);

        if(ability instanceof Taskable) {
            abilities.computeIfAbsent(AbilityType.TASKABLE, k -> new HashSet<>()).add(ability);
        }
        if(ability instanceof Consumer) {
            abilities.computeIfAbsent(AbilityType.CONSUMER, k -> new HashSet<>()).add(ability);
        }
        if(ability instanceof CommandSender) {
            abilities.computeIfAbsent(AbilityType.COMMAND_SENDER, k -> new HashSet<>()).add(ability);
        }
        if(ability instanceof Crafter) {
            abilities.computeIfAbsent(AbilityType.CRAFTER, k -> new HashSet<>()).add(ability);
        }
        if(ability instanceof Damager) {
            abilities.computeIfAbsent(AbilityType.DAMAGER, k -> new HashSet<>()).add(ability);
        }
        if(ability instanceof Defender) {
            abilities.computeIfAbsent(AbilityType.DEFENDER, k -> new HashSet<>()).add(ability);
        }
        if(ability instanceof Killer) {
            abilities.computeIfAbsent(AbilityType.KILLER, k -> new HashSet<>()).add(ability);
        }
        if(ability instanceof Interact) {
            abilities.computeIfAbsent(AbilityType.INTERACT, k -> new HashSet<>()).add(ability);
        }
        if(ability instanceof Helder) {
            abilities.computeIfAbsent(AbilityType.HELDER, k -> new HashSet<>()).add(ability);
        }
        if(ability instanceof Merchant) {
            abilities.computeIfAbsent(AbilityType.MERCHANT, k -> new HashSet<>()).add(ability);
        }
        if(ability instanceof RaidWinner) {
            abilities.computeIfAbsent(AbilityType.RAID_WINNER, k -> new HashSet<>()).add(ability);
        }
        if(ability instanceof Sneaker) {
            abilities.computeIfAbsent(AbilityType.SNEAKER, k -> new HashSet<>()).add(ability);
        }
        if(ability instanceof Targetable) {
            abilities.computeIfAbsent(AbilityType.TARGETABLE, k -> new HashSet<>()).add(ability);
        }
    }

    public void removeAbility(String key) {
        Ability ability = Race.getInstance().getAbilityManager().getAbility(key);

        if(ability instanceof Taskable) {
            removeAbility(AbilityType.TASKABLE, ability);
        }
        if(ability instanceof Consumer) {
            removeAbility(AbilityType.CONSUMER, ability);
        }
        if(ability instanceof CommandSender) {
            removeAbility(AbilityType.COMMAND_SENDER, ability);
        }
        if(ability instanceof Crafter) {
            removeAbility(AbilityType.CRAFTER, ability);
        }
        if(ability instanceof Damager) {
            removeAbility(AbilityType.DAMAGER, ability);
        }
        if(ability instanceof Defender) {
            removeAbility(AbilityType.DEFENDER, ability);
        }
        if(ability instanceof Killer) {
            removeAbility(AbilityType.KILLER, ability);
        }
        if(ability instanceof Interact) {
            removeAbility(AbilityType.INTERACT, ability);
        }
        if(ability instanceof Helder) {
            removeAbility(AbilityType.HELDER, ability);
        }
        if(ability instanceof Merchant) {
            removeAbility(AbilityType.MERCHANT, ability);
        }
        if(ability instanceof RaidWinner) {
            removeAbility(AbilityType.RAID_WINNER, ability);
        }
        if(ability instanceof Sneaker) {
            removeAbility(AbilityType.SNEAKER, ability);
        }
        if(ability instanceof Targetable) {
            removeAbility(AbilityType.TARGETABLE, ability);
        }
    }

    private void removeAbility(AbilityType type, Ability ability) {
        Set<Ability> list = abilities.get(type);
        if(list != null) {
            list.remove(ability);
        }
    }

    public <T extends Ability> Set<T> getAbilities(AbilityType type) {
        return (Set<T>) abilities.getOrDefault(type, Collections.emptySet());
    }

    public void addVisualEffect(Runnable effect) {
        boolean wasEmpty = visualQueue.isEmpty();
        visualQueue.add(effect);

        if(wasEmpty) {
            playNextVisualEffect();
        }
    }

    public void playNextVisualEffect() {
        if(visualQueue.isEmpty()) return;

        Player player = getPlayer();
        if(player != null && player.isOnline()) {

            Runnable runnable = visualQueue.peek();
            if(runnable != null) {
                runnable.run();
                Bukkit.getScheduler().runTaskLater(Race.getInstance(), () -> {
                    visualQueue.remove();
                    playNextVisualEffect();
                }, 50L);
            } else {
                playNextVisualEffect();
            }

        } else {
            visualQueue.clear();
        }
    }

    public void clearVisualQueue() {
        visualQueue.clear();
    }

    // RaceData

    public void addExp(int n) {
        raceData.addExp(n);
        tryRankUp();
        updateTabInfo();
    }

    public void rankUp() {
        raceData.rankUp();

        IRace irace = Race.getInstance().getRaceManager().getRaceModel(raceData.getRace());
        if(!(irace instanceof IRankable rankable)) return;

        irace.applyRacePerks(getPlayer(), this, raceData);

        String rankName = rankable.getRankName(raceData.getRank());
        addVisualEffect(() -> {
            Messager messager = Race.getInstance().getMessager();
            messager.sendRankUpTitle(uuid, rankName);
        });
    }

    void tryRankUp() {
        IRace irace = Race.getInstance().getRaceManager().getRaceModel(raceData.getRace());
        if(!(irace instanceof IRankable rankable)) return;

        while (true) {
            int expRequired = rankable.getExpRequired(raceData.getRank() + 1);
            if(expRequired < 0) break;
            if (raceData.getExp() < expRequired) break;
            if(!rankable.canRankUp(this)) break;

            raceData.subExp(expRequired);
            rankUp();
        }
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

        if(!(Race.getInstance().getRaceManager().getRaceModel(raceData.getRace()) instanceof IRankable irace)) return;

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
            RaceManager raceManager = Race.getInstance().getRaceManager();

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
                if(raceManager.getRaceModel(profile.raceData.getRace()) instanceof IRankable rankable) {
                    rankable.addExpAbilities(profile);
                }
                Bukkit.getScheduler().runTask(Race.getInstance(), () -> future.complete(profile));
                return;
            }

            RaceProfile defaultProfile = new RaceProfile(player.getUniqueId(), race != null ? race.loadData.apply(null, new RaceType.PrimaryData(-1, 0, 0)) : RaceType.HUMAN.loadData.apply(null, null));
            if(raceManager.getRaceModel(defaultProfile.raceData.getRace()) instanceof IRankable rankable) {
                rankable.addExpAbilities(defaultProfile);
            }
            Bukkit.getScheduler().runTask(Race.getInstance(), () -> future.complete(defaultProfile));
        });

        return future;
    }

    public static CompletableFuture<RaceProfile> loadProfile(Player player) {
        return loadProfile(player, null, -1);
    }

    public void save() { // save after reading
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
