package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.Size;

/**
 * Represents a large maze area.
 * <p>
 * A {@code LargeArea} is a concrete {@link MazeLevels} with a fixed size of 32×32
 * cells and a higher exploration space. It defines entry and exit portals as well
 * as an optional key requirement and difficulty level.
 * </p>
 */

public class LargeArea extends MazeLevels {
    /**
     * Creates a new large maze area.
     * @param areaPortalEntry (AreaPortals): entry portal of the area
     * @param areaPortalExit  (AreaPortals): exit portal of the area
     * @param keyID           (int): identifier of the key required to unlock the exit
     * @param difficulty      (int): difficulty level of the area
     */
    public LargeArea(AreaPortals areaPortalEntry, AreaPortals areaPortalExit, int keyID, int difficulty) {
        super(areaPortalEntry, areaPortalExit, difficulty, keyID, "LargeArea", Size.LARGE);
    }
}
