package be.lymaes.race.ability.model;

import be.lymaes.race.ability.Ability;

public class EmptyAbility implements Ability {

    public static final Ability INSTANCE = new EmptyAbility();

    private EmptyAbility() {
    }
}
