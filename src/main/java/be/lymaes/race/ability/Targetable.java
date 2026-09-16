package be.lymaes.race.ability;

import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

public interface Targetable extends Ability {

    void onTarget(EntityTargetLivingEntityEvent e);

}
