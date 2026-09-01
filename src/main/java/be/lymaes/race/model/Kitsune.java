package be.lymaes.race.model;

import be.lymaes.race.RaceProfile;
import be.lymaes.race.ability.*;
import be.lymaes.race.ability.model.Disguise;
import be.lymaes.race.ability.model.Invisibility;
import be.lymaes.race.ability.model.KitsuneRaidExp;
import be.lymaes.race.ability.model.KitsuneZoneExp;
import be.lymaes.race.data.KitsuneData;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;

public class Kitsune implements IRace<KitsuneData>, IRankable {

    public static final double TOLERENCE = 0.02;

    private static final NamespacedKey STRENGTH = NamespacedKey.fromString("kitsune:strength");
    private static final NamespacedKey SPEED = NamespacedKey.fromString("kitsune:speed");
    private static final NamespacedKey JUMP = NamespacedKey.fromString("kitsune:jump");
    private static final NamespacedKey FALL = NamespacedKey.fromString("kitsune:fall");

    public Map<String, Ability> getAbilities() {
        return Map.of(
                AbilityKey.KITSUNE.RAID_EXP, new KitsuneRaidExp(),
                AbilityKey.KITSUNE.ZONE_EXP, new KitsuneZoneExp(),

                AbilityKey.FOX_DISGUISE, new Disguise(DisguiseType.FOX),
                AbilityKey.INVISIBILITY, new Invisibility()
        );
    }

    @Override
    public void addExpAbilities(RaceProfile profile) {
        profile.addAbility(AbilityKey.KITSUNE.RAID_EXP);
        profile.addAbility(AbilityKey.KITSUNE.ZONE_EXP);
    }

    public void removeExpAbilities(RaceProfile profile) {
        profile.removeAbility(AbilityKey.KITSUNE.RAID_EXP);
        profile.removeAbility(AbilityKey.KITSUNE.ZONE_EXP);
    }

    private void applyAbilities(RaceProfile profile) {
        int rank = profile.raceData.getRank();

        if(rank >= Rank.FOUR.rank) {
            profile.addAbility(AbilityKey.FOX_DISGUISE);
        }
        if(rank >= Rank.FIVE.rank) {
            profile.addAbility(AbilityKey.INVISIBILITY);
        }
    }

    private void applyAttribute(Player player, KitsuneData data) {

        double speedMultiplier = 0.0;
        double strengthMultiplier = 0.0;
        double JBLvl = 0;

        switch(Rank.fromRank(data.getRank())) {
            case ONE -> speedMultiplier = 0.05;
            case TWO -> {
                speedMultiplier = 0.10;
                strengthMultiplier = 0.05;
                JBLvl = 1.0;
            }
            case THREE -> {
                speedMultiplier = 0.15;
                strengthMultiplier = 0.10;
                JBLvl = 1.0;
            }
            case FOUR -> {
                speedMultiplier = 0.20;
                strengthMultiplier = 0.15;
                JBLvl = 1.0;
            }
            case FIVE -> {
                speedMultiplier = 0.25;
                strengthMultiplier = 0.20;
                JBLvl = 2.0;
            }
            case SIX -> {
                speedMultiplier = 0.30;
                strengthMultiplier = 0.25;
                JBLvl = 2;
            }
            case SEVEN -> {
                speedMultiplier = 0.40;
                strengthMultiplier = 0.30;
                JBLvl = 2;
            }
            case EIGHT -> {
                speedMultiplier = 0.50;
                strengthMultiplier = 0.35;
                JBLvl = 2;
            }
            case NINE -> {
                speedMultiplier = 1.0;
                strengthMultiplier = 0.50;
                JBLvl = 3;
            }
        }

        if(strengthMultiplier > 0.0) {
            IRace.replaceAttribute(player, Attribute.ATTACK_DAMAGE, STRENGTH, strengthMultiplier, AttributeModifier.Operation.ADD_SCALAR);
        }
        if(speedMultiplier > 0.0) {
            IRace.replaceAttribute(player, Attribute.MOVEMENT_SPEED, SPEED, speedMultiplier, AttributeModifier.Operation.ADD_SCALAR);
        }

        AttributeInstance jump = player.getAttribute(Attribute.JUMP_STRENGTH);
        AttributeInstance fall = player.getAttribute(Attribute.SAFE_FALL_DISTANCE);
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

    private void applyEffect(Player player, KitsuneData data) {

        if(player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        }
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, PotionEffect.INFINITE_DURATION, 0, true, false, true));


        if(data.getRank() >= Rank.SIX.rank) {
            if(player.hasPotionEffect(PotionEffectType.LUCK)) {
                player.removePotionEffect(PotionEffectType.LUCK);
            }
            player.addPotionEffect(new PotionEffect(PotionEffectType.LUCK, PotionEffect.INFINITE_DURATION, 0, true, false, true));
        }
    }

    @Override
    public void applyRacePerks(Player player, RaceProfile profile, KitsuneData data) {
        if(profile.raceData == data) {
            if(getExpRequired(data.getRank() + 1) != -1) {
                addExpAbilities(profile);
            } else {
                removeExpAbilities(profile);
            }
        }

        applyAbilities(profile);
        applyEffect(player, data);
        applyAttribute(player, data);

        reapplyPerms(player, data);
    }

    @Override
    public void reapplyPerms(Player player, KitsuneData data) {
        if(data.getRank() >= Rank.NINE.rank) {
            player.setAllowFlight(true);
        } else if(player.getAllowFlight()) {
            player.setAllowFlight(false);
        }
    }

    @Override
    public void reapplyEffect(Player player, KitsuneData data) {
        applyEffect(player, data);

        if(data.getRank() >= Rank.NINE.rank) {
            player.setAllowFlight(true);
        }
    }

    @Override
    public void cleanup(Player player, RaceProfile profile) {

        IRace.removeAttribute(player, Attribute.ATTACK_DAMAGE, STRENGTH);
        IRace.removeAttribute(player, Attribute.MOVEMENT_SPEED, SPEED);
        IRace.removeAttribute(player, Attribute.JUMP_STRENGTH, JUMP);
        IRace.removeAttribute(player, Attribute.SAFE_FALL_DISTANCE, FALL);

        PotionEffect nightVision = player.getPotionEffect(PotionEffectType.NIGHT_VISION);
        if(nightVision != null && nightVision.isInfinite()) {
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        }

        PotionEffect luck = player.getPotionEffect(PotionEffectType.LUCK);
        if(luck != null && luck.isInfinite()) {
            player.removePotionEffect(PotionEffectType.LUCK);
        }

        PotionEffect invisibility = player.getPotionEffect(PotionEffectType.INVISIBILITY);
        if(invisibility != null && invisibility.isInfinite()) {
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
        }

        if (DisguiseAPI.isDisguised(player) && DisguiseAPI.getDisguise(player).getType() == DisguiseType.FOX) {
            DisguiseAPI.undisguiseToAll(player);
        }

        for(String key : getAbilities().keySet()) {
            profile.removeAbility(key);
        }
    }

    @Override
    public String getRankName(int rank) {
        return Rank.fromRank(rank).name;
    }

    @Override
    public int getExpRequired(int rank) {
        if(rank < Rank.values().length) {
            return 0;
        }
        return -1;
    }

    @Override
    public boolean canRankUp(RaceProfile profile) {
        return false;
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
            if(n < 0) {
                return ranks[0];
            }
            else if(n < ranks.length) {
                return ranks[n];
            }
            return ranks[ranks.length-1];
        }
    }
}
