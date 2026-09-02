package be.lymaes.race.model;

import be.lymaes.race.Race;
import be.lymaes.race.RaceProfile;
import be.lymaes.race.ability.*;
import be.lymaes.race.ability.model.*;
import be.lymaes.race.ability.model.Fireball;
import be.lymaes.race.data.IRaceData;
import be.lymaes.race.data.OniData;
import be.lymaes.race.manager.RaceManager;
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

    public Map<AbilityKey, Ability> getAbilities() {
        double[] defendFactor = new double[] {0.00, 0.05, 0.10, 0.15, 0.20, 0.25, 0.50};
        return Map.of(
                AbilityKey.ONI_EXP, new OniExp(),
                AbilityKey.HYDROPHOBIA, new Hydrophobia(),
                AbilityKey.MEAT_EATER, new MeatEater(),
                AbilityKey.FIREBALL, new Fireball(),
                AbilityKey.SILENT_ENTITY, new SilentEntity(),
                AbilityKey.ONI_ABSORPTION, new Absorption(defendFactor),
                AbilityKey.CRAFT_PRIMORDIAL_ONI_BLOOD, EmptyAbility.INSTANCE
        );
    }

    @Override
    public void addExpAbilities(RaceProfile profile) {
        profile.addAbility(AbilityKey.ONI_EXP);
    }

    public void removeExpAbilities(RaceProfile profile) {
        profile.removeAbility(AbilityKey.ONI_EXP);
    }

    private void applyAbilities(RaceProfile profile, OniData data) {
        int rank = data.getRank();

        profile.addAbility(AbilityKey.HYDROPHOBIA);
        profile.addAbility(AbilityKey.MEAT_EATER);
        profile.addAbility(AbilityKey.ONI_ABSORPTION);

        if(rank >= Rank.LIEUTENANT.rank) {
            profile.addAbility(AbilityKey.FIREBALL);
        }

        if(rank >= Rank.CAPTAIN.rank) {
            profile.addAbility(AbilityKey.SILENT_ENTITY);
        }

        if(rank >= Rank.GENERAL.rank) {
            profile.addAbility(AbilityKey.CRAFT_PRIMORDIAL_ONI_BLOOD);
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

    private void applyOverlayPerks(Player player, RaceProfile profile, OniData data) {
        IRaceData overlay = data.getOverlay();
        if(overlay == null) return;

        RaceManager raceManager = Race.getInstance().getRaceManager();
        IRace modelOverlay = raceManager.getRaceModel(overlay.getRace());

        modelOverlay.applyRacePerks(player, profile, overlay);
    }

    @Override
    public void applyRacePerks(Player player, RaceProfile profile, OniData data) {
        if(profile.raceData == data) {
            if(getExpRequired(data.getRank() + 1) != -1) {
                addExpAbilities(profile);
            } else {
                removeExpAbilities(profile);
            }
        }

        applyAbilities(profile, data);
        applyEffect(player, data);
        applyAttribute(player, data);

        applyOverlayPerks(player, profile, data);
    }

    private void reapplyOverlayEffect(Player player, OniData data) {
        IRaceData overlay = data.getOverlay();
        if(overlay == null) return;

        RaceManager raceManager = Race.getInstance().getRaceManager();
        IRace modelOverlay = raceManager.getRaceModel(overlay.getRace());

        modelOverlay.reapplyEffect(player, overlay);
    }

    @Override
    public void reapplyEffect(Player player, OniData data) {
        applyEffect(player, data);
        reapplyOverlayEffect(player, data);
    }

    private void cleanupOverlayEffect(Player player, RaceProfile profile) {
        IRaceData overlay = ((OniData) profile.raceData).getOverlay();
        if(overlay == null) return;

        RaceManager raceManager = Race.getInstance().getRaceManager();
        IRace modelOverlay = raceManager.getRaceModel(overlay.getRace());

        modelOverlay.cleanup(player, profile);
    }

    @Override
    public void cleanup(Player player, RaceProfile profile) {
        cleanupOverlayEffect(player, profile);

        IRace.removeAttribute(player, Attribute.ATTACK_DAMAGE, STRENGTH);
        IRace.removeAttribute(player, Attribute.MOVEMENT_SPEED, SPEED);
        IRace.removeAttribute(player, Attribute.MAX_HEALTH, HEALTH);

        PotionEffect regeneration = player.getPotionEffect(PotionEffectType.REGENERATION);
        if(regeneration != null && regeneration.isInfinite()) {
            player.removePotionEffect(PotionEffectType.REGENERATION);
        }

        for(AbilityKey key : getAbilities().keySet()) {
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
            return Rank.fromRank(rank).expRequired;
        }
        return -1;
    }

    @Override
    public boolean canRankUp(RaceProfile profile) {
        return profile.raceData.getRank() + 1 < Rank.GENERAL.rank;
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
