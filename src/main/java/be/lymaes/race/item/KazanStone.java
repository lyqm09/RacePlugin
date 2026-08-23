package be.lymaes.race.item;

import be.lymaes.race.RaceProfile;
import be.lymaes.race.model.IRace;
import be.lymaes.race.model.Oni;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.ThreadLocalRandom;

public class KazanStone implements IRaceItem, Consumable, Droppable {

    private static final double TOL = 0.0005;

    @Override
    public RaceItem getType() {
        return RaceItem.KAZAN_STONE;
    }

    @Override
    public void onDrop(EntityDeathEvent e) {
        if(!(e.getEntity() instanceof WitherSkeleton))
            return;

        double random = ThreadLocalRandom.current().nextDouble();
        if(random > TOL)
            return;

        ItemStack kazanStone = RaceItem.KAZAN_STONE.getItem();
        e.getDrops().add(kazanStone);
    }

    @Override
    public void onConsume(Player player, RaceProfile profile, IRace model) {
        if(model instanceof Oni oni) {
            int nextRank = profile.raceData.getRank() + 1;
            if(nextRank < Oni.Rank.GENERAL.rank) return;

            int expRequired = oni.getExpRequired(nextRank);
            if(expRequired < 0) return;
            if (profile.raceData.getExp() < expRequired) return;

            profile.raceData.subExp(expRequired);
            profile.rankUp();

            profile.updateTabInfo();
        } else {
            player.addPotionEffect(PotionEffectType.POISON.createEffect(10 * 20, 1));
        }
    }

}
