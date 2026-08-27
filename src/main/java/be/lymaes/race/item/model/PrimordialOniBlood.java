package be.lymaes.race.item.model;

import be.lymaes.race.Messager;
import be.lymaes.race.Race;
import be.lymaes.race.RaceProfile;
import be.lymaes.race.data.IRaceData;
import be.lymaes.race.data.OniData;
import be.lymaes.race.item.ARaceItem;
import be.lymaes.race.item.RaceItem;
import be.lymaes.race.item.Splashable;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.List;

public class PrimordialOniBlood extends ARaceItem implements Splashable {

    @Override
    public RaceItem getType() {
        return RaceItem.PRIMORDIAL_ONI_BLOOD;
    }

    @Override
    protected void applyMeta(ItemMeta meta) {
        if(!(meta instanceof PotionMeta potion)) return;

        potion.setColor(Color.RED);
    }

    @Override
    public void onSplash(List<Player> players, List<IRaceData> data) {
        Messager messager = Race.getInstance().getMessager();
        for(int i = 0; i < players.size(); i++) {
            IRaceData raceData = data.get(i);
            if(raceData instanceof OniData) return;

            Player player = players.get(i);
            messager.sendOniChoice(player, raceData);
        }

    }

}
