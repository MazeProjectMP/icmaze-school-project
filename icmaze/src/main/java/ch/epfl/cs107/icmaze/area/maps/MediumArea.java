package ch.epfl.cs107.icmaze.area.maps;

public class MediumArea extends MazeLevels {

    private static final int sideSize = 16;
    public MediumArea(AreaPortals areaPortalEntry, AreaPortals areaPortalExit, int keyID, int difficulty){
        super(areaPortalEntry, areaPortalExit, difficulty, keyID, "MediumArea", sideSize);
    }


}
