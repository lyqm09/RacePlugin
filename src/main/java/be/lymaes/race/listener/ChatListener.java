package be.lymaes.race.listener;

import be.lymaes.race.Race;
import be.lymaes.race.RaceProfile;
import be.lymaes.race.model.RaceType;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;


public class ChatListener implements Listener {

    public final BukkitAudiences adventure;

    public ChatListener(Race plugin) {
        this.adventure = plugin.getMessager().adventure();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent e) {

        e.setCancelled(true);

        Player player = e.getPlayer();
        String message = e.getMessage();

        RaceProfile profile = Race.getInstance().getRaceManager().getProfile(player);
        RaceType race = profile.raceData.getRace();

        Component chatFormat = Component.empty()
                .append(Component.text("[").color(NamedTextColor.WHITE))
                .append(Component.text(race.name).color(race.color))
                .append(Component.text("] ").color(NamedTextColor.WHITE))
                .append(Component.text(player.getDisplayName()).color(race.color))
                .append(Component.text(" > ").color(NamedTextColor.WHITE))
                .append(Component.text(message).color(NamedTextColor.WHITE));

        adventure.players().sendMessage(chatFormat);
        adventure.console().sendMessage(chatFormat);

    }

}
