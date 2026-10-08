package be.lymaes.race.ability.model;

import be.lymaes.race.RaceProfile;
import be.lymaes.race.ability.Damager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class FrostAspect implements Damager {

    @Override
    public void onDamage(EntityDamageByEntityEvent e, Player damager, RaceProfile profile) {
        if(!damager.isSneaking()) return;
        if(!(e.getEntity() instanceof LivingEntity target)) return;

        int duration = target.getMaxFreezeTicks();

        target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, duration, 1));
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, duration, 2));
        target.setFreezeTicks(duration);
    }

}
