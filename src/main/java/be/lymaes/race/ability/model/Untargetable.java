package be.lymaes.race.ability.model;

import be.lymaes.race.ability.Targetable;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

public class Untargetable implements Targetable {

    private final Class<? extends Entity> ignoredEntityClass;

    public Untargetable(Class<? extends Entity> entity) {
        this.ignoredEntityClass = entity;
    }

    public void onTarget(EntityTargetLivingEntityEvent e) {
        if(!ignoredEntityClass.isInstance(e.getEntity())) return;
        if(e.getReason() == EntityTargetEvent.TargetReason.TARGET_ATTACKED_ENTITY) return;

        e.setCancelled(true);
    }

}
