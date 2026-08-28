package be.lymaes.race.command;

import be.lymaes.race.Race;
import be.lymaes.race.gui.GUITypes;
import be.lymaes.race.manager.GUIManager;
import be.lymaes.race.manager.RaceManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.Function;

public class MutsuharaCMD implements CommandExecutor {

    private final RaceManager raceManager;
    private final GUIManager guiManager;

    public MutsuharaCMD(Race plugin) {
        this.raceManager = plugin.getRaceManager();
        this.guiManager = plugin.getGuiManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player player)) {
            return false;
        }

        if(args.length == 0) {
            guiManager.getGUI(GUITypes.RACE).open(player);
            return true;
        }
        else if(args.length == 2) {

            UUID token = UUID.fromString(args[1]);
            Function<Player, Boolean> function = raceManager.getPendingOffer(token);
            if(function == null) {
                player.sendMessage("Cette offre n'est pas disponible.");
                return true;
            }

            if(args[0].equalsIgnoreCase("accepte")) {
                if(function.apply(player)) {
                    raceManager.removePendingOffer(token);
                }
                return true;
            }
            else if(args[0].equalsIgnoreCase("refuse")) {
                player.sendMessage("Tu as refusé l'offre.");
                raceManager.removePendingOffer(token);
                return true;
            }
        }

        return false;
    }
}
