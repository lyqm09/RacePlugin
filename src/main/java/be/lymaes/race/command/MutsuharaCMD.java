package be.lymaes.race.command;

import be.lymaes.race.Race;
import be.lymaes.race.gui.GUITypes;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MutsuharaCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player player)) {
            return false;
        }

        if(args.length > 0) {
            return false;
        }

        Race.getInstance().getGuiManager().getGUI(GUITypes.RACE).open(player);
        return true;
    }
}
