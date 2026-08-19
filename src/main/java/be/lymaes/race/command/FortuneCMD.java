package be.lymaes.race.command;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class FortuneCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage("Erreur : Seul un joueur peut executer cette commande.");
            return true;
        }

        if(args.length > 0) {
            return false;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();
        if(item.getType() == Material.AIR || meta == null) {
            player.sendMessage(ChatColor.RED + "Erreur : Impossible d'appliquer Fortune.");
            return true;
        }

        meta.addEnchant(Enchantment.FORTUNE, 1, true);
        meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);

        item.setItemMeta(meta);

        return true;
    }

}
