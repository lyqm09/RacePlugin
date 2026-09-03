package be.lymaes.race.ability.model;

import be.lymaes.race.Race;
import be.lymaes.race.RaceProfile;
import be.lymaes.race.ability.ItemDropping;
import be.lymaes.race.ability.Taskable;
import be.lymaes.race.data.IRaceData;
import be.lymaes.race.data.KitsuneData;
import be.lymaes.race.data.OniData;
import be.lymaes.race.model.IRace;
import be.lymaes.race.model.Kitsune;
import be.lymaes.race.util.SimpleBlockLocation;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class Offering extends PermAbility implements Taskable, ItemDropping {

    private static final Material BLOCK_TYPE = Material.GOLD_BLOCK;
    private static final int LUCK_LVL = 3; // Luck IV

    private final Set<Item> trackedItem = new HashSet<>();
    private final Map<SimpleBlockLocation, Material> kamiBlock = new HashMap<>();

    public Offering(String permission) {
        super(permission);
    }

    @Override
    public void run(Player player, RaceProfile profile, IRaceData data, long currentTime) {
        KitsuneData kitsuneData = getKitsuneData(data);
        if(kitsuneData == null) return;

        PotionEffect luck = player.getPotionEffect(PotionEffectType.LUCK);
        if(luck == null) return;
        if(luck.isInfinite()) return;

        IRace<? extends IRaceData> irace = Race.getInstance().getRaceManager().getRaceModel(kitsuneData.getRace());
        if(!(irace instanceof Kitsune kitsune)) return;

        kitsune.reapplyEffect(player, kitsuneData);
    }

    @Override
    public void terminate() {
        for(SimpleBlockLocation location : kamiBlock.keySet()) {
            removeKamiBlock(location);
        }
    }

    @Override
    public void onDrop(PlayerDropItemEvent e) {
        if(e.getItemDrop().getItemStack().getType() != Material.DIAMOND) return;
        trackedItem.add(e.getItemDrop());
    }

    public void trackDiamonds() {
        Map<UUID, Integer> players = new HashMap<>();

        if(trackedItem.isEmpty()) return;

        Iterator<Item> iterator = trackedItem.iterator();
        while(iterator.hasNext()) {
            Item item = iterator.next();

            if(!item.isValid() || item.isDead()) {
                iterator.remove();
                continue;
            }

            if(item.isOnGround()) {
                UUID uuid = item.getThrower();
                if(uuid == null) {
                    iterator.remove();
                    continue;
                }

                Block block = item.getLocation().getBlock().getRelative(BlockFace.DOWN);
                SimpleBlockLocation blockLoc = new SimpleBlockLocation(block.getWorld().getUID(), block.getX(), block.getY(), block.getZ());
                if(kamiBlock.containsKey(blockLoc)) {
                    players.put(uuid, players.getOrDefault(uuid, 0) + 1);
                    item.remove();
                }

                iterator.remove();
            }
        }

        if (players.isEmpty()) return;

        for(Map.Entry<UUID, Integer> entry : players.entrySet()) {
            UUID uuid = entry.getKey();
            Player player = Bukkit.getPlayer(uuid);
            if(player == null || !player.isOnline()) continue;

            PotionEffect luck = player.getPotionEffect(PotionEffectType.LUCK);
            int time = 0;
            if(luck != null) {
                if(luck.getAmplifier() == LUCK_LVL) {
                    if(luck.isInfinite()) continue;
                    time = luck.getDuration();
                }

                player.removePotionEffect(PotionEffectType.LUCK);
            }
            player.addPotionEffect(new PotionEffect(PotionEffectType.LUCK, time + entry.getValue() * 20, LUCK_LVL, false, false, true));
        }
    }

    public boolean setKamiBlock(Block block) {
        SimpleBlockLocation blockLoc = new SimpleBlockLocation(block.getWorld().getUID(), block.getX(), block.getY(), block.getZ());
        if(kamiBlock.containsKey(blockLoc)) return false;

        kamiBlock.put(blockLoc, block.getType());
        block.setType(BLOCK_TYPE);
        return true;
    }

    public void setKamiBlock(SimpleBlockLocation location) {
        if(kamiBlock.containsKey(location)) return;

        World world = Bukkit.getWorld(location.worldUuid());
        if(world == null) return;
        Block block = world.getBlockAt(location.x(), location.y(), location.z());

        kamiBlock.put(location, block.getType());
        block.setType(BLOCK_TYPE);
    }

    public boolean hasKamiBlock(SimpleBlockLocation location) {
        return kamiBlock.containsKey(location);
    }

    public void removeKamiBlock(SimpleBlockLocation location) {
        Material material = kamiBlock.remove(location);
        if(material == null) return;

        World world = Bukkit.getWorld(location.worldUuid());
        if(world == null) return;
        Block block = world.getBlockAt(location.x(), location.y(), location.z());

        block.setType(material);
    }

    private KitsuneData getKitsuneData(IRaceData data) {
        if(data instanceof KitsuneData d) {
            return d;
        }
        else {
            if(data instanceof OniData oniData && oniData.getOverlay() instanceof KitsuneData d) {
                return d;
            }
        }

        return null;
    }

}
