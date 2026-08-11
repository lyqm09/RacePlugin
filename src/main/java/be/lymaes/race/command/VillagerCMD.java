package be.lymaes.race.command;

import be.lymaes.race.Race;
import be.lymaes.race.RaceProfile;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VillagerCMD implements CommandExecutor {

    private static final String TIME_KEY = "spawn_villager_cmd";
    private static final long COOLDOWN = 1000 * 60 * 5;

    private Map<UUID, Villager> villagers = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage("Erreur : Seul un joueur peut executer cette commande.");
            return true;
        }

        if(args.length > 0) {
            return false;
        }

        RaceProfile profile = Race.getInstance().getRaceManager().getProfile(player);

        long time = profile.getTime(TIME_KEY);
        long currentTime = System.currentTimeMillis();

        if(time != 0 && time > currentTime) {
            player.sendMessage(ChatColor.RED + "Error : Tu dois encore attendre avant d'utiliser cette commande.");
            return true;
        }

        Villager prevVillager = villagers.get(player.getUniqueId());
        if(prevVillager != null && !prevVillager.isDead()) {
            prevVillager.remove();
        }

        Villager newVillager = player.getWorld().spawn(player.getLocation(), Villager.class, CreatureSpawnEvent.SpawnReason.CUSTOM, true, null);
        villagers.put(player.getUniqueId(), newVillager);

        profile.putTime(TIME_KEY, currentTime + COOLDOWN);

        return true;
    }
}
