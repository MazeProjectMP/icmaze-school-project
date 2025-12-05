package ch.epfl.cs107.icmaze.area.maps;
import ch.epfl.cs107.icmaze.Difficulty;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class MazeLevels extends ICMazeArea {
    private AreaPortals areaPortalEntry;
    private AreaPortals areaPortalExit;
    private int difficulty;

    private int keyID;

    private final String mapName;

    public MazeLevels(AreaPortals areaPortalEntry, AreaPortals areaPortalExit, int difficulty, int keyID, String mapName, int sizeSide) {
        super(mapName, sizeSide);

        this.areaPortalEntry = areaPortalEntry;
        this.areaPortalExit = areaPortalExit;
        this.difficulty = difficulty;
        this.mapName = mapName;
        this.keyID = keyID;
    }

//    public DiscreteCoordinates getWestPortalSpawn() {
//        return new DiscreteCoordinates(getSizeSide() + 1, getSizeSide() / 2);
//    }
//    public DiscreteCoordinates getEstPortalSpawn() {
//        return new DiscreteCoordinates(0, getSizeSide() / 2);
//    }
//    public DiscreteCoordinates getNorthPortalSpawn(){
//        return new DiscreteCoordinates(getSizeSide()/2, 0);
//    }
//    public DiscreteCoordinates getSouthPortalSpawn(){
//        return new DiscreteCoordinates(getSizeSide()/2, getSizeSide() + 1);
//    }

    /**
     * @return the player's spawn position in the area
     */
    @Override
    public DiscreteCoordinates getPlayerSpawnPosition() {
        return new DiscreteCoordinates(0, 0);
    }


    @Override
    public String getTitle() {
        return "icmaze/" + mapName + "/" + String.valueOf(keyID);
    }

}
