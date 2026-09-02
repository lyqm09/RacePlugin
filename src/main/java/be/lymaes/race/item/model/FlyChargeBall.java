package be.lymaes.race.item.model;

import be.lymaes.race.item.ARaceItem;
import be.lymaes.race.item.IStaticItem;
import be.lymaes.race.item.RaceItem;

public class FlyChargeBall extends ARaceItem implements IStaticItem {

    @Override
    public RaceItem getType() {
        return RaceItem.FLY_CHARGE;
    }

}
