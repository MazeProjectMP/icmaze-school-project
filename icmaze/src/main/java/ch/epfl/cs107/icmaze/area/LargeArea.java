package ch.epfl.cs107.icmaze.area;

import ch.epfl.cs107.icmaze.Difficulty;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class LargeArea extends ICMazeArea{
    private AreaPortals areaPortalEntry;
    private AreaPortals areaPortalExit;
    private int difficulty;
    public LargeArea(AreaPortals areaPortalEntry, AreaPortals areaPortalExit, int difficulty) {
        super("LargeArea", 32);

        this.areaPortalEntry = areaPortalEntry;
        this.areaPortalExit = areaPortalExit;
        this.difficulty = difficulty;


    }

    public LargeArea() {
        super("LargeArea", 32);

    }

    @Override
    public DiscreteCoordinates getPlayerSpawnPosition() {
        return null;
    }

    @Override
    public void lockPortalWithKey(AreaPortals areaPortals, int maxValue) {

    }

    @Override
    public void createArea() {

    }

    @Override
    protected void registerTheme() {

    }
//
//    @Override
//    public AreaPortals getEntryPortal() {
//        return null;
//    }

    @Override
    public String getTitle() {
        return "";
    }
}
