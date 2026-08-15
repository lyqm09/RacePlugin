package be.lymaes.race.command;

import be.lymaes.race.Race;
import be.lymaes.race.RaceProfile;
import be.lymaes.race.manager.RaceManager;
import be.lymaes.race.model.IRace;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

public class MexpCMD implements CommandExecutor {

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, @NonNull String[] args) {
        if(args.length != 3) {
            return false;
        }

        if(!args[0].equals("give")) {
            return false;
        }

        Player player = Bukkit.getPlayer(args[1]);
        if(player == null || !player.isOnline()) {
            sender.sendMessage("Erreur : Le joueur spécifié n'est pas en ligne.");
            return true;
        }

        int exp = -1;
        try {
            exp = Integer.parseInt(args[2]);
        } catch (NumberFormatException ignored) {}
        if(exp < 1) {
            sender.sendMessage("Erreur : Le montant spécifié doit être un entier positif.");
            return true;
        }

        RaceManager manager = Race.getInstance().getRaceManager();
        RaceProfile profile = manager.getProfile(player);
        IRace model = manager.getRaceModel(profile.raceData.getRace());

        model.addExp(profile, exp);
        sender.sendMessage("Un don de " + exp + "exp a été fait à " + player.getDisplayName());
        return true;
    }
}
