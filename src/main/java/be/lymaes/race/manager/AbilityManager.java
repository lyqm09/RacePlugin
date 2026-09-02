package be.lymaes.race.manager;

import be.lymaes.race.ability.Ability;
import be.lymaes.race.ability.AbilityKey;
import be.lymaes.race.model.IRace;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

public class AbilityManager {

    private final Map<AbilityKey, Ability> register = new EnumMap<>(AbilityKey.class);

    public AbilityManager(Collection<IRace<?>> models) {
        for(IRace<?> model : models) {
            Map<AbilityKey, Ability> abilities = model.getAbilities();

            if(abilities.isEmpty()) continue;

            register.putAll(abilities);
        }
    }

    public Ability getAbility(AbilityKey key) {
        return register.get(key);
    }

}
