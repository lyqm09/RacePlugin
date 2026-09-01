package be.lymaes.race.model;

import be.lymaes.race.RaceProfile;
import be.lymaes.race.ability.*;
import be.lymaes.race.ability.model.*;
import be.lymaes.race.ability.model.Fireball;
import be.lymaes.race.data.OniData;
import be.lymaes.race.item.model.PrimordialOniBlood;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;

public class Oni implements IRace<OniData>, IRankable {

    private static final NamespacedKey STRENGTH = NamespacedKey.fromString("oni:strength");
    private static final NamespacedKey SPEED = NamespacedKey.fromString("oni:speed");
    private static final NamespacedKey HEALTH = NamespacedKey.fromString("oni:health");

    public Map<String, Ability> getAbilities() {
        double[] defendFactor = new double[] {0.00, 0.05, 0.10, 0.15, 0.20, 0.25, 0.50};
        return Map.of(
                AbilityKey.ONI.EXP, new OniExp(),
                AbilityKey.HYDROPHOBIA, new Hydrophobia(),
                AbilityKey.MEAT_EATER, new MeatEater(),
                AbilityKey.FIREBALL, new Fireball(),
                AbilityKey.SILENT_ENTITY, new SilentEntity(),
                AbilityKey.ONI.ABSORPTION, new Absorption(defendFactor)
        );
    }

    @Override
    public void addExpAbilities(RaceProfile profile) {
        profile.addAbility(AbilityKey.ONI.EXP);
    }

    public void removeExpAbilities(RaceProfile profile) {
        profile.removeAbility(AbilityKey.ONI.EXP);
    }

    private void applyAbilities(RaceProfile profile) {
        int rank = profile.raceData.getRank();

        profile.addAbility(AbilityKey.HYDROPHOBIA);
        profile.addAbility(AbilityKey.MEAT_EATER);
        profile.addAbility(AbilityKey.ONI.ABSORPTION);

        if(rank >= Rank.LIEUTENANT.rank) {
            profile.addAbility(AbilityKey.FIREBALL);
        }

        if(rank >= Rank.CAPTAIN.rank) {
            profile.addAbility(AbilityKey.SILENT_ENTITY);
        }
    }

    private void applyPerms(Player player, OniData data) {
        if(data.getRank() >= Rank.GENERAL.rank) {
            IRace.addPermission(player, PrimordialOniBlood.PERM_CRAFT);
        }
    }

    private void applyAttribute(Player player, OniData data) {
        Rank rank = Rank.fromRank(data.getRank());

        double multiplier = switch(rank) {
            case EVOLVED -> 0.05;
            case LIEUTENANT -> 0.10;
            case CAPTAIN -> 0.15;
            case COMMANDER -> 0.20;
            case LORD -> 0.25;
            case GENERAL -> 0.50;
            default -> 0.0;
        };

        IRace.replaceAttribute(player, Attribute.ATTACK_DAMAGE, STRENGTH, multiplier, AttributeModifier.Operation.ADD_SCALAR);
        IRace.replaceAttribute(player, Attribute.MOVEMENT_SPEED, SPEED, multiplier, AttributeModifier.Operation.ADD_SCALAR);

        if(data.getRank() >= Rank.LORD.rank) {
            AttributeInstance health = player.getAttribute(Attribute.MAX_HEALTH);
            if(health != null) {
                boolean isAbsent = true;
                for (AttributeModifier mod : health.getModifiers()) {
                    if (mod.getKey().equals(HEALTH)) {
                        isAbsent = false;
                        break;
                    }
                }

                if(isAbsent) {
                    IRace.replaceAttribute(player, Attribute.MAX_HEALTH, HEALTH, 20, AttributeModifier.Operation.ADD_NUMBER);
                    double ratio = player.getHealth()/health.getValue();
                    player.setHealth(ratio * health.getValue());
                }
            }
        }
    }

    private void applyEffect(Player player, OniData data) {
        if(data.getRank() >= Rank.COMMANDER.rank) {
            if(player.hasPotionEffect(PotionEffectType.REGENERATION)) {
                player.removePotionEffect(PotionEffectType.REGENERATION);
            }
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, PotionEffect.INFINITE_DURATION, 0, true, false, true));
        }
    }

