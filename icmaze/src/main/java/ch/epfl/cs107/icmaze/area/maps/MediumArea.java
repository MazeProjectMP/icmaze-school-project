package ch.epfl.cs107.icmaze.area.maps;


import ch.epfl.cs107.icmaze.Size;

/**
 * Medium-sized maze area (16x16).
 */
public class MediumArea extends MazeLevels {


    /**
     * Creates a medium maze area.
     * @param areaPortalEntry the entry portal configuration
     * @param areaPortalExit the exit portal configuration
     * @param keyID the identifier used in the area title
     * @param difficulty the maze difficulty level
     */
    public MediumArea(AreaPortals areaPortalEntry, AreaPortals areaPortalExit, int keyID, int difficulty){
        super(areaPortalEntry, areaPortalExit, difficulty, keyID, "MediumArea", Size.MEDIUM);
    }


}
