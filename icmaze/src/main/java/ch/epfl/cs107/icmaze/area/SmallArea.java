package ch.epfl.cs107.icmaze.area;

import ch.epfl.cs107.icmaze.Difficulty;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class SmallArea extends ICMazeArea {
    private AreaPortals areaPortalEntry;
    private AreaPortals areaPortalExit;
    private int difficulty;


    public SmallArea(AreaPortals areaPortalEntry, AreaPortals areaPortalExit, int difficulty) {
        super("SmallArea", 8);
        this.areaPortalEntry = areaPortalEntry;
        this.areaPortalExit = areaPortalExit;
        this.difficulty = difficulty;

    }
    public SmallArea() {
        super("SmallArea", 8);
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
        super.createArea();
    }

    @Override
    protected void registerTheme() {

    }


//    @Override
//    public ICMazeArea.AreaPortals getEntryPortal() {
//        return null;
//    }

    @Override
    public String getTitle() {
        return "j";
    }
}