//    private void applyOverlayPerks(Player player, OniData data) {
//        RaceManager raceManager = Race.getInstance().getRaceManager();
//        IRaceData overlay = data.getOverlay();
//        IRace modelOverlay = raceManager.getRaceModel(overlay.getRace());
//
//        modelOverlay.applyRacePerks(player, overlay);
//    }

    @Override
    public void applyRacePerks(Player player, RaceProfile profile, OniData data) {
        if(profile.raceData == data) {
            if(getExpRequired(data.getRank() + 1) != -1) {
                addExpAbilities(profile);
            } else {
                removeExpAbilities(profile);
            }
        }

        applyAbilities(profile);
        applyPerms(player, data);
        applyEffect(player, data);
        applyAttribute(player, data);

//        applyOverlayPerks(player, data);
    }

//    private void reapplyOverlayPerms(Player player, OniData data) {
//        RaceManager raceManager = Race.getInstance().getRaceManager();
//        IRaceData overlay = data.getOverlay();
//        IRace modelOverlay = raceManager.getRaceModel(overlay.getRace());
//
//        modelOverlay.reapplyPerms(player, overlay);
//    }

    @Override
    public void reapplyPerms(Player player, OniData data) {
        applyPerms(player, data);

//        reapplyOverlayPerms(player, data);
    }

//    private void reapplyOverlayEffect(Player player, OniData data) {
//        RaceManager raceManager = Race.getInstance().getRaceManager();
//        IRaceData overlay = data.getOverlay();
//        IRace modelOverlay = raceManager.getRaceModel(overlay.getRace());
//
//        modelOverlay.reapplyEffect(player, overlay);
//    }

    @Override
    public void reapplyEffect(Player player, OniData data) {
        applyEffect(player, data);

//        reapplyOverlayEffect(player, data);
    }

//    private void cleanupOverlayEffect(Player player) {
//        RaceManager raceManager = Race.getInstance().getRaceManager();
//        OniData data = (OniData) raceManager.getProfile(player).raceData;
//        IRaceData overlay = data.getOverlay();
//        IRace modelOverlay = raceManager.getRaceModel(overlay.getRace());
//
//        modelOverlay.cleanup(player);
//    }

    @Override
    public void cleanup(Player player, RaceProfile profile) {
        IRace.removeAttribute(player, Attribute.ATTACK_DAMAGE, STRENGTH);
        IRace.removeAttribute(player, Attribute.MOVEMENT_SPEED, SPEED);
        IRace.removeAttribute(player, Attribute.MAX_HEALTH, HEALTH);

        PotionEffect regeneration = player.getPotionEffect(PotionEffectType.REGENERATION);
        if(regeneration != null && regeneration.isInfinite()) {
            player.removePotionEffect(PotionEffectType.REGENERATION);
        }

        IRace.removePermission(player, PrimordialOniBlood.PERM_CRAFT);

        for(String key : getAbilities().keySet()) {
            profile.removeAbility(key);
        }

//        cleanupOverlayEffect(player);
    }

    @Override
    public String getRankName(int rank) {
        return Rank.fromRank(rank).name;
    }

    @Override
    public int getExpRequired(int rank) {
        if(rank < Rank.values().length) {
            return Rank.fromRank(rank).expRequired;
        }
        return -1;
    }

    @Override
    public boolean canRankUp(RaceProfile profile) {
        return profile.raceData.getRank() < Rank.GENERAL.rank;
    }

    public enum Rank {
        BEAST(0, "Bête", 0),
        EVOLVED(1, "Évolué", 40),
        LIEUTENANT(2, "Lieutenant", 200),
        CAPTAIN(3, "Capitaine", 1000),
        COMMANDER(4, "Commandant", 4000),
        LORD(5, "Seigneur", 10_000),
        GENERAL(6, "Général", 20_000);

        public final int rank;
        public final String name;
        public final int expRequired;

        Rank(int rank, String name, int expRequired) {
            this.rank = rank;
            this.name = name;
            this.expRequired = expRequired;
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
