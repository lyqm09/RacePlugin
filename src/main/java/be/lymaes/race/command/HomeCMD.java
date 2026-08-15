package be.lymaes.race.command;

import be.lymaes.race.Race;
import be.lymaes.race.RaceProfile;
import be.lymaes.race.data.TamashiData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

public class HomeCMD implements CommandExecutor  {

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, @NonNull String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage("Erreur : Seul un joueur peut executer cette commande.");
            return true;
        }

        if(args.length > 0) {
            return false;
        }

        RaceProfile profile = Race.getInstance().getRaceManager().getProfile(player);
        TamashiData data = ((TamashiData)profile.raceData);

        data.setHome(player.getLocation());
        sender.sendMessage("Nouveau foyer défini.");
        return true;
    }

}
