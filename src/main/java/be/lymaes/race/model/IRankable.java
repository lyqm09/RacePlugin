package be.lymaes.race.model;

import be.lymaes.race.RaceProfile;

public interface IRankable {

    String getRankName(int rank);

    int getExpRequired(int rank);

    default boolean canRankUp(RaceProfile profile) {
        return true;
    }

}
