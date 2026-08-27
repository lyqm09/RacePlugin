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

    public RaceManager() {
        for(RaceType race : RaceType.values()) {
            register.put(race, race.model.get());
        }
    }

    public void terminate() {
        for(RaceProfile profile : profiles.values()) {
            profile.saveSynchronously();
        }

        profiles.clear();
        register.clear();
    }

    public void changeRace(Player player, RaceType race) {
        changeRace(player, race, -1);
    }

    public void changeRace(Player player, RaceType race, int subrace) {
        RaceProfile profile = getProfile(player);

        if (profile.raceData.getRace() == race) {
            if (register.get(race) instanceof ISubRaceable) {
                if (profile.raceData.getSubrace() == subrace) return;
            } else {
                return;
            }
        }

        save(profile);

        profile.clearVisualQueue();
        getRaceModel(profile.raceData.getRace()).cleanup(profile);

        RaceProfile.loadProfile(player, race, subrace).thenAccept(newProfile -> addAndApply(player, race, newProfile));
    }

    private void addAndApply(Player player, RaceType race, RaceProfile newProfile) {
        profiles.put(player, newProfile);

        getRaceModel(race).applyRacePerks(newProfile);
        player.getPersistentDataContainer().set(RACE_KEY, PersistentDataType.STRING, newProfile.raceData.getRace().name());
        player.getPersistentDataContainer().set(SUBRACE_KEY, PersistentDataType.INTEGER, newProfile.raceData.getSubrace());

        newProfile.setTabName();
        newProfile.updateTabInfo();
    }

    private void verifyAndLoadRace(RaceProfile profile) {
        Player player = profile.getPlayer();;

        String raceName = player.getPersistentDataContainer().get(RACE_KEY, PersistentDataType.STRING);
        int subRaceId = player.getPersistentDataContainer().getOrDefault(SUBRACE_KEY, PersistentDataType.INTEGER, 0);
        int rank = player.getPersistentDataContainer().getOrDefault(RANK_KEY, PersistentDataType.INTEGER, 0);

        IRace oldRace = getRaceModel(RaceType.fromName(raceName));

        boolean wasRefreshed = false;
        if (raceName == null) {
            getRaceModel(profile.raceData.getRace()).applyRacePerks(profile);
            player.getPersistentDataContainer().set(RACE_KEY, PersistentDataType.STRING, profile.raceData.getRace().name());
            player.getPersistentDataContainer().set(SUBRACE_KEY, PersistentDataType.INTEGER, profile.raceData.getSubrace());
            wasRefreshed = true;
        }

        else if (!raceName.equalsIgnoreCase(profile.raceData.getRace().name())) {
            oldRace.cleanup(profile);

            getRaceModel(profile.raceData.getRace()).applyRacePerks(profile);
            player.getPersistentDataContainer().set(RACE_KEY, PersistentDataType.STRING, profile.raceData.getRace().name());
            player.getPersistentDataContainer().set(SUBRACE_KEY, PersistentDataType.INTEGER, profile.raceData.getSubrace());
            wasRefreshed = true;
        }

        else if (oldRace instanceof ISubRaceable) {
            if(subRaceId != profile.raceData.getSubrace()) {
                oldRace.cleanup(profile);

                getRaceModel(profile.raceData.getRace()).applyRacePerks(profile);
                player.getPersistentDataContainer().set(SUBRACE_KEY, PersistentDataType.INTEGER, profile.raceData.getSubrace());
                wasRefreshed = true;
            }
        }

        if(rank != profile.raceData.getRank()) {
            getRaceModel(profile.raceData.getRace()).applyRacePerks(profile);
            player.getPersistentDataContainer().set(RANK_KEY, PersistentDataType.INTEGER, profile.raceData.getRank());
            wasRefreshed = true;
        }

        if(!wasRefreshed) {
            getRaceModel(profile.raceData.getRace()).reapplyPerms(profile);
        }
    }

    public void load(Player player) {
        RaceProfile.loadProfile(player).thenAccept(profile -> {
            profiles.put(player, profile);

            verifyAndLoadRace(profile);

            profile.setTabName();
            profile.updateTabInfo();
        });
    }

    public void unload(Player player) {
        RaceProfile profile = getProfile(player);

        save(profile);
        player.getPersistentDataContainer().set(RANK_KEY, PersistentDataType.INTEGER, profile.raceData.getRank());

        profile.clearVisualQueue();

        profiles.remove(player);
    }

    public void save(RaceProfile profile) {
        profile.save();
    }

    public Collection<IRace> getRegisterValues() {
        return register.values();
    }

    public IRace getRaceModel(RaceType race) {
        return register.get(race);
    }

    public RaceProfile getProfile(Player player) {
        if(!profiles.containsKey(player))
            return new RaceProfile(player.getUniqueId(), RaceType.HUMAN.loadData.apply(null, null));
        return profiles.get(player);
    }

}
