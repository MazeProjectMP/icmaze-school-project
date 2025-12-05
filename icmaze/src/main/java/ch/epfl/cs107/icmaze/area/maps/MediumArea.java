package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.Difficulty;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class MediumArea extends MazeLevels {

    private static final int sideSize = 16;
    public MediumArea(AreaPortals areaPortalEntry, AreaPortals areaPortalExit, int keyID){
        super(areaPortalEntry, areaPortalExit, Difficulty.HARDEST, keyID, "MediumArea", sideSize);
    }


}
