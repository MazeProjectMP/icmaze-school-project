package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.Size;

/**
 * Small-sized maze area (8x8).
 */

public class SmallArea extends MazeLevels {
    /**
     * Creates a small maze area.
     *
     * @param areaPortalEntry the entry portal configuration
     * @param areaPortalExit the exit portal configuration
     * @param keyID the identifier used in the area title
     * @param difficulty the maze difficulty level
     */
    public SmallArea(AreaPortals areaPortalEntry, AreaPortals areaPortalExit, int keyID, int difficulty){
        super(areaPortalEntry, areaPortalExit, difficulty, keyID, "SmallArea", Size.SMALL);
    }
}
