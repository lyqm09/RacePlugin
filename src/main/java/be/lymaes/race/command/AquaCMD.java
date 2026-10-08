package be.lymaes.race.command;

import be.lymaes.race.Race;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jspecify.annotations.NonNull;

public class AquaCMD implements CommandExecutor {

    public AquaCMD(Race plugin) {

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

        ItemStack item = player.getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();
        if(item.getType() == Material.AIR || meta == null) {
            player.sendMessage("Erreur : Impossible d'appliquer Aqua Affinity.");
            return true;
        }

        meta.addEnchant(Enchantment.AQUA_AFFINITY, 1, true);
        meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);

        item.setItemMeta(meta);

        return true;
    }
}
