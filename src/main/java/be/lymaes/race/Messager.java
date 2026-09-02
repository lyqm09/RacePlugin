package be.lymaes.race;

import be.lymaes.race.data.IRaceData;
import be.lymaes.race.data.OniData;
import be.lymaes.race.manager.RaceManager;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.UUID;

public class Messager {

    private final RaceManager raceManager;
    private final BukkitAudiences adventure;

    public Messager(Race plugin) {
        this.raceManager = plugin.getRaceManager();
        this.adventure = BukkitAudiences.create(plugin);
    }

    public void terminate() {
        adventure.close();
    }

    public void sendRankUpTitle(UUID uuid, String rankName) {
        Audience audience = adventure.player(uuid);

        Times times = Times.times(Duration.ofMillis(500), Duration.ofMillis(1500), Duration.ofMillis(500));

        Title title = Title.title(
                Component.text("RANG SUPÉRIEUR", NamedTextColor.GOLD, TextDecoration.BOLD),
                Component.text(rankName, NamedTextColor.YELLOW),
                times
        );

        Sound sound = Sound.sound(
                Key.key("block.note_block.iron_xylophone"),
                Sound.Source.MASTER,
                1.0f,
                1.0f
        );

        audience.showTitle(title);
        audience.playSound(sound);
    }

    public void sendOniChoice(Player player, IRaceData data) {
        Audience audience = adventure.player(player);

        UUID playerUuid = player.getUniqueId();
        UUID token = UUID.randomUUID();
        raceManager.putPendingOffer(token, (target) -> {
            if(playerUuid != target.getUniqueId()) {
                target.sendMessage("Cette offre ne t'est pas destinée.");
                return false;
            }

            raceManager.changeRace(target, new OniData(0, 0, data));
            target.sendMessage("Félicitation ! Tu deviens Oni tout en gardant tes anciens avantages.");
            return true;
        }, 20 * 60);

        Component message = Component.text("Veux tu devenir Oni ? ")
                .append(Component.text("[OUI]")
                .color(NamedTextColor.GREEN)
                .clickEvent(ClickEvent.runCommand("/mutsuhara accepte " + token)))
                .append(Component.text(" "))
                .append(Component.text("[NON]")
                .color(NamedTextColor.RED)
                .clickEvent(ClickEvent.runCommand("/mutsuhara refuse " + token)));

        audience.sendMessage(message);
    }

    public BukkitAudiences adventure() {
        return adventure;
    }

}
