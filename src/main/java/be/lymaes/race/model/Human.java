package be.lymaes.race.model;

import be.lymaes.race.RaceProfile;
import be.lymaes.race.ability.Ability;
import be.lymaes.race.ability.AbilityKey;
import be.lymaes.race.data.HumanData;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Map;

public class Human implements IRace<HumanData> {

    @Override
    public Map<AbilityKey, Ability> getAbilities() {
        return Collections.emptyMap();
    }

    @Override
    public void applyRacePerks(Player player, RaceProfile profile, HumanData data) {

    }

    @Override
    public void reapplyPerms(Player player, HumanData data) {

    }

    @Override
    public void reapplyEffect(Player player, HumanData data) {

    }

    @Override
    public void cleanup(Player player, RaceProfile profile) {

    }

}
