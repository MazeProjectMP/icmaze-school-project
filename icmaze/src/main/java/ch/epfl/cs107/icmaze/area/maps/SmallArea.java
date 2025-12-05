package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.Difficulty;
import ch.epfl.cs107.icmaze.MazeGenerator;
import ch.epfl.cs107.icmaze.actor.Rock;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class SmallArea extends MazeLevels {
    private static final int side = 8;
    public SmallArea(AreaPortals areaPortalEntry, AreaPortals areaPortalExit, int keyID){
        super(areaPortalEntry, areaPortalExit, Difficulty.HARDEST, keyID, "SmallArea", side);
    }
}
