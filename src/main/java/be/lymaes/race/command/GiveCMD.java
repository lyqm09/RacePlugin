package be.lymaes.race.command;

import be.lymaes.race.Race;
import be.lymaes.race.item.IRaceItem;
import be.lymaes.race.item.RaceItem;
import be.lymaes.race.manager.ItemManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GiveCMD implements CommandExecutor, TabCompleter {

    private final ItemManager itemManager;

    private final List<String> items;

    public GiveCMD(Race plugin) {
        this.itemManager = plugin.getItemManager();

        this.items = new ArrayList<>();
        for(RaceItem ritem : RaceItem.values()) {
            items.add(ritem.id);
        }
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, @NonNull String[] args) {
        if(args.length != 2) {
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if(target == null) {
            sender.sendMessage("Erreur : Le joueur mentionné n'existe pas.");
            return true;
        }

        if(!items.contains(args[1])) {
            sender.sendMessage("Erreur : L'objet mentionné n'existe pas.");
            return true;
        }

        IRaceItem item = itemManager.getItem(args[1]);
        if(item == null) {
            sender.sendMessage("Erreur : Un problème s'est produit.");
            return true;
        }

        target.getInventory().addItem(item.getItem());
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, @NonNull String[] args) {
        if(args.length == 2) {
            return items;
        }
        return List.of();
    }
}
