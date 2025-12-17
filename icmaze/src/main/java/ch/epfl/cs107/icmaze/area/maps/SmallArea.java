package ch.epfl.cs107.icmaze.area.maps;

public class SmallArea extends MazeLevels {
    private static final int side = 8;
    public SmallArea(AreaPortals areaPortalEntry, AreaPortals areaPortalExit, int keyID, int difficulty){
        super(areaPortalEntry, areaPortalExit, difficulty, keyID, "SmallArea", side);
    }
}
