package be.lymaes.race.command;

import be.lymaes.race.Race;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class FishCMD implements CommandExecutor {

    private final List<EntityType> aquaticEntities;

    public FishCMD(Race plugin) {
        aquaticEntities = Arrays.asList(
                EntityType.AXOLOTL,
                EntityType.COD,
                EntityType.DOLPHIN,
                EntityType.ELDER_GUARDIAN,
                EntityType.GLOW_SQUID,
                EntityType.GUARDIAN,
                EntityType.NAUTILUS,
                EntityType.PUFFERFISH,
                EntityType.SALMON,
                EntityType.SQUID,
                EntityType.TADPOLE,
                EntityType.TROPICAL_FISH,
                EntityType.TURTLE,
                EntityType.ZOMBIE_NAUTILUS
        );
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

        int random = ThreadLocalRandom.current().nextInt(aquaticEntities.size());
        EntityType entity = aquaticEntities.get(random);
        Location location = player.getLocation();

        location.getWorld().spawnEntity(location, entity);
        return true;
    }
}
