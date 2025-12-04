package ch.epfl.cs107.icmaze.area;

import ch.epfl.cs107.icmaze.Difficulty;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class MediumArea extends ICMazeArea {
    private AreaPortals areaPortalEntry;
    private AreaPortals areaPortalExit;
    private int difficulty;
    public MediumArea(AreaPortals areaPortalEntry, AreaPortals areaPortalExit, int difficulty) {
        super("MediumArea", 16);

        this.areaPortalEntry = areaPortalEntry;
        this.areaPortalExit = areaPortalExit;
        this.difficulty = Difficulty.HARDEST;
    }

    public MediumArea() {
        super("MediumArea", 16);

    }
    @Override
    public DiscreteCoordinates getPlayerSpawnPosition() {
        return null;
    }

    @Override
    public void lockPortalWithKey(AreaPortals areaPortals, int maxValue) {

    }

//    @Override
//    public void createArea() {
//
//    }

    @Override
    protected void registerTheme() {

    }

//    @Override
//    public AreaPortals getEntryPortal() {
//        return null;
//    }

    @Override
    public String getTitle() {
        return "";
    }
}
