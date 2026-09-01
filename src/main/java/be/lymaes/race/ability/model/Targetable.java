package be.lymaes.race.ability.model;

import be.lymaes.race.ability.Ability;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

public interface Targetable extends Ability {

    void onTarget(EntityTargetLivingEntityEvent e);

}
