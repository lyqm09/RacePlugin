package be.lymaes.race.command;

import be.lymaes.race.Race;
import be.lymaes.race.RaceProfile;
import be.lymaes.race.manager.RaceManager;
import be.lymaes.race.model.Kitsune;
import be.lymaes.race.model.RaceType;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

public class MtailCMD implements CommandExecutor {

    private final RaceManager raceManager;

    public MtailCMD(Race plugin) {
        raceManager = plugin.getRaceManager();
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, @NonNull String[] args) {
        if(args.length != 3) {
            return false;
        }

        boolean give;
        if(args[0].equals("give")) {
            give = true;
        } else if(args[0].equals("remove")) {
            give = false;
        } else {
            return false;
        }

        Player player = Bukkit.getPlayer(args[1]);
        if(player == null || !player.isOnline()) {
            sender.sendMessage("Erreur : Le joueur spécifié n'est pas en ligne.");
            return true;
        }

        RaceProfile profile = raceManager.getProfile(player);
        RaceType race = profile.raceData.getRace();
        if(!(raceManager.getRaceModel(race) instanceof Kitsune kitsune)) {
            sender.sendMessage("Erreur : Le joueur spécifié n'est pas un Kitsune.");
            return true;
        }

        int queues = -1;
        try {
            queues = Integer.parseInt(args[2]);
        } catch (NumberFormatException ignored) {}
        if(queues < 1) {
            sender.sendMessage("Erreur : Le montant spécifié doit être un entier positif.");
            return true;
        }

        int newRank = profile.raceData.getRank() + (give ? queues : -queues);
        Kitsune.Rank rank = Kitsune.Rank.fromRank(newRank);
        profile.raceData.setRank(rank.rank);

        kitsune.applyRacePerks(profile);
        profile.updateTabInfo();

        sender.sendMessage(player.getDisplayName() + " devient un Kitsune à " + rank.name + ".");
        player.sendMessage("Tu deviens un Kitsune à " + rank.name + ".");
        return true;
    }

}
