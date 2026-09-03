package be.lymaes.race.command;

import be.lymaes.race.Race;
import be.lymaes.race.RaceProfile;
import be.lymaes.race.ability.AbilityKey;
import be.lymaes.race.ability.model.Offering;
import be.lymaes.race.data.KitsuneData;
import be.lymaes.race.manager.AbilityManager;
import be.lymaes.race.manager.RaceManager;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

public class SetKamiCMD implements CommandExecutor {

    private final RaceManager raceManager;
    private final AbilityManager abilityManager;

    public SetKamiCMD(Race plugin) {
        this.raceManager = plugin.getRaceManager();
        this.abilityManager = plugin.getAbilityManager();
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

        RaceProfile profile = raceManager.getProfile(player);
        if(!(profile.raceData instanceof KitsuneData data)) return false;

        Block block = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
        if(block.isEmpty() || block.isLiquid() || block.isPassable()) return false;

        if(!(abilityManager.getAbility(AbilityKey.PERM_SETKAMI) instanceof Offering offering)) {
            sender.sendMessage("Erreur : Oups... Une erreur s'est produite.");
            return true;
        }

        if(!offering.setKamiBlock(block)) {
            sender.sendMessage("Erreur : Ce lieu a deja été définit comme lieu d'offrande.");
            return true;
        }

        if(profile.hasAbility(AbilityKey.PERM_SETKAMI)) {
            data.setKamiBlockLocation(block);
        }

        sender.sendMessage("Lieu d'offrande définit.");
        return true;
    }

}
