package be.lymaes.race.manager;

import be.lymaes.race.RaceProfile;
import be.lymaes.race.model.*;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class RaceManager {

    private static final NamespacedKey RACE_KEY = NamespacedKey.fromString("race:race_name");
    private static final NamespacedKey SUBRACE_KEY = NamespacedKey.fromString("race:subrace_id");
    private static final NamespacedKey RANK_KEY = NamespacedKey.fromString("race:rank");

    private Map<RaceType, IRace> register = new EnumMap<>(RaceType.class);
    private Map<Player, RaceProfile> profiles = new HashMap<>();
    private Map<RaceType, List<RaceProfile>> races = new EnumMap<>(RaceType.class);

    public RaceManager() {
        register.put(RaceType.HUMAN, new Human());
        register.put(RaceType.KARYU, new Karyu());
        register.put(RaceType.KITSUNE, new Kitsune());
        register.put(RaceType.ONI, new Oni());
        register.put(RaceType.TAMASHI, new Tamashi());

        races.put(RaceType.HUMAN, new ArrayList<>());
        races.put(RaceType.KARYU, new ArrayList<>());
        races.put(RaceType.KITSUNE, new ArrayList<>());
        races.put(RaceType.ONI, new ArrayList<>());
        races.put(RaceType.TAMASHI, new ArrayList<>());
    }

    public void terminate() {
        register.clear();
        profiles.clear();
        for(List<RaceProfile> lists : races.values()) {
            lists.clear();
        }
        races.clear();
    }

    public void changeRace(Player player, RaceType race) {
        changeRace(player, race, -1);
    }

    public void changeRace(Player player, RaceType race, int subrace) {
        RaceProfile profile = getProfile(player);

        save(profile);

        getRaceModel(profile.race).cleanup(profile);
        removePlayerFromRaces(profile);

        RaceProfile.loadProfile(player, race, subrace).thenAccept(newProfile -> {
            profiles.put(player, newProfile);

            addPlayerToRaces(newProfile);

            getRaceModel(race).loadRank(newProfile);
            player.getPersistentDataContainer().set(RACE_KEY, PersistentDataType.STRING, newProfile.race.name());
            player.getPersistentDataContainer().set(SUBRACE_KEY, PersistentDataType.INTEGER, newProfile.subRace);

            newProfile.setTabName();
            newProfile.updateTabInfo();
        });
    }

    private void verifyAndLoadRace(RaceProfile profile) {
        String raceName = profile.player.getPersistentDataContainer().get(RACE_KEY, PersistentDataType.STRING);
        int subRaceId = profile.player.getPersistentDataContainer().getOrDefault(SUBRACE_KEY, PersistentDataType.INTEGER, 0);
        int rank = profile.player.getPersistentDataContainer().getOrDefault(RANK_KEY, PersistentDataType.INTEGER, 0);

        IRace oldRace = getRaceModel(RaceType.fromName(raceName));

        if (raceName == null) {
            getRaceModel(profile.race).loadRank(profile);
            profile.player.getPersistentDataContainer().set(RACE_KEY, PersistentDataType.STRING, profile.race.name());
            profile.player.getPersistentDataContainer().set(SUBRACE_KEY, PersistentDataType.INTEGER, profile.subRace);
        }

        else if (!raceName.equalsIgnoreCase(profile.race.name())) {
            oldRace.cleanup(profile);

            getRaceModel(profile.race).loadRank(profile);
            profile.player.getPersistentDataContainer().set(RACE_KEY, PersistentDataType.STRING, profile.race.name());
            profile.player.getPersistentDataContainer().set(SUBRACE_KEY, PersistentDataType.INTEGER, profile.subRace);
        }

        else if (oldRace instanceof ISubRaceable) {
            if(subRaceId != profile.subRace) {
                oldRace.cleanup(profile);

                getRaceModel(profile.race).loadRank(profile);
                profile.player.getPersistentDataContainer().set(SUBRACE_KEY, PersistentDataType.INTEGER, profile.subRace);
            }
        }

        if(rank != profile.getRank()) {
            getRaceModel(profile.race).loadRank(profile);
            profile.player.getPersistentDataContainer().set(RANK_KEY, PersistentDataType.INTEGER, profile.getRank());
        }
    }

    public void load(Player player) {
        RaceProfile.loadProfile(player).thenAccept(profile -> {
            profiles.put(player, profile);

            verifyAndLoadRace(profile);

            addPlayerToRaces(profile);

            profile.setTabName();
            profile.updateTabInfo();
        });
    }

    public void unload(Player player) {
        RaceProfile profile = getProfile(player);

        save(profile);
        player.getPersistentDataContainer().set(RANK_KEY, PersistentDataType.INTEGER, profile.getRank());

        removePlayerFromRaces(profile);

        profiles.remove(player);
    }

    public void save(RaceProfile profile) {
        profile.save();
    }

    public void saveAll() {
        for(RaceProfile profile : profiles.values()) {
            save(profile);
        }
    }

    public Collection<IRace> getRegisterValues() {
        return register.values();
    }

    public IRace getRaceModel(RaceType race) {
        return register.get(race);
    }

    public RaceProfile getProfile(Player player) {
        if(!profiles.containsKey(player))
            return new RaceProfile(player, RaceType.HUMAN);
        return profiles.get(player);
    }

    private void addPlayerToRaces(RaceProfile profile) {
        if(races.containsKey(profile.race)) {
            List<RaceProfile> players = new ArrayList<>();
            players.add(profile);
            races.put(profile.race, players);
        } else {
            races.get(profile.race).add(profile);
        }
    }

    private void removePlayerFromRaces(RaceProfile profile) {
        if(races.containsKey(profile.race)) {
            races.get(profile.race).remove(profile);
        }
    }

    public List<RaceProfile> getRaceProfiles(RaceType race) {
        return races.get(race);
    }

}
