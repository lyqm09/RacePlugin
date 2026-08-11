package be.lymaes.race.model;

import be.lymaes.race.Messager;
import be.lymaes.race.Race;
import be.lymaes.race.RaceProfile;
import be.lymaes.race.manager.RaceManager;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.raid.RaidFinishEvent;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.util.concurrent.ThreadLocalRandom;

public class Kitsune implements IRace {

    private static final NamespacedKey STRENGTH = NamespacedKey.fromString("kitsune:strength");
    private static final NamespacedKey SPEED = NamespacedKey.fromString("kitsune:speed");
    private static final NamespacedKey JUMP = NamespacedKey.fromString("kitsune:jump");
    private static final NamespacedKey FALL = NamespacedKey.fromString("kitsune:fall");

    private static final String TIME_KEY = "time_in_jungle_or_forest";

    public void task(RaceManager manager) {
        long currentTime = System.currentTimeMillis();

        for(RaceProfile profile : manager.getRaceProfiles(RaceType.KITSUNE)) {

            Biome currentBiome = profile.player.getWorld().getBiome(profile.player.getLocation());

            long time = profile.getTime(TIME_KEY);
            if(currentBiome == Biome.FOREST || currentBiome == Biome.JUNGLE) {
                if (currentTime - time >= 1000 * 60 * 60) {
                    if (ThreadLocalRandom.current().nextDouble() <= 0.01) {
                        rankUp(profile);
                    }
                    profile.putTime(TIME_KEY, currentTime);
                }
                else if(time <= 0) {
                    profile.putTime(TIME_KEY, currentTime);
                }
            }
            else {
                if(time != 0) {
                    profile.putTime(TIME_KEY, 0L);
                }
            }

        }
    }

