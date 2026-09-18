package be.lymaes.race.ability.model;

import be.lymaes.race.Race;
import be.lymaes.race.RaceProfile;
import be.lymaes.race.ability.RaidWinner;
import be.lymaes.race.data.IRaceData;
import be.lymaes.race.model.IRace;
import be.lymaes.race.model.Kitsune;

public class KitsuneRaidExp implements RaidWinner {

    public void onRaidFinish(RaceProfile profile) {
        IRaceData raceData = profile.getRaceData();

        IRace irace = Race.getInstance().getRaceManager().getRaceModel(raceData.getRace());
        if(!(irace instanceof Kitsune kitsune)) return;

        if(kitsune.getExpRequired(raceData.getRank()+1) != -1) {
            profile.rankUp();
        }
    }

}
