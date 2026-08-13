package be.lymaes.race.model;

import be.lymaes.race.Messager;
import be.lymaes.race.Race;
import be.lymaes.race.RaceProfile;
import be.lymaes.race.gui.GUITypes;
import be.lymaes.race.item.FlyCharge;
import be.lymaes.race.item.IStaticItem;
import be.lymaes.race.item.RaceItem;
import be.lymaes.race.manager.ItemManager;
import be.lymaes.race.manager.RaceManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Tamashi implements IRace, ISubRaceable {

    public static final String PERM_HOME = "race.tamashi.home";

    private static final double DISTANCE = 200;

    private static final NamespacedKey STRENGTH = NamespacedKey.fromString("tamashi:strength");
    private static final NamespacedKey SPEED = NamespacedKey.fromString("tamashi:speed");

    private static final String TIME_KEY = "time_around_spawn";

    public void task(RaceManager manager) {
        long currentTime = System.currentTimeMillis();

        for(RaceProfile profile : manager.getRaceProfiles(RaceType.TAMASHI)) {

            Location currentLoc = profile.player.getLocation();
            Location spawnLoc = profile.player.getRespawnLocation();
            if(spawnLoc == null) {
                spawnLoc = profile.player.getWorld().getSpawnLocation();
            }

            long time = profile.getTime(TIME_KEY);

            if(currentLoc.distance(spawnLoc) > DISTANCE) {
                if(!profile.player.hasPotionEffect(PotionEffectType.HUNGER)) {
                    profile.player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, PotionEffect.INFINITE_DURATION, 0, true, false, true));
                }
                if(!profile.player.hasPotionEffect(PotionEffectType.WEAKNESS)) {
                    profile.player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, PotionEffect.INFINITE_DURATION, 0, true, false, true));
                }

                if(time != 0) {
                    profile.putTime(TIME_KEY, 0L);
                }
            }
            else {
                removeMalus(profile);

                if(time <= 0) {
                    profile.putTime(TIME_KEY, currentTime);
                }
                else if(currentTime - time >= 1000 * 60) {
                    addExp(profile, 1);
                    profile.putTime(TIME_KEY, currentTime);
                }
            }

            if((currentTime / 1000) % 60 == 0) {
                for (Player player : Race.getInstance().getServer().getOnlinePlayers()) {
                    if (player.equals(profile.player))
                        continue;

                    if (player.getLocation().distance(currentLoc) <= DISTANCE) {
                        addExp(profile, 1);
                    }

                    if (player.getLocation().distance(spawnLoc) <= DISTANCE) {
                        addExp(profile, 1);
                    }
                }
            }

        }
    }

    private void removeMalus(RaceProfile profile) {
        PotionEffect hunger = profile.player.getPotionEffect(PotionEffectType.HUNGER);
        if(hunger != null && hunger.isInfinite()) {
            profile.player.removePotionEffect(PotionEffectType.HUNGER);
        }
        PotionEffect weakness = profile.player.getPotionEffect(PotionEffectType.WEAKNESS);
        if(weakness != null && weakness.isInfinite()) {
            profile.player.removePotionEffect(PotionEffectType.WEAKNESS);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if(!(e.getEntity() instanceof Player player))
            return;

        RaceProfile profile = Race.getInstance().getRaceManager().getProfile(player);
        if(profile.race != RaceType.TAMASHI)
            return;

        if(profile.subRace == SubRace.EARTH.id) {

            double factor = switch (Rank.fromRank(profile.getRank())) {
                case EMBRYO -> 0.10;
                case CHILD -> 0.20;
                case ACCOMPLISHED -> 0.40;
                case HALF_GOD -> 0.60;
                case KAMI -> 0.80;
                case OKAMI -> 0.90;
            };

            e.setDamage(e.getFinalDamage() * (1.0 - factor));
        }
        else if(profile.subRace == SubRace.AIR.id) {
            if(e.getCause() != EntityDamageEvent.DamageCause.FALL)
                return;

            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageEntity(EntityDamageByEntityEvent e) {
        if(!(e.getDamager() instanceof Player player))
            return;

        RaceProfile profile = Race.getInstance().getRaceManager().getProfile(player);
        if(profile.race != RaceType.TAMASHI)
            return;

        Rank rank = Rank.fromRank(profile.getRank());

        if(profile.subRace == SubRace.FIRE.id) {

            int time = switch(rank) {
                case EMBRYO -> 1;
                case CHILD -> 2;
                case ACCOMPLISHED -> 5;
                case HALF_GOD -> 10;
                case KAMI -> 20;
                case OKAMI -> 30;
            };

            e.getEntity().setFireTicks(time * 20);
        }
        else if(profile.subRace == SubRace.WATER.id) {

            if(!player.isInWater())
                return;

            double factor = switch (rank) {
                case EMBRYO -> 0.05;
                case CHILD -> 0.10;
                case ACCOMPLISHED -> 0.20;
                case HALF_GOD -> 0.30;
                case KAMI -> 0.40;
                case OKAMI -> 0.50;
            };

            e.setDamage(e.getFinalDamage() * (1.0 + factor));
        }
    }

    @EventHandler
    public void onItemSwitch(PlayerItemHeldEvent e) {
        Player player = e.getPlayer();

        if(player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR)
            return;

        RaceProfile profile = Race.getInstance().getRaceManager().getProfile(player);
        if(profile.race != RaceType.TAMASHI)
            return;

        ItemStack oldItem = player.getInventory().getItem(e.getPreviousSlot());
        ItemStack newItem = player.getInventory().getItem(e.getNewSlot());

        if(profile.subRace == SubRace.AIR.id) {
            if(profile.getRank() < Rank.OKAMI.rank)
                return;

            ItemManager manager = Race.getInstance().getItemManager();

            if(newItem != null && manager.getItem(newItem) instanceof FlyCharge) {
                player.setAllowFlight(true);
            }
            else if(oldItem != null && manager.getItem(oldItem) instanceof FlyCharge) {
                player.setAllowFlight(false);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        RaceProfile profile = Race.getInstance().getRaceManager().getProfile(player);
        if(profile.race != RaceType.TAMASHI)
            return;

        ItemStack item = e.getItem();
        Rank rank = Rank.fromRank(profile.getRank());

        if(profile.subRace == SubRace.EARTH.id) {

            if(item == null || (item.getType() != Material.DIRT && item.getType() != Material.GRASS_BLOCK))
                return;

            if (e.getAction() != Action.RIGHT_CLICK_AIR)
                return;

            int amount = item.getAmount();
            if (amount > 1) {
                item.setAmount(amount - 1);
            } else {
                player.getInventory().setItemInMainHand(null);
            }

            player.setFoodLevel(player.getFoodLevel() + 1);
            player.setSaturation(player.getSaturation() + 0.5f);

            e.setCancelled(true);
        }
        else if(profile.subRace == SubRace.FIRE.id) {

            if(item == null || item.getType() != Material.BLAZE_POWDER)
                return;

            if (e.getAction() != Action.RIGHT_CLICK_AIR)
                return;

            int amount = item.getAmount();
            if (amount > 1) {
                item.setAmount(amount - 1);
            } else {
                player.getInventory().setItemInMainHand(null);
            }

            player.launchProjectile(Fireball.class);

            e.setCancelled(true);
        }
        else if(profile.subRace == SubRace.AIR.id) {

            if(!(Race.getInstance().getItemManager().getItem(item) instanceof FlyCharge))
                return;

            e.setCancelled(true);

            if(profile.getRank() >= Rank.OKAMI.rank)
                return;

            if(e.getAction() != Action.RIGHT_CLICK_AIR)
                return;

            int time = switch(rank) {
                case CHILD -> 10;
                case ACCOMPLISHED -> 20;
                case HALF_GOD -> 30;
                case KAMI -> 5 * 60;
                default -> 5;
            };

            if(!player.hasCooldown(item)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, time * 20, 0, true, false, true));
                player.setCooldown(item, (time + 10) * 20);
            }
            else if(player.hasPotionEffect(PotionEffectType.LEVITATION)) {
                player.removePotionEffect(PotionEffectType.LEVITATION);
                player.setCooldown(item, 10 * 20);
            }
        }
    }

    @Override
    public void cleanup(RaceProfile profile) {

        removeMalus(profile);

        // water
        PotionEffect conduit = profile.player.getPotionEffect(PotionEffectType.CONDUIT_POWER);
        if(conduit != null && conduit.isInfinite()) {
            profile.player.removePotionEffect(PotionEffectType.CONDUIT_POWER);
        }

        PotionEffect dolphinGrace = profile.player.getPotionEffect(PotionEffectType.DOLPHINS_GRACE);
        if(dolphinGrace != null && dolphinGrace.isInfinite()) {
            profile.player.removePotionEffect(PotionEffectType.DOLPHINS_GRACE);
        }

        // fire
        PotionEffect fireResistance = profile.player.getPotionEffect(PotionEffectType.FIRE_RESISTANCE);
        if(fireResistance != null && fireResistance.isInfinite()) {
            profile.player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
        }

        IRace.removeAttribute(profile.player, Attribute.ATTACK_DAMAGE, STRENGTH);

        // air
        if(Race.getInstance().getItemManager().getItem(profile.player.getInventory().getContents()[8]) instanceof IStaticItem) {
            profile.player.getInventory().setItem(8, null);
        }

        IRace.removeAttribute(profile.player, Attribute.MOVEMENT_SPEED, SPEED);

        profile.player.setAllowFlight(false);
    }


    private void rankUp(RaceProfile profile) {
        profile.rankUp();

        Rank rank = Rank.fromRank(profile.getRank());

        Messager.sendRankupTitle(profile, rank.name);
        // TODO ajouter a une queue de title

        loadRank(profile);
    }



    private void loadWaterEffect(RaceProfile profile) {
        int dolphinGraceLvl = switch(Rank.fromRank(profile.getRank())) {
            case EMBRYO, CHILD -> 1;
            case ACCOMPLISHED, HALF_GOD -> 2;
            case KAMI, OKAMI -> 3;
        };

        if(profile.player.hasPotionEffect(PotionEffectType.CONDUIT_POWER)) {
            profile.player.removePotionEffect(PotionEffectType.CONDUIT_POWER);
        }
        profile.player.addPotionEffect(new PotionEffect(PotionEffectType.CONDUIT_POWER, PotionEffect.INFINITE_DURATION, 0, true, false, true));


        if(profile.player.hasPotionEffect(PotionEffectType.DOLPHINS_GRACE)) {
            profile.player.removePotionEffect(PotionEffectType.DOLPHINS_GRACE);
        }
        profile.player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, PotionEffect.INFINITE_DURATION, dolphinGraceLvl-1, true, false, true));
    }

    private void loadWater(RaceProfile profile) {
        loadWaterEffect(profile);
    }

    private void loadFireAttribute(RaceProfile profile) {
        double multiplier = switch(Rank.fromRank(profile.getRank())) {
            case EMBRYO, CHILD -> 0.00;
            case ACCOMPLISHED -> 0.10;
            case HALF_GOD -> 0.20;
            case KAMI, OKAMI -> 0.30;
        };

        if(multiplier > 0.0) {
            IRace.replaceAttribute(profile.player, Attribute.ATTACK_DAMAGE, STRENGTH, multiplier, AttributeModifier.Operation.ADD_SCALAR);
        }
    }

    private void loadFireEffect(RaceProfile profile) {
        if(profile.player.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) {
            profile.player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
        }
        profile.player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, PotionEffect.INFINITE_DURATION, 0, true, false, true));
    }

    private void loadFire(RaceProfile profile) {
        loadFireEffect(profile);
        loadFireAttribute(profile);
    }

    private void loadAirAttribute(RaceProfile profile) {
        double multiplier = switch(Rank.fromRank(profile.getRank())) {
            case EMBRYO -> 0.05;
            case CHILD -> 0.10;
            case ACCOMPLISHED -> 0.20;
            case HALF_GOD -> 0.30;
            case KAMI -> 0.50;
            case OKAMI -> 1.00;
        };

        IRace.replaceAttribute(profile.player, Attribute.MOVEMENT_SPEED, SPEED, multiplier, AttributeModifier.Operation.ADD_SCALAR);
    }

    private void giveAirItem(RaceProfile profile) {
        ItemStack item = profile.player.getInventory().getContents()[8];
        if(!(Race.getInstance().getItemManager().getItem(item) instanceof FlyCharge)) {
            profile.player.getInventory().setItem(8, RaceItem.FLY_CHARGE.getItem());

            if(item != null && !profile.player.getInventory().addItem(item).isEmpty()) {
                profile.player.getWorld().dropItemNaturally(profile.player.getLocation(), item);
            }
        }
    }

    private void loadAir(RaceProfile profile) {
        loadAirAttribute(profile);
        giveAirItem(profile);
    }

    @Override
    public void loadRank(RaceProfile profile) {
        switch(SubRace.fromId(profile.subRace)) {
            case WATER -> loadWater(profile);
            case FIRE -> loadFire(profile);
            case AIR -> loadAir(profile);
        }
    }

    @Override
    public void reloadEffect(RaceProfile profile) {
        switch(SubRace.fromId(profile.subRace)) {
            case WATER -> loadWaterEffect(profile);
            case FIRE -> loadFireEffect(profile);
            case AIR -> giveAirItem(profile);
        }
    }

    public void checkRankup(RaceProfile profile) {
        if(profile.getRank() < Rank.values().length-1) {

            Rank rank = Rank.fromRank(profile.getRank() + 1);

            int required = rank.expRequired;
            if (profile.getExp() < required)
                return;

            profile.subExp(required);
            rankUp(profile);
            checkRankup(profile);
        }
    }

    @Override
    public void addExp(RaceProfile profile, int n) {
        profile.addExp(n);
        checkRankup(profile);
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
        return 0;
    }

    @Override
    public GUITypes getSubRaceGUI() {
        return GUITypes.TAMASHI;
    }

    @Override
    public String getSubraceName(int subrace) {
        return SubRace.fromId(subrace).name;
    }

    public enum Rank {
        EMBRYO(0, "Embryon", 0),
        CHILD(1, "Enfant", 400),
        ACCOMPLISHED(2, "Accompli", 4000),
        HALF_GOD(3, "Semi Divin", 10_000),
        KAMI(4, "Kami", 20_000),
        OKAMI(5, "Okami", 40_000);

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
            if(n < ranks.length) {
                return ranks[n];
            }
            return ranks[0];
        }
    }

    public enum SubRace {
        WATER(0, "Eau", Material.BLUE_DYE),
        EARTH(1, "Terre", Material.ORANGE_DYE),
        FIRE(2, "Feu", Material.RED_DYE),
        AIR(3, "Air", Material.GREEN_DYE);

        public final int id;
        public final String name;
        public final Material icon;

        SubRace(int id, String name, Material icon) {
            this.id = id;
            this.name = name;
            this.icon = icon;
        }

        public ItemStack getItem() {
            ItemStack item = new ItemStack(icon);
            ItemMeta meta = item.getItemMeta();

            if(meta == null)
                return item;

            meta.setDisplayName(ChatColor.WHITE + name);

            item.setItemMeta(meta);
            return item;
        }

        public static SubRace fromId(int id) {
            SubRace[] sub = SubRace.values();
            if(id < sub.length) {
                return sub[id];
            }
            return sub[0];
        }

    }
}
