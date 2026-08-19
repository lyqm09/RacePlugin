package be.lymaes.race.model;

import be.lymaes.race.RaceProfile;

public interface IRankable {


    void addExp(RaceProfile profile, int n);
    String getRankName(int rank);
    int getExpRequired(int rank);

}
