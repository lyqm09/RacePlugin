package be.lymaes.race.manager;

import be.lymaes.race.Race;
import be.lymaes.race.RaceProfile;
import be.lymaes.race.data.IRaceData;
import be.lymaes.race.model.*;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.function.Function;

public class RaceManager {

    private static final NamespacedKey RACE_KEY = NamespacedKey.fromString("race:race_name");
    private static final NamespacedKey SUBRACE_KEY = NamespacedKey.fromString("race:subrace_id");
    private static final NamespacedKey RANK_KEY = NamespacedKey.fromString("race:rank");

    private Map<RaceType, IRace<?>> register = new EnumMap<>(RaceType.class);
    private Map<UUID, RaceProfile> profiles = new HashMap<>();

    private Map<UUID, Function<Player, Boolean>> pendingOffer = new HashMap<>();

    public RaceManager() {
        for(RaceType race : RaceType.values()) {
            register.put(race, race.model.get());
        }
    }

    public void terminate() {
        for(RaceProfile profile : profiles.values()) {
            profile.saveSynchronously();
        }

        pendingOffer.clear();
        profiles.clear();
        register.clear();
    }

    public void changeRace(Player player, RaceType race) {
        changeRace(player, race, -1);
    }

    public void changeRace(Player player, RaceType race, int subrace) {
        RaceProfile profile = getProfile(player);
        IRaceData raceData = profile.getRaceData();

        if (raceData.getRace() == race) {
            if (register.get(race) instanceof ISubRaceable) {
                if (raceData.getSubrace() == subrace) return;
            } else {
                return;
            }
        }

        save(profile);

        profile.clearVisualQueue();
        getRaceModel(raceData.getRace()).cleanup(player, profile);

        RaceProfile.loadProfile(player, race, subrace).thenAccept(newProfile -> addAndApply(player, race, newProfile));
    }

    public void changeRace(@NonNull Player player, @NonNull IRaceData data) {
        RaceProfile profile = getProfile(player);
        IRaceData raceData = profile.getRaceData();

        save(profile);
        profile.clearVisualQueue();
        getRaceModel(raceData.getRace()).cleanup(player, profile);

        RaceProfile newProfile = new RaceProfile(player.getUniqueId(), data);
        addAndApply(player, data.getRace(), newProfile);
    }

    private void addAndApply(Player player, RaceType race, RaceProfile newProfile) {
        profiles.put(player.getUniqueId(), newProfile);

        IRaceData newRaceData = newProfile.getRaceData();

        getRaceModel(race).applyRacePerks(player, newProfile, newRaceData);
        player.getPersistentDataContainer().set(RACE_KEY, PersistentDataType.STRING, newRaceData.getRace().name());
        player.getPersistentDataContainer().set(SUBRACE_KEY, PersistentDataType.INTEGER, newRaceData.getSubrace());

        newProfile.setTabName();
        newProfile.updateTabInfo();
    }

    private void verifyAndLoadRace(RaceProfile profile) {
        Player player = profile.getPlayer();
        IRaceData raceData = profile.getRaceData();

        String raceName = player.getPersistentDataContainer().get(RACE_KEY, PersistentDataType.STRING);
        int subRaceId = player.getPersistentDataContainer().getOrDefault(SUBRACE_KEY, PersistentDataType.INTEGER, 0);
        int rank = player.getPersistentDataContainer().getOrDefault(RANK_KEY, PersistentDataType.INTEGER, 0);

        IRace oldRace = getRaceModel(RaceType.fromName(raceName));

        if (raceName == null) {
            player.getPersistentDataContainer().set(RACE_KEY, PersistentDataType.STRING, raceData.getRace().name());
            player.getPersistentDataContainer().set(SUBRACE_KEY, PersistentDataType.INTEGER, raceData.getSubrace());
        }

        else if (!raceName.equalsIgnoreCase(raceData.getRace().name())) {
            oldRace.cleanup(player, profile);

            player.getPersistentDataContainer().set(RACE_KEY, PersistentDataType.STRING, raceData.getRace().name());
            player.getPersistentDataContainer().set(SUBRACE_KEY, PersistentDataType.INTEGER, raceData.getSubrace());
        }

        else if (oldRace instanceof ISubRaceable) {
            if(subRaceId != raceData.getSubrace()) {
                oldRace.cleanup(player, profile);

                player.getPersistentDataContainer().set(SUBRACE_KEY, PersistentDataType.INTEGER, raceData.getSubrace());
            }
        }

        if(rank != raceData.getRank()) {
            player.getPersistentDataContainer().set(RANK_KEY, PersistentDataType.INTEGER, raceData.getRank());
        }

        getRaceModel(raceData.getRace()).applyRacePerks(player, profile, raceData);
    }

    public void load(Player player) {
        RaceProfile.loadProfile(player).thenAccept(profile -> {
            profiles.put(player.getUniqueId(), profile);

            verifyAndLoadRace(profile);

            profile.setTabName();
            profile.updateTabInfo();
        });
    }

    public void unload(Player player) {
        RaceProfile profile = getProfile(player);

        save(profile);
        player.getPersistentDataContainer().set(RANK_KEY, PersistentDataType.INTEGER, profile.getRaceData().getRank());

        profile.clearVisualQueue();

        profiles.remove(player.getUniqueId());
    }

    public void save(RaceProfile profile) {
        profile.save();
    }

    public Collection<IRace<?>> getModels() {
        return register.values();
    }

    public IRace getRaceModel(RaceType race) {
        return register.get(race);
    }

    public RaceProfile getProfile(Player player) {
        if(!profiles.containsKey(player.getUniqueId()))
            return new RaceProfile(player.getUniqueId(), RaceType.HUMAN.loadData.apply(null, null));
        return profiles.get(player.getUniqueId());
    }

    public void putPendingOffer(UUID token, Function<Player, Boolean> function, long cooldown) {
        pendingOffer.put(token, function);

        if(cooldown < 0) return;
        Bukkit.getScheduler().runTaskLater(Race.getInstance(), () -> pendingOffer.remove(token), cooldown);
    }

    public Function<Player, Boolean> getPendingOffer(UUID token) {
        return pendingOffer.get(token);
    }

    public void removePendingOffer(UUID token) {
        pendingOffer.remove(token);
    }

}
