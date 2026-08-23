package be.lymaes.race.item;

import org.bukkit.event.entity.EntityDeathEvent;

public interface Droppable {

    void onDrop(EntityDeathEvent e);

}
