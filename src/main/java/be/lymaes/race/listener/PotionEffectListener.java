package be.lymaes.race.listener;

import be.lymaes.race.Race;
import be.lymaes.race.RaceProfile;
import be.lymaes.race.manager.RaceManager;
import be.lymaes.race.model.IRace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffect;

public class PotionEffectListener implements Listener {

    private final RaceManager raceManager;

    public PotionEffectListener(Race plugin) {
        this.raceManager = plugin.getRaceManager();
    }

    @EventHandler
    public void onPotionReceive(EntityPotionEffectEvent e) {
        if(!(e.getEntity() instanceof Player player)) return;

        RaceProfile profile = raceManager.getProfile(player);
        if(profile == null) return;

        IRace model = raceManager.getRaceModel(profile.raceData.getRace());
        if(model == null) return;

        PotionEffect oldEffect = e.getOldEffect();
        if(oldEffect == null) return;

        PotionEffect modelEffect = model.getEffect(oldEffect.getType(), profile.raceData);
        if(modelEffect == null) return;

        if(isSameEffect(oldEffect, modelEffect)) return;

        EntityPotionEffectEvent.Cause cause = e.getCause();
        if(cause == EntityPotionEffectEvent.Cause.PLUGIN || cause == EntityPotionEffectEvent.Cause.DEATH) return;

        e.setCancelled(true);
    }

    private boolean isSameEffect(PotionEffect effect0, PotionEffect effect1) {
        return (effect0.isInfinite() == effect1.isInfinite())
                && (effect0.getAmplifier() == effect1.getAmplifier())
                && (effect0.hasParticles() == effect1.hasParticles())
                && (effect0.hasIcon() == effect1.hasIcon());
    }

}
