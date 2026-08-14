package be.lymaes.race;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;

public class Messager {

    private final BukkitAudiences adventure;

    // TODO private Map<Audience, Queue<Consumer<?>>> titleQueue = new HashMap<>();

    public Messager(Race plugin) {
        this.adventure = BukkitAudiences.create(plugin);
    }

    public static void sendRankupTitle(RaceProfile profile, String rank) {
        Audience player = Race.getInstance().getMessager().adventure.player(profile.getPlayer());

        Component title = Component.text("Rank Up !").color(NamedTextColor.RED);
        Component subtitle = Component.text(rank).color(profile.raceData.getRace().color);
        Title message = Title.title(title, subtitle);

        // TODO sound

        player.showTitle(message);
    }

    public BukkitAudiences adventure() {
        if(adventure == null)
            throw new IllegalCallerException();
        return this.adventure;
    }

    public void terminate() {
        adventure.close();
    }

}
