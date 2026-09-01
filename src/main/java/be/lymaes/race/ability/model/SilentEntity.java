package be.lymaes.race.ability.model;

import org.bukkit.entity.Monster;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

public class SilentEntity implements Targetable {

    public void onTarget(EntityTargetLivingEntityEvent e) {
        if(!(e.getEntity() instanceof Monster)) return;
        if(e.getReason() == EntityTargetEvent.TargetReason.TARGET_ATTACKED_ENTITY) return;

        e.setCancelled(true);
    }

}
