package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.Difficulty;

public class LargeArea extends MazeLevels {
    public LargeArea(AreaPortals areaPortalEntry, AreaPortals areaPortalExit, int keyID){
        super(areaPortalEntry, areaPortalExit, Difficulty.HARDEST, keyID, "LargeArea", 32);
    }
}
