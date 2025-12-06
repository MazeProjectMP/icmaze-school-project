package ch.epfl.cs107.icmaze.area.maps;
import ch.epfl.cs107.icmaze.Difficulty;
import ch.epfl.cs107.icmaze.MazeGenerator;
import ch.epfl.cs107.icmaze.RandomGenerator;
import ch.epfl.cs107.icmaze.actor.Rock;
import ch.epfl.cs107.icmaze.actor.collectable.Key;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.play.engine.actor.Actor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        generateMaze(); // Generation de maze
        Key key = new Key(this, randomSpawnPosition(), Integer.MAX_VALUE);
        registerActor(key);
    }

    private boolean notInSpawn(int x, int y) {
        int[] portalsX = {0, 0, 1, 1, getSizeSide(), getSizeSide(), getSizeSide()-1, getSizeSide()-1, getSizeSide()/2, getSizeSide()/2 - 1, getSizeSide()/2, getSizeSide()/2 - 1, getSizeSide()/2, getSizeSide()/2 - 1, getSizeSide()/2, getSizeSide()/2 - 1};
        int[] portalsY = {getSizeSide()/2, getSizeSide()/2 - 1, getSizeSide()/2, getSizeSide()/2 - 1, getSizeSide()/2, getSizeSide()/2 - 1, getSizeSide()/2, getSizeSide()/2 - 1, 0, 0, 1, 1, getSizeSide(), getSizeSide(), getSizeSide()-1, getSizeSide()-1};
        for(int i = 0; i < portalsX.length; i++){
            if ((portalsX[i] + 1)  == x && (portalsY[i] + 1) == y){
                return false;
            }
        }
        return true;
    }
    public void generateMaze(){
        int size = getSizeSide();
        int[][] maze = MazeGenerator.createMaze(size, size, Difficulty.HARDEST);
        boolean[][] chemins = new boolean[size + 2][size + 2]; // +2 pour les bords

        // Mise en place des murs du maze, si c'est un mur on on place un rocher, sinon c'est un chemin
        for(int y = 0; y < getSizeSide() ; y++){
            for(int x = 0; x < getSizeSide() ; x++){
                if(maze[y][x] == 1 && notInSpawn(x+1,y+1)){
                    Rock rock = new Rock(this, new DiscreteCoordinates(x+1,y+1)); // +1 car on place le maze dans l'aire jouable
                    registerActor(rock);
                }else{
                    chemins[x + 1][y + 1] = true;
                }
            }
        }

        // Determination des connexions autour d'une cellule pratiquable
        for (int x = 0; x < size + 2; x++) {
            for (int y = 0; y < size + 2; y++) {
                if (chemins[x][y]) {
                    DiscreteCoordinates coord = new DiscreteCoordinates(x, y);

                    // Verification dans des connexions dans les 4 directions
                    boolean left = (x > 0) && chemins[x - 1][y];
                    boolean up = (y < size + 1) && chemins[x][y + 1];
                    boolean right = (x < size + 1) && chemins[x + 1][y];
                    boolean down = (y > 0) && chemins[x][y - 1];

                    areaGraph.addNode(coord, left, up, right, down);
                }
            }
        }
    }

    public int getKeyID() {
        return keyID;
    }

    private DiscreteCoordinates randomSpawnPosition(){
        List<DiscreteCoordinates> possiblePositions = new ArrayList<>(areaGraph.keySet());  // Copie (copie profonde    ) les differentes possitions possibles

        if (!possiblePositions.isEmpty()) {
            Collections.shuffle(possiblePositions, RandomGenerator.rng);
            return new DiscreteCoordinates(possiblePositions.get(0).x, possiblePositions.get(0).y);
        }
        return null;
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
