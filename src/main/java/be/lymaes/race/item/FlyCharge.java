package be.lymaes.race.item;

import be.lymaes.race.model.Tamashi;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
public class FlyCharge extends ARaceItem implements IStaticItem, Heldable {

    @Override
    public RaceItem getType() {
        return RaceItem.FLY_CHARGE;
    }

    @Override
    public void onSwitchOn(Player player) {
        if(player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) return;

        if(!player.hasPermission(Tamashi.PERM_FLY_CHARGE)) return;

        player.setAllowFlight(true);
    }

    @Override
    public void onSwitchOff(Player player) {
        if(player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) return;

        if(!player.hasPermission(Tamashi.PERM_FLY_CHARGE)) return;

        player.setAllowFlight(false);
    }
}
