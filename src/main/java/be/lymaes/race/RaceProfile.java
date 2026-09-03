package be.lymaes.race;

import be.lymaes.race.ability.*;
import be.lymaes.race.ability.model.EmptyAbility;
import be.lymaes.race.ability.model.Offering;
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
    private transient Set<AbilityKey> abilities;
    private transient Map<AbilityType, Set<Ability>> eventAbilities;
    private transient Queue<Runnable> visualQueue;

    public RaceProfile(UUID uuid, IRaceData raceData) {
        this.uuid = uuid;
        this.raceData = raceData;
        this.abilities = EnumSet.noneOf(AbilityKey.class);
        this.eventAbilities = new EnumMap<>(AbilityType.class);
        this.visualQueue = new LinkedList<>();
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public void addAbility(AbilityKey key) {
        abilities.add(key);

        Ability ability = Race.getInstance().getAbilityManager().getAbility(key);
        if(ability instanceof EmptyAbility) return;

        if(ability instanceof Taskable) {
            eventAbilities.computeIfAbsent(AbilityType.TASKABLE, k -> new HashSet<>()).add(ability);
        }
        if(ability instanceof Consumer) {
            eventAbilities.computeIfAbsent(AbilityType.CONSUMER, k -> new HashSet<>()).add(ability);
        }
        if(ability instanceof CommandSender commandSender) {
            commandSender.addPermission(getPlayer());
        }
        if(ability instanceof Damager) {
            eventAbilities.computeIfAbsent(AbilityType.DAMAGER, k -> new HashSet<>()).add(ability);
        }
        if(ability instanceof Defender) {
            eventAbilities.computeIfAbsent(AbilityType.DEFENDER, k -> new HashSet<>()).add(ability);
        }
        if(ability instanceof Killer) {
            eventAbilities.computeIfAbsent(AbilityType.KILLER, k -> new HashSet<>()).add(ability);
        }
        if(ability instanceof Interact) {
            eventAbilities.computeIfAbsent(AbilityType.INTERACT, k -> new HashSet<>()).add(ability);
        }
        if(ability instanceof ItemDropping) {
            eventAbilities.computeIfAbsent(AbilityType.ITEM_DROPPING, k -> new HashSet<>()).add(ability);
        }
        if(ability instanceof Helder) {
            eventAbilities.computeIfAbsent(AbilityType.HELDER, k -> new HashSet<>()).add(ability);
        }
        if(ability instanceof Merchant) {
            eventAbilities.computeIfAbsent(AbilityType.MERCHANT, k -> new HashSet<>()).add(ability);
        }
        if(ability instanceof RaidWinner) {
            eventAbilities.computeIfAbsent(AbilityType.RAID_WINNER, k -> new HashSet<>()).add(ability);
        }
        if(ability instanceof Sneaker) {
            eventAbilities.computeIfAbsent(AbilityType.SNEAKER, k -> new HashSet<>()).add(ability);
        }
        if(ability instanceof Targetable) {
            eventAbilities.computeIfAbsent(AbilityType.TARGETABLE, k -> new HashSet<>()).add(ability);
        }
    }

    public void removeAbility(AbilityKey key) {
        abilities.remove(key);

        Ability ability = Race.getInstance().getAbilityManager().getAbility(key);
        if(ability instanceof EmptyAbility) return;

        if(ability instanceof Taskable) {
            removeEventAbility(AbilityType.TASKABLE, ability);
        }
        if(ability instanceof Consumer) {
            removeEventAbility(AbilityType.CONSUMER, ability);
        }
        if(ability instanceof CommandSender commandSender) {
            commandSender.removePermission(getPlayer());
        }
        if(ability instanceof Damager) {
            removeEventAbility(AbilityType.DAMAGER, ability);
        }
        if(ability instanceof Defender) {
            removeEventAbility(AbilityType.DEFENDER, ability);
        }
        if(ability instanceof Killer) {
            removeEventAbility(AbilityType.KILLER, ability);
        }
        if(ability instanceof Interact) {
            removeEventAbility(AbilityType.INTERACT, ability);
        }
        if(ability instanceof ItemDropping) {
            removeEventAbility(AbilityType.ITEM_DROPPING, ability);
        }
        if(ability instanceof Helder) {
            removeEventAbility(AbilityType.HELDER, ability);
        }
        if(ability instanceof Merchant) {
            removeEventAbility(AbilityType.MERCHANT, ability);
        }
        if(ability instanceof RaidWinner) {
            removeEventAbility(AbilityType.RAID_WINNER, ability);
        }
        if(ability instanceof Sneaker) {
            removeEventAbility(AbilityType.SNEAKER, ability);
        }
        if(ability instanceof Targetable) {
            removeEventAbility(AbilityType.TARGETABLE, ability);
        }
    }

    private void removeEventAbility(AbilityType type, Ability ability) {
        Set<Ability> list = eventAbilities.get(type);
        if(list != null) {
            list.remove(ability);
        }
    }

    public boolean hasAbility(AbilityKey key) {
        return abilities.contains(key);
    }

    @SuppressWarnings("unchecked")
    public <T extends Ability> Set<T> getEventAbilities(AbilityType type) {
        return (Set<T>) eventAbilities.getOrDefault(type, Collections.emptySet());
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

            rootNode.put("current", raceData.getRace().name());
            raceData.saveProfileData(rootNode);
            Race.MAPPER.writerWithDefaultPrettyPrinter().writeValue(file.toFile(), rootNode);

        } catch (IOException e) {
            throw new RuntimeException("Unable to save the profile file for " + uuid, e);
        }
    }

}
