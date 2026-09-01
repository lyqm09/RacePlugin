package be.lymaes.race.ability;

import be.lymaes.race.RaceProfile;

public interface RaidWinner extends Ability {

    void onRaidFinish(RaceProfile profile);

}
