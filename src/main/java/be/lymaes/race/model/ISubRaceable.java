package be.lymaes.race.model;

import be.lymaes.race.gui.GUITypes;
import be.lymaes.race.gui.IRaceGUI;

public interface ISubRaceable {

    GUITypes getSubRaceGUI();

    String getSubraceName(int subrace);

}
