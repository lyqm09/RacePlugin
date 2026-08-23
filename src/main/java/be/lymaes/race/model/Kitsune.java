package be.lymaes.race.model;

import be.lymaes.race.RaceProfile;
import be.lymaes.race.ability.Interactable;
import be.lymaes.race.ability.RaidFinisher;
import be.lymaes.race.ability.SneakyCharacter;
import be.lymaes.race.ability.Taskable;
import be.lymaes.race.data.IRaceData;
import be.lymaes.race.data.KitsuneData;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class Kitsune implements IRace, IRankable, Taskable, Interactable, RaidFinisher, SneakyCharacter {

    private static final double TOL = 0.02;

    private static final NamespacedKey STRENGTH = NamespacedKey.fromString("kitsune:strength");
    private static final NamespacedKey SPEED = NamespacedKey.fromString("kitsune:speed");
    private static final NamespacedKey JUMP = NamespacedKey.fromString("kitsune:jump");
    private static final NamespacedKey FALL = NamespacedKey.fromString("kitsune:fall");

    private static final Set<Biome> AVAILABLE_BIOMES = Set.of(
            Biome.JUNGLE,
            Biome.SPARSE_JUNGLE,
            Biome.BAMBOO_JUNGLE,
            Biome.BIRCH_FOREST,
            Biome.OLD_GROWTH_BIRCH_FOREST,
            Biome.DARK_FOREST,
            Biome.FOREST,
            Biome.FLOWER_FOREST,
            Biome.CHERRY_GROVE
    );

    @Override
    public void onTask(Player player, RaceProfile profile) {
        long currentTime = System.currentTimeMillis();

        KitsuneData data = ((KitsuneData)profile.raceData);
        long time = data.getTimeInForest();

        Biome currentBiome = player.getWorld().getBiome(player.getLocation());
        if(AVAILABLE_BIOMES.contains(currentBiome)) {
            if (currentTime - time >= 1000 * 60 * 60) {
                if (ThreadLocalRandom.current().nextDouble() <= TOL) {
                    profile.rankUp();
                }
                data.setTimeInForest(currentTime);
            }
            else if(time <= 0) {
                data.setTimeInForest(currentTime);
            }
        }
        else {
            if(time != 0) {
                data.setTimeInForest(0L);
            }
        }
    }

    @Override
    public void onRaidFinish(RaceProfile profile) {
        profile.rankUp();
    }

    @Override
    public void onToggleSneak(PlayerToggleSneakEvent e, Player player, IRaceData raceData) {
        if(raceData.getRank() < Rank.FIVE.rank)
            return;

        if(e.isSneaking()) {
            if(!player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, PotionEffect.INFINITE_DURATION, 0, true, false, true));
            }
        }
        else {
            PotionEffect effect = player.getPotionEffect(PotionEffectType.INVISIBILITY);
            if(effect != null && effect.isInfinite()) {
                player.removePotionEffect(PotionEffectType.INVISIBILITY);
            }
        }
    }

    @Override
    public void onInteract(PlayerInteractEvent e, Player player, IRaceData raceData) {
        if(raceData.getRank() < Rank.FOUR.rank)
            return;

        if(!player.isSneaking())
            return;

        if(e.getItem() != null || e.getMaterial() != Material.AIR)
            return;

        if(e.getAction() != Action.LEFT_CLICK_AIR && e.getAction() != Action.LEFT_CLICK_BLOCK) // AIR doesn't work !
            return;

        if (DisguiseAPI.isDisguised(player) && DisguiseAPI.getDisguise(player).getType() == DisguiseType.FOX) {
            DisguiseAPI.undisguiseToAll(player);
        }
        else {
            MobDisguise foxDisguise = new MobDisguise(DisguiseType.FOX);
            DisguiseAPI.disguiseToAll(player, foxDisguise);
        }

        e.setCancelled(true);
    }

    private void applyAttribute(RaceProfile profile) {
        Player player = profile.getPlayer();

        double speedMultiplier = 0.0;
        double strengthMultiplier = 0.0;
        double JBLvl = 0;

        switch(Rank.fromRank(profile.raceData.getRank())) {
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

    private void applyEffect(RaceProfile profile) {
        Player player = profile.getPlayer();

        if(player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        }
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, PotionEffect.INFINITE_DURATION, 0, true, false, true));


        if(profile.raceData.getRank() >= Rank.SIX.rank) {
            if(player.hasPotionEffect(PotionEffectType.LUCK)) {
                player.removePotionEffect(PotionEffectType.LUCK);
            }
            player.addPotionEffect(new PotionEffect(PotionEffectType.LUCK, PotionEffect.INFINITE_DURATION, 0, true, false, true));
        }
    }

    @Override
    public void applyRacePerks(RaceProfile profile) {
        applyEffect(profile);
        applyAttribute(profile);

        reapplyPerms(profile);
    }

    @Override
    public void reapplyPerms(RaceProfile profile) {
        if(profile.raceData.getRank() >= Rank.NINE.rank) {
            profile.getPlayer().setAllowFlight(true);
        }
    }

    @Override
    public void reapplyEffect(RaceProfile profile) {
        applyEffect(profile);
    }

    @Override
    public void cleanup(RaceProfile profile) {
        Player player = profile.getPlayer();

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
            if(n < ranks.length) {
                return ranks[n];
            }
            return ranks[0];
        }
    }
}
