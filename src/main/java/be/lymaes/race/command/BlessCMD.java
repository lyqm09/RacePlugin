package be.lymaes.race.command;

import be.lymaes.race.Race;
import be.lymaes.race.RaceProfile;
import com.google.common.collect.Lists;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class BlessCMD implements CommandExecutor {

    private static final String TIME_KEY = "bless_cmd";

    private final List<Enchantment> ENCHANTMENTS;

    public BlessCMD() {
        ENCHANTMENTS = Lists.newArrayList(Registry.ENCHANTMENT);
    }

    private ItemStack getEnchantedBook() {

        Enchantment enchantment = ENCHANTMENTS.get(ThreadLocalRandom.current().nextInt(ENCHANTMENTS.size()));

        int lvl = ThreadLocalRandom.current().nextInt(enchantment.getStartLevel(), enchantment.getMaxLevel() + 1);

        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);

        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
        if (meta == null)
            return book;

        meta.addStoredEnchant(enchantment, lvl, true);
        book.setItemMeta(meta);

        return book;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage("Erreur : Seul un joueur peut executer cette commande.");
            return true;
        }

        if(args.length != 1) {
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if(target == null) {
            player.sendMessage(Color.RED + "Error : Le joueur visé n'est pas en ligne.");
            return true;
        }

        RaceProfile profile = Race.getInstance().getRaceManager().getProfile(player);

        long time = profile.getTime(TIME_KEY);
        long currentTime = System.currentTimeMillis();

        if(time != 0 && time > currentTime) {
            player.sendMessage(ChatColor.RED + "Error : Tu dois encore attendre avant d'utiliser cette commande.");
            return true;
        }

        if(target.getInventory().addItem().isEmpty()) {
            profile.putTime(TIME_KEY, currentTime + 1000 * 60 * 10);
        }
        else {
            player.sendMessage(ChatColor.RED + "Erreur : Le joueur spécifié n'a pas la place dans son inventaire.");
        }

        return true;
    }

}
