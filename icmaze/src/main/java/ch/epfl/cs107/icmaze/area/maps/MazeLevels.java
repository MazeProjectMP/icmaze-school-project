package ch.epfl.cs107.icmaze.area.maps;
import ch.epfl.cs107.icmaze.Difficulty;
import ch.epfl.cs107.icmaze.MazeGenerator;
import ch.epfl.cs107.icmaze.actor.Rock;
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

    @Override
    public void createArea() {
        super.createArea();
        generateMaze(); // Generation de maxe
    }


    public void generateMaze(){
        int[][] maze = MazeGenerator.createMaze(getSizeSide(), getSizeSide(), Difficulty.HARDEST);
        for(int y = 0; y < getSizeSide(); y++){
            for(int x = 0; x < getSizeSide(); x++){
                if(maze[x][y] == 1 && notInSpawn(x,y)){
                    Rock rock = new Rock(this, new DiscreteCoordinates(x,y));
                    registerActor(rock);
                }
            }
        }
    }
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
