package be.lymaes.race.model;

import be.lymaes.race.RaceProfile;

public class Human implements IRace {

    @Override
    public void loadRank(RaceProfile profile) {

    }

    @Override
    public void reloadEffect(RaceProfile profile) {

    }

    @Override
    public void cleanup(RaceProfile profile) {

    }

    @Override
    public void addExp(RaceProfile profile, int n) {

    }

    @Override
    public String getRankName(int rank) {
        return null;
    }

    @Override
    public int getExpRequired(int rank) {
        return 0;
    }

}