    @EventHandler
    public void onRaidFinish(RaidFinishEvent e) {
        RaceManager manager = Race.getInstance().getRaceManager();
        for(Player player : e.getWinners()) {

            RaceProfile profile = manager.getProfile(player);
            IRace race = manager.getRaceModel(profile.race);

            if(race instanceof Kitsune) {
                rankUp(profile);
            }

        }
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e) {
        Player player = e.getPlayer();
        RaceProfile profile = Race.getInstance().getRaceManager().getProfile(player);
        if(profile.race != RaceType.KITSUNE)
            return;

        if(profile.getRank() < Rank.FIVE.rank)
            return;

        if(e.isSneaking()) {
            if(!player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, PotionEffect.INFINITE_DURATION, 1, true, false, true));
            }
        }
        else {
            PotionEffect effect = player.getPotionEffect(PotionEffectType.INVISIBILITY);
            if(effect != null && effect.isInfinite()) {
                player.removePotionEffect(PotionEffectType.INVISIBILITY);
            }
        }
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        RaceProfile profile = Race.getInstance().getRaceManager().getProfile(player);
        if(profile.race != RaceType.KITSUNE)
            return;

        if(profile.getRank() < Rank.FOUR.rank)
            return;

        if(!player.isSneaking())
            return;

        if(e.getItem() != null || e.getMaterial() != Material.AIR)
            return;

        if(e.getAction() != Action.LEFT_CLICK_AIR && e.getAction() != Action.LEFT_CLICK_BLOCK) // AIR doesn't work !
            return;

        if (DisguiseAPI.isDisguised(player) && DisguiseAPI.getDisguise(profile.player).getType() == DisguiseType.FOX) {
            DisguiseAPI.undisguiseToAll(player);
        }
        else {
            MobDisguise foxDisguise = new MobDisguise(DisguiseType.FOX);
            DisguiseAPI.disguiseToAll(player, foxDisguise);
        }

        e.setCancelled(true);
    }

    @Override
    public void cleanup(RaceProfile profile) {
        IRace.removeAttribute(profile.player, Attribute.ATTACK_DAMAGE, STRENGTH);
        IRace.removeAttribute(profile.player, Attribute.MOVEMENT_SPEED, SPEED);
        IRace.removeAttribute(profile.player, Attribute.JUMP_STRENGTH, JUMP);
        IRace.removeAttribute(profile.player, Attribute.SAFE_FALL_DISTANCE, FALL);

        PotionEffect nightVision = profile.player.getPotionEffect(PotionEffectType.NIGHT_VISION);
        if(nightVision != null && nightVision.isInfinite()) {
            profile.player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        }

        PotionEffect luck = profile.player.getPotionEffect(PotionEffectType.LUCK);
        if(luck != null && luck.isInfinite()) {
            profile.player.removePotionEffect(PotionEffectType.LUCK);
        }

        PotionEffect invisibility = profile.player.getPotionEffect(PotionEffectType.INVISIBILITY);
        if(invisibility != null && invisibility.isInfinite()) {
            profile.player.removePotionEffect(PotionEffectType.INVISIBILITY);
        }

        if (DisguiseAPI.isDisguised(profile.player) && DisguiseAPI.getDisguise(profile.player).getType() == DisguiseType.FOX) {
            DisguiseAPI.undisguiseToAll(profile.player);
        }
    }

    private void rankUp(RaceProfile profile) {
        profile.rankUp();

        Rank rank = Rank.fromRank(profile.getRank());

        Messager.sendRankupTitle(profile, rank.name);

        loadRank(profile);
    }

    private void loadAttribute(RaceProfile profile) {
        double speedMultiplier = 0.0;
        double strengthMultiplier = 0.0;
        double JBLvl = 0;

        switch(Rank.fromRank(profile.getRank())) {
            case ONE -> speedMultiplier = 0.05;
            case TWO -> {
                speedMultiplier = 0.10;
                strengthMultiplier = 5.0;
                JBLvl = 1.0;
            }
            case THREE -> {
                speedMultiplier = 0.15;
                strengthMultiplier = 10.0;
                JBLvl = 1.0;
            }
            case FOUR -> {
                speedMultiplier = 0.20;
                strengthMultiplier = 15.0;
                JBLvl = 1.0;
            }
            case FIVE -> {
                speedMultiplier = 0.25;
                strengthMultiplier = 20.0;
                JBLvl = 2.0;
            }
            case SIX -> {
                speedMultiplier = 0.30;
                strengthMultiplier = 25.0;
                JBLvl = 2;
            }
            case SEVEN -> {
                speedMultiplier = 0.40;
                strengthMultiplier = 30.0;
                JBLvl = 2;
            }
            case EIGHT -> {
                speedMultiplier = 50.0;
                strengthMultiplier = 35.0;
                JBLvl = 2;
            }
            case NINE -> {
                speedMultiplier = 100.0;
                strengthMultiplier = 50.0;
                JBLvl = 3;
            }
        }

        if(strengthMultiplier > 0.0) {
            IRace.replaceAttribute(profile.player, Attribute.ATTACK_DAMAGE, STRENGTH, strengthMultiplier, AttributeModifier.Operation.ADD_SCALAR);
        }
        if(speedMultiplier > 0.0) {
            IRace.replaceAttribute(profile.player, Attribute.MOVEMENT_SPEED, SPEED, speedMultiplier, AttributeModifier.Operation.ADD_SCALAR);
        }

        AttributeInstance jump = profile.player.getAttribute(Attribute.JUMP_STRENGTH);
        AttributeInstance fall = profile.player.getAttribute(Attribute.SAFE_FALL_DISTANCE);
        if(JBLvl > 0 && jump != null && fall != null) {
            for (AttributeModifier mod : jump.getModifiers()) {
                if (mod.getKey().equals(JUMP)) {
                    jump.removeModifier(mod);
                    break;
                }
            }
            for (AttributeModifier mod : fall.getModifiers()) {
                if (mod.getKey().equals(FALL)) {
                    fall.removeModifier(mod);
                    break;
                }
            }
            jump.addModifier(new AttributeModifier(JUMP, JBLvl * 0.1 , AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.ANY));
            fall.addModifier(new AttributeModifier(FALL, JBLvl, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.ANY));
        }
    }

    private void loadEffect(RaceProfile profile) {
        if(profile.player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
            profile.player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        }
        profile.player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, PotionEffect.INFINITE_DURATION, 1, true, false, false));


        if(profile.getRank() >= Rank.SIX.rank) {
            if(profile.player.hasPotionEffect(PotionEffectType.LUCK)) {
                profile.player.removePotionEffect(PotionEffectType.LUCK);
            }
            profile.player.addPotionEffect(new PotionEffect(PotionEffectType.LUCK, PotionEffect.INFINITE_DURATION, 1, true, false, false));
        }
    }

    @Override
    public void loadRank(RaceProfile profile) {
        loadEffect(profile);
        loadAttribute(profile);

        if(profile.getRank() >= Rank.NINE.rank) {
            profile.player.setAllowFlight(true);
        }
    }

    @Override
    public void reloadEffect(RaceProfile profile) {
        loadEffect(profile);
    }

    @Override
    public void addExp(RaceProfile profile, int n) {
    }

    @Override
    public String getRankName(int rank) {
        return Rank.fromRank(rank).name;
    }

    @Override
    public int getExpRequired(int rank) {
        return 0;
    }

    public enum Rank {
        ONE(0, "Une Queue"),
        TWO(1, "Deux Queues"),
        THREE(2, "Trois Queues"),
        FOUR(3, "Quatre Queues"),
        FIVE(4, "Cinq Queues"),
        SIX(5, "Six Queues"),
        SEVEN(6, "Sept Queues"),
        EIGHT(7, "Huit Queues"),
        NINE(8, "Neuf Queues");

        public final int rank;
        public final String name;

        Rank(int rank, String name) {
            this.rank = rank;
            this.name = name;
        }

        public static Rank fromRank(int n) {
            Rank[] ranks = Rank.values();
            if(n < ranks.length) {
                return ranks[n];
            }
            return ranks[0];
        }
    }
}
