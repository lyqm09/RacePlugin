package be.lymaes.race.command;

import be.lymaes.race.Race;
import be.lymaes.race.manager.RaceManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Registry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionEffectTypeCategory;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class CallKamiCMD implements CommandExecutor {

    private static final long COOLDOWN = 5 * 60 * 1000;

    private final RaceManager raceManager;
    private Map<UUID, Long> cooldowns = new HashMap<>();

    private static final List<PotionEffectType> POSITIVE_EFFECTS = new ArrayList<>();

    static {
        for(PotionEffectType type : Registry.EFFECT) {
            if(type == null || type.getCategory() != PotionEffectTypeCategory.BENEFICIAL) continue;
            POSITIVE_EFFECTS.add(type);
        }
    }

    public CallKamiCMD(Race plugin) {
        this.raceManager = plugin.getRaceManager();
    }

    public void giveBonus(Player player) {
        List<PotionEffectType> shuffled = new ArrayList<>(POSITIVE_EFFECTS);

        ThreadLocalRandom random = ThreadLocalRandom.current();
        Collections.shuffle(shuffled, random);

        int size = Math.min(5, shuffled.size());
        List<PotionEffectType> bonus = shuffled.subList(0, size);

        for(PotionEffectType type : bonus) {
            player.addPotionEffect(new PotionEffect(type, (int) (COOLDOWN/1000) * 20, random.nextInt(5)));
        }
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, @NonNull String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage("Erreur : Seul un joueur peut executer cette commande.");
            return true;
        }

        if(args.length > 0) {
            return false;
        }

        long currentTime = System.currentTimeMillis();

        UUID uuid = player.getUniqueId();
        if(cooldowns.containsKey(uuid)) {
            long endTime = cooldowns.get(uuid);
            long time = currentTime - endTime;

            if(time > 0) {
                player.sendMessage("Tu dois encore attendre " + ChatColor.AQUA + time + ChatColor.RESET + "sec avec de réutiliser cette commande.");
                return true;
            }
        }

        cooldowns.put(uuid, currentTime + COOLDOWN);
        giveBonus(player);
        return true;
    }

}
