package be.lymaes.race.model;

import be.lymaes.race.Race;
import be.lymaes.race.RaceProfile;
import be.lymaes.race.ability.*;
import be.lymaes.race.data.IRaceData;
import be.lymaes.race.data.TamashiData;
import be.lymaes.race.gui.GUITypes;
import be.lymaes.race.item.FlyCharge;
import be.lymaes.race.item.IStaticItem;
import be.lymaes.race.item.RaceItem;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.bukkit.inventory.meta.components.consumable.ConsumableComponent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Tamashi implements IRace, ISubRaceable, IRankable, Taskable, Damageable, Damager, Interactable, Helder {

    public static final String PERM_HOME = "race.tamashi.home";
    public static final String PERM_FLY_CHARGE = "race.tamashi.air.fly_charge";

    private static final double DISTANCE = 200;

    private static final NamespacedKey STRENGTH = NamespacedKey.fromString("tamashi:strength");
    private static final NamespacedKey SPEED = NamespacedKey.fromString("tamashi:speed");

    @Override
    public void onTask(Player player, RaceProfile profile) {
        long currentTime = System.currentTimeMillis();

        Location playerLoc = player.getLocation();
        TamashiData data = ((TamashiData)profile.raceData);
        Location home = data.getHome();

        int exp = 0;
        boolean isAlone = true;

        for (Player other : Bukkit.getOnlinePlayers()) {
            if (other.equals(player))
                continue;

            if (other.getLocation().distance(playerLoc) <= DISTANCE) {
                isAlone = false;
                exp++;
            }

            if (other.getLocation().distance(home) <= DISTANCE) {
                exp++;
            }
        }

        if(playerLoc.distance(home) > DISTANCE) {
            if(isAlone) {
                if (!player.hasPotionEffect(PotionEffectType.HUNGER)) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 3 * 20, 0, true, false, true));
                }
                if (!player.hasPotionEffect(PotionEffectType.WEAKNESS)) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 3 * 20, 0, true, false, true));
                }
            }
        }
        else {
            exp++;
        }

        if((currentTime / 1000) % 60 == 0 && exp > 0) {
            profile.addExp(exp);
        }

    }

    @Override
    public void onSwitchOff(IRaceData raceData, ItemStack item) {
        if(raceData.getSubrace() == SubRace.EARTH.id) {
            if(item == null || item.getType() != Material.DIRT) return;

            ItemMeta meta = item.getItemMeta();
            if(meta == null) return;

            meta.setConsumable(null);
            meta.setFood(null);

            item.setItemMeta(meta);
        }
    }

    @Override
    public void onSwitchOn(IRaceData raceData, ItemStack item) {
        if(raceData.getSubrace() == SubRace.EARTH.id) {
            if(item == null || item.getType() != Material.DIRT) return;

            ItemMeta meta = item.getItemMeta();
            if(meta == null) return;

            ConsumableComponent consumable = meta.getConsumable();
            consumable.setAnimation(ConsumableComponent.Animation.EAT);
            consumable.setConsumeSeconds(1.6f);
            consumable.setSound(Sound.ENTITY_GENERIC_EAT);
            meta.setConsumable(consumable);

            FoodComponent food = meta.getFood();
            food.setNutrition(1);
            food.setSaturation(0.5f);
            food.setCanAlwaysEat(true);
            meta.setFood(food);

            item.setItemMeta(meta);
        }
    }

    @Override
    public void onInteract(PlayerInteractEvent e, Player player, IRaceData raceData) {
        ItemStack item = e.getItem();

        if(raceData.getSubrace() == SubRace.FIRE.id) {
            launchFireball(e, player, item);
        }
        else if(raceData.getSubrace() == SubRace.AIR.id) {
            useFlyCharge(e, player, item, raceData);
        }
    }

    private void launchFireball(PlayerInteractEvent e, Player player, ItemStack item) {
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

    private void useFlyCharge(PlayerInteractEvent e, Player player, ItemStack item, IRaceData raceData) {
        if(!(Race.getInstance().getItemManager().getItem(item) instanceof FlyCharge))
            return;

        e.setCancelled(true);

        if(raceData.getRank() >= Rank.OKAMI.rank)
            return;

        if(e.getAction() != Action.RIGHT_CLICK_AIR)
            return;

        Rank rank = Rank.fromRank(raceData.getRank());
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

    @Override
    public void onAttack(EntityDamageByEntityEvent e, Player player, IRaceData raceData) {
        Rank rank = Rank.fromRank(raceData.getRank());

        if(raceData.getSubrace() == SubRace.FIRE.id) {

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
        else if(raceData.getSubrace() == SubRace.WATER.id) {

            if(!player.isInWater())
                return;

            double factor = switch(rank) {
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

    @Override
    public void onDefend(EntityDamageEvent e, Player player, IRaceData raceData) {
        if(raceData.getSubrace() == SubRace.EARTH.id) {

            double factor = switch (Rank.fromRank(raceData.getRank())) {
                case EMBRYO -> 0.10;
                case CHILD -> 0.20;
                case ACCOMPLISHED -> 0.40;
                case HALF_GOD -> 0.60;
                case KAMI -> 0.80;
                case OKAMI -> 0.90;
            };

            e.setDamage(e.getFinalDamage() * (1.0 - factor));
        }
        else if(raceData.getSubrace() == SubRace.AIR.id) {
            if(e.getCause() != EntityDamageEvent.DamageCause.FALL)
                return;

            e.setCancelled(true);
        }
    }

    private void applyWaterEffect(RaceProfile profile) {
        Player player = profile.getPlayer();

        int dolphinGraceLvl = switch(Rank.fromRank(profile.raceData.getRank())) {
            case EMBRYO, CHILD -> 1;
            case ACCOMPLISHED, HALF_GOD -> 2;
            case KAMI, OKAMI -> 3;
        };

        if(player.hasPotionEffect(PotionEffectType.CONDUIT_POWER)) {
            player.removePotionEffect(PotionEffectType.CONDUIT_POWER);
        }
        player.addPotionEffect(new PotionEffect(PotionEffectType.CONDUIT_POWER, PotionEffect.INFINITE_DURATION, 0, true, false, true));


        if(player.hasPotionEffect(PotionEffectType.DOLPHINS_GRACE)) {
            player.removePotionEffect(PotionEffectType.DOLPHINS_GRACE);
        }
        player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, PotionEffect.INFINITE_DURATION, dolphinGraceLvl-1, true, false, true));
    }

    private void applyWater(RaceProfile profile) {
        applyWaterEffect(profile);
    }

    private void applyFireAttribute(RaceProfile profile) {
        double multiplier = switch(Rank.fromRank(profile.raceData.getRank())) {
            case EMBRYO, CHILD -> 0.00;
            case ACCOMPLISHED -> 0.10;
            case HALF_GOD -> 0.20;
            case KAMI, OKAMI -> 0.30;
        };

        if(multiplier > 0.0) {
            IRace.replaceAttribute(profile.getPlayer(), Attribute.ATTACK_DAMAGE, STRENGTH, multiplier, AttributeModifier.Operation.ADD_SCALAR);
        }
    }

    private void applyFireEffect(RaceProfile profile) {
        Player player = profile.getPlayer();

        if(player.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) {
            player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
        }
        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, PotionEffect.INFINITE_DURATION, 0, true, false, true));
    }

    private void applyFire(RaceProfile profile) {
        applyFireEffect(profile);
        applyFireAttribute(profile);
    }

    private void applyAirPermission(RaceProfile profile) {
        Player player = profile.getPlayer();

        if(profile.raceData.getRank() >= Rank.OKAMI.rank) {
            if(!player.hasPermission(PERM_FLY_CHARGE)) {
                IRace.addPermission(player, PERM_FLY_CHARGE);
            }
        }
    }

    private void applyAirAttribute(RaceProfile profile) {
        double multiplier = switch(Rank.fromRank(profile.raceData.getRank())) {
            case EMBRYO -> 0.05;
            case CHILD -> 0.10;
            case ACCOMPLISHED -> 0.20;
            case HALF_GOD -> 0.30;
            case KAMI -> 0.50;
            case OKAMI -> 1.00;
        };

        IRace.replaceAttribute(profile.getPlayer(), Attribute.MOVEMENT_SPEED, SPEED, multiplier, AttributeModifier.Operation.ADD_SCALAR);
    }

    private void giveAirItem(RaceProfile profile) {
        Player player = profile.getPlayer();

        ItemStack item = player.getInventory().getContents()[8];
        if(!(Race.getInstance().getItemManager().getItem(item) instanceof FlyCharge)) {
            player.getInventory().setItem(8, RaceItem.FLY_CHARGE.getItem());

            if(item != null && !player.getInventory().addItem(item).isEmpty()) {
                player.getWorld().dropItemNaturally(player.getLocation(), item);
            }
        }
    }

    private void applyAir(RaceProfile profile) {
        applyAirPermission(profile);
        applyAirAttribute(profile);
        giveAirItem(profile);
    }

    @Override
    public void applyRacePerks(RaceProfile profile) {
        reapplyPerms(profile);

        switch(SubRace.fromId(profile.raceData.getSubrace())) {
            case WATER -> applyWater(profile);
            case FIRE -> applyFire(profile);
            case AIR -> applyAir(profile);
        }
    }

    @Override
    public void reapplyPerms(RaceProfile profile) {
        Player player = profile.getPlayer();
        if(!player.hasPermission(PERM_HOME)) {
            IRace.addPermission(player, PERM_HOME);
        }
    }

    @Override
    public void reapplyEffect(RaceProfile profile) {
        switch(SubRace.fromId(profile.raceData.getSubrace())) {
            case WATER -> applyWaterEffect(profile);
            case FIRE -> applyFireEffect(profile);
            case AIR -> giveAirItem(profile);
        }
    }

    @Override
    public void cleanup(RaceProfile profile) {
        Player player = profile.getPlayer();

        IRace.removePermission(player, PERM_HOME);

        // water
        PotionEffect conduit = player.getPotionEffect(PotionEffectType.CONDUIT_POWER);
        if(conduit != null && conduit.isInfinite()) {
            player.removePotionEffect(PotionEffectType.CONDUIT_POWER);
        }

        PotionEffect dolphinGrace = player.getPotionEffect(PotionEffectType.DOLPHINS_GRACE);
        if(dolphinGrace != null && dolphinGrace.isInfinite()) {
            player.removePotionEffect(PotionEffectType.DOLPHINS_GRACE);
        }

        // fire
        PotionEffect fireResistance = player.getPotionEffect(PotionEffectType.FIRE_RESISTANCE);
        if(fireResistance != null && fireResistance.isInfinite()) {
            player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
        }

        IRace.removeAttribute(player, Attribute.ATTACK_DAMAGE, STRENGTH);

        // air
        if(Race.getInstance().getItemManager().getItem(player.getInventory().getContents()[8]) instanceof IStaticItem) {
            player.getInventory().setItem(8, null);
        }

        IRace.removeAttribute(player, Attribute.MOVEMENT_SPEED, SPEED);

        IRace.removePermission(player, PERM_FLY_CHARGE);
        player.setAllowFlight(false);
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
