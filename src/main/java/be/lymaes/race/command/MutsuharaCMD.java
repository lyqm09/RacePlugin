package be.lymaes.race.command;

import be.lymaes.race.Race;
import be.lymaes.race.gui.GUITypes;
import be.lymaes.race.manager.GUIManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MutsuharaCMD implements CommandExecutor {

    private final GUIManager guiManager;

    public MutsuharaCMD(Race plugin) {
        this.guiManager = plugin.getGuiManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player player)) {
            return false;
        }

        if(args.length > 0) {
            return false;
        }

        guiManager.getGUI(GUITypes.RACE).open(player);
        return true;
    }
}
