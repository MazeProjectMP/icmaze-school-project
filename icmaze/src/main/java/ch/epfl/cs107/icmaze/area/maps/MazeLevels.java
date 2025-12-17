package ch.epfl.cs107.icmaze.area.maps;
import ch.epfl.cs107.icmaze.Difficulty;
import ch.epfl.cs107.icmaze.MazeGenerator;
import ch.epfl.cs107.icmaze.RandomGenerator;
import ch.epfl.cs107.icmaze.actor.rocks.CommonRock;
import ch.epfl.cs107.icmaze.actor.collectable.Key;
import ch.epfl.cs107.icmaze.actor.enemies.LogMonster;
import ch.epfl.cs107.icmaze.actor.rocks.CrystalRock;
import ch.epfl.cs107.icmaze.actor.rocks.GoldenRock;
import ch.epfl.cs107.icmaze.actor.rocks.Rock;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MazeLevels extends ICMazeArea {
    private AreaPortals areaPortalEntry;
    private AreaPortals areaPortalExit;
    private int difficulty;
    private static final int MAX_NB_LOGMONSTER = 3;

    private int keyID;

    private final String mapName;

    private Key key;

    private ArrayList<Rock> walls;
    private ArrayList<LogMonster> logMonsters;

    public MazeLevels(AreaPortals areaPortalEntry, AreaPortals areaPortalExit, int difficulty, int keyID, String mapName, int sizeSide) {
        super(mapName, sizeSide);

        this.areaPortalEntry = areaPortalEntry;
        this.areaPortalExit = areaPortalExit;
        this.difficulty = difficulty;
        this.mapName = mapName;
        this.keyID = keyID;

        walls = new ArrayList<>();
        logMonsters = new ArrayList<>();
    }

    @Override
    public void createArea() {
        super.createArea();
        generateMaze(); // Generation de maze

        // Ajoute la cle
        key = new Key(this, randomSpawnPosition(), this.keyID);
        registerActor(key);
        // Cree les monstres
        createMonsters();
    }

    private void createMonsters(){
        double diffRatio = Math.min(1.0, (double) Difficulty.HARDEST / difficulty);
        double addProbability = 0.25 + 0.60 * diffRatio; // Probabilite d'ajout

        for (int i = 0; i < MAX_NB_LOGMONSTER; i++) {
                if (RandomGenerator.rng.nextDouble() < addProbability) {
                    // Etat initial
                    LogMonster.State initialState = determineLogMonsterInitialState(diffRatio);

                    // Orientation initiale
                    Orientation initialOrientation = Orientation.values()[RandomGenerator.nextInt(4)];

                    // Position initiale
                    DiscreteCoordinates initialPosition = randomSpawnPosition();

                    // Niveau de vie maximal
                    int maxHP = 3;

                    LogMonster logMonster = new LogMonster(this, initialOrientation, initialPosition, maxHP, initialState, areaGraph, difficulty);
                    logMonsters.add(logMonster);
                    this.registerActor(logMonster);
                }
            }
    }
    @Override
    public void update(float deltatime){
        super.update(deltatime);
        if(isOn()){
            winGame();
        }
    }

    private void winGame(){
        for(LogMonster logMonster : logMonsters){
            logMonster.sleep();
        }
        for(Rock rock : walls){
            rock.breakWall();
        }
    }

    // Determine l'Etat initiale du logMonster
    private LogMonster.State determineLogMonsterInitialState(double diffRatio) {
        double pTarget = 0.10 + 0.70 * diffRatio;
        double pRandom = 0.20;
        double pSleeping = 1.0 - (pTarget + pRandom);

        double r = RandomGenerator.rng.nextDouble();

        if (r < pSleeping) {
            return LogMonster.State.SLEEPING; // Etat dormant
        } else if (r < pSleeping + pRandom) {
            return LogMonster.State.MOVING; // Etat de deplacement aleatoire
        } else {
            return LogMonster.State.TARGETING; // Etat de chasse
        }
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

    public void addWalkableCellToGraph(DiscreteCoordinates c) {
        // voisins déjà "walkable" = ceux qui existent dans le graph
        boolean left  = areaGraph.nodeExists(new DiscreteCoordinates(c.x - 1, c.y));
        boolean up    = areaGraph.nodeExists(new DiscreteCoordinates(c.x, c.y + 1));
        boolean right = areaGraph.nodeExists(new DiscreteCoordinates(c.x + 1, c.y));
        boolean down  = areaGraph.nodeExists(new DiscreteCoordinates(c.x, c.y - 1));

        areaGraph.addNode(c, left, up, right, down);
    }


    private void generateMaze(){
        int size = getSizeSide();
        int[][] maze = MazeGenerator.createMaze(size, size, Difficulty.HARDEST);
        boolean[][] chemins = new boolean[size + 2][size + 2]; // +2 pour les bords

        // Mise en place des murs du maze, si c'est un mur on on place un rocher, sinon c'est un chemin
        for(int y = 0; y < getSizeSide() ; y++){
            for(int x = 0; x < getSizeSide() ; x++){
                if(maze[y][x] == 1 && notInSpawn(x+1,y+1)){
                    double pRandomRock = RandomGenerator.rng.nextDouble();
                    Rock chosenRock;
                    DiscreteCoordinates rockPosition = new DiscreteCoordinates(x + 1,y + 1);  // +1 car on place le maze dans l'aire jouable
                    if ( pRandomRock > 0.7) {
                        chosenRock = new CrystalRock(this, rockPosition);
                    } else if (0.6 < pRandomRock) {
                        chosenRock = new GoldenRock(this, rockPosition);
                    }else{
                        chosenRock = new CommonRock(this, rockPosition);
                    }
                    walls.add(chosenRock);
                    registerActor(chosenRock);
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
        List<DiscreteCoordinates> possiblePositions = new ArrayList<>(areaGraph.keySet());  // Copie profonde des differentes possitions possibles

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
