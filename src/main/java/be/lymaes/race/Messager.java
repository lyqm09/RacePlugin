package be.lymaes.race;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;

import java.time.Duration;
import java.util.UUID;

public class Messager {

    private final BukkitAudiences adventure;

    public Messager(Race plugin) {
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

}
