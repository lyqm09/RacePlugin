package be.lymaes.race.manager;

import be.lymaes.race.ability.Ability;
import be.lymaes.race.model.IRace;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AbilityManager {

    private final Map<String, Ability> register = new HashMap<>();

    public AbilityManager(Collection<IRace<?>> models) {
        for(IRace<?> model : models) {
            Map<String, Ability> abilities = model.getAbilities();

            if(abilities.isEmpty()) continue;

            register.putAll(abilities);
        }
    }

    public Ability getAbility(String key) {
        return register.get(key);
    }

}
