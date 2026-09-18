package be.lymaes.race.command;

import be.lymaes.race.Race;
import be.lymaes.race.RaceProfile;
import be.lymaes.race.ability.AbilityKey;
import be.lymaes.race.data.TamashiData;
import be.lymaes.race.manager.RaceManager;
import be.lymaes.race.model.IRace;
import be.lymaes.race.model.Tamashi;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.ThreadLocalRandom;

public class KamiCMD implements CommandExecutor {

    public static final int COOLDOWN = 10 * 60;
    public static final int ACTIVE_TIME = 5 * 60;

    private final Race plugin;
    private final RaceManager raceManager;

    public KamiCMD(Race plugin) {
        this.plugin = plugin;
        this.raceManager = plugin.getRaceManager();
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, @NonNull String[] args) {
        if(args.length != 1) {
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if(target == null) {
            sender.sendMessage("Erreur : Le joueur visé n'est pas en ligne.");
            return true;
        }

        if(!(sender instanceof Player player)) {
            int subrace = ThreadLocalRandom.current().nextInt(4);
            if(!giveKamiAbilities(target, subrace)) {
                sender.sendMessage("Oups... Une erreur s'est produite.");
                return true;
            }
            sender.sendMessage(target.getDisplayName() + " - Tamashi de type " + Tamashi.SubRace.fromId(subrace).name);
            return true;
        }

        if(target == player) {
            sender.sendMessage("Erreur : Tu n'as pas le droit d'utiliser cette commande sur toi.");
            return true;
        }

        RaceProfile profile = raceManager.getProfile(player);
        TamashiData tamashiData = profile.getRaceData(TamashiData.class);
        if(tamashiData == null) {
            sender.sendMessage("Erreur : Tu n'as pas le droit d'utiliser cette commande.");
            return true;
        }

        long currentTime = System.currentTimeMillis();

        long time = tamashiData.getKamiCMDTime();
        if(currentTime < time) {
            sender.sendMessage("Tu dois encore attendre avant d'utiliser cette commande.");
            return true;
        }

        int subrace = tamashiData.getSubrace();
        if(!giveKamiAbilities(target, subrace)) {
            sender.sendMessage("Oups... Une erreur s'est produite.");
            return true;
        }

        tamashiData.setKamiCMDTime(currentTime + COOLDOWN * 1000);

        player.sendMessage(target.getDisplayName() + " a le droit d'utiliser les capacités d'un Tamashi de type " + Tamashi.SubRace.fromId(subrace).name + " de rang Kami.");
        return true;
    }

    public boolean giveKamiAbilities(Player target, int subrace) {
        RaceProfile profile = raceManager.getProfile(target);
        if(profile == null) return false;

        TamashiData tamashiData = new TamashiData(subrace, Tamashi.Rank.KAMI.rank, 0);

        IRace model = raceManager.getRaceModel(tamashiData.getRace());
        if(!(model instanceof Tamashi tamashi)) return false;

        profile.addRaceData(tamashiData.getClass(), tamashiData);
        tamashi.applyRacePerks(target, profile, tamashiData);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {

            tamashi.cleanup(target, profile);
            profile.removeRaceData(tamashiData.getClass());

        }, ACTIVE_TIME * 20L);
        return true;
    }

}
