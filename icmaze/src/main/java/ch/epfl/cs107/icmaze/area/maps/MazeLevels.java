package ch.epfl.cs107.icmaze.area.maps;
import ch.epfl.cs107.icmaze.Difficulty;
import ch.epfl.cs107.icmaze.MazeGenerator;
import ch.epfl.cs107.icmaze.RandomGenerator;
import ch.epfl.cs107.icmaze.actor.enemies.Wizard;
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

/**
 * Represents a generic maze-based level in the game.
 * <p>
 * A {@code MazeLevels} instance generates a procedural maze, places walls,
 * enemies and a key, and manages win conditions. It also maintains a navigation
 * graph used by path-finding enemies.
 * </p>
 */
public class MazeLevels extends ICMazeArea {
    private AreaPortals areaPortalEntry;
    private AreaPortals areaPortalExit;
    private int difficulty;
    private static final int MAX_NB_LOGMONSTER = 3;
    private static final double PROBA_SPAWN_FIRST_WIZARD = 0.30;
    private static final double PROBA_SPAWN_SECOND_WIZARD = 0.10;

    private int keyID;

    private final String mapName;

    private Key key;

    private ArrayList<Rock> walls;
    private ArrayList<LogMonster> logMonsters;
    private ArrayList<Wizard> wizards;

    /**
     * Creates a new maze level.
     *
     * @param areaPortalEntry (AreaPortals): entry portal configuration
     * @param areaPortalExit  (AreaPortals): exit portal configuration
     * @param difficulty      (int): difficulty level
     * @param keyID           (int): identifier of the required key
     * @param mapName         (String): name of the map
     * @param sizeSide        (int): side length of the maze
     */
    public MazeLevels(AreaPortals areaPortalEntry, AreaPortals areaPortalExit, int difficulty, int keyID, String mapName, int sizeSide) {
        super(mapName, sizeSide);

        this.areaPortalEntry = areaPortalEntry;
        this.areaPortalExit = areaPortalExit;
        this.difficulty = difficulty;
        this.mapName = mapName;
        this.keyID = keyID;

        walls = new ArrayList<>();
        logMonsters = new ArrayList<>();
        wizards = new ArrayList<>();
    }

    /**
     * Creates and initializes the area.
     * Generates the maze, places the key and spawns enemies.
     */
    @Override
    public void createArea() {
        super.createArea();
        generateMaze();

        key = new Key(this, randomSpawnPosition(), this.keyID);
        registerActor(key);
        createMonsters();
    }

    /** Creates all enemies in the maze. */
    private void createMonsters() {
        createLogMonsters();
        createWizards();
    }

    /**
     * Spawns log monsters depending on the difficulty level.
     */
    private void createLogMonsters() {
        double diffRatio = Math.min(1.0, (double) Difficulty.HARDEST / difficulty);
        double addProbability = 0.25 + 0.60 * diffRatio;

        for (int i = 0; i < MAX_NB_LOGMONSTER; i++) {
            if (RandomGenerator.rng.nextDouble() < addProbability) {

                LogMonster.State initialState = determineLogMonsterInitialState(diffRatio);
                Orientation initialOrientation = Orientation.values()[RandomGenerator.nextInt(4)];
                DiscreteCoordinates initialPosition = randomSpawnPosition();

                if (initialPosition == null) continue;

                int maxHP = 3;

                LogMonster logMonster = new LogMonster(
                        this,
                        initialOrientation,
                        initialPosition,
                        maxHP,
                        initialState,
                        areaGraph,
                        difficulty
                );

                logMonsters.add(logMonster);
                registerActor(logMonster);
            }
        }
    }

    /**
     * Spawns wizard enemies with fixed probabilities.
     */
    private void createWizards() {
        /// 30 percent for the first
        if (RandomGenerator.rng.nextDouble() < PROBA_SPAWN_FIRST_WIZARD) {
            spawnOneWizard();

            /// 10 percent for the second
            if (RandomGenerator.rng.nextDouble() < PROBA_SPAWN_SECOND_WIZARD) {
                spawnOneWizard();
            }
        }
    }

    /**
     * Spawns a single wizard at a random valid position.
     */
    private void spawnOneWizard() {
        DiscreteCoordinates initialPosition = randomSpawnPosition();

        Wizard wizard = new Wizard(this, initialPosition, areaGraph);

        wizards.add(wizard);
        registerActor(wizard);
    }

    /**
     * Updates the area logic.
     * Ends the level when the winning condition is met.
     * @param deltatime (float): elapsed time since last update
     */
    @Override
    public void update(float deltatime){
        super.update(deltatime);
        if(isOn()){
            winGame();
        }
    }

    /**
     * Handles the end-of-level behavior.
     * All enemies are neutralized and walls are destroyed.
     */
    private void winGame(){
        for(LogMonster logMonster : logMonsters){
            logMonster.sleep();
        }
        for(Rock rock : walls){
            rock.breakWall();
        }
        for (Wizard wizard : wizards){
            wizard.vanish();
        }
    }

    /**
     * Determines the initial state of a log monster based on difficulty.
     * @param diffRatio (double): normalized difficulty ratio
     * @return (LogMonster.State): initial monster state
     */
    private LogMonster.State determineLogMonsterInitialState(double diffRatio) {
        double pTarget = 0.10 + 0.70 * diffRatio;
        double pRandom = 0.20;
        double pSleeping = 1.0 - (pTarget + pRandom);

        double r = RandomGenerator.rng.nextDouble();

        if (r < pSleeping) {
            return LogMonster.State.SLEEPING; /// Sleeping state
        } else if (r < pSleeping + pRandom) {
            return LogMonster.State.MOVING; ///State of random movement
        } else {
            return LogMonster.State.TARGETING; /// Chasing state
        }
    }

    /**
     * Checks whether a cell is not part of a portal spawn zone.
     * @param x (int): x-coordinate
     * @param y (int): y-coordinate
     * @return (boolean): {@code true} if the cell is allowed
     */
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

    /**
     * Adds a walkable cell to the area graph.
     * @param c the coordinates of the cell to add
     */
    public void addWalkableCellToGraph(DiscreteCoordinates c) {
        boolean left  = areaGraph.nodeExists(new DiscreteCoordinates(c.x - 1, c.y));
        boolean up    = areaGraph.nodeExists(new DiscreteCoordinates(c.x, c.y + 1));
        boolean right = areaGraph.nodeExists(new DiscreteCoordinates(c.x + 1, c.y));
        boolean down  = areaGraph.nodeExists(new DiscreteCoordinates(c.x, c.y - 1));

        areaGraph.addNode(c, left, up, right, down);
    }

    /**
     * Generates the maze layout of the area.
     * Walkable cells are added to the graph and walls are filled with rocks.
     */
    private void generateMaze(){
        int size = getSizeSide();
        int[][] maze = MazeGenerator.createMaze(size, size, Difficulty.HARDEST);
        boolean[][] chemins = new boolean[size + 2][size + 2]; // +2 pour les bords

        /// Placement of walls in the maze, choice between a rock or a path
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

        ///Determine connections around each walkable cell
        for (int x = 0; x < size + 2; x++) {
            for (int y = 0; y < size + 2; y++) {
                if (chemins[x][y]) {
                    DiscreteCoordinates coord = new DiscreteCoordinates(x, y);

                    /// Check connections in the four cardinal directions
                    boolean left = (x > 0) && chemins[x - 1][y];
                    boolean up = (y < size + 1) && chemins[x][y + 1];
                    boolean right = (x < size + 1) && chemins[x + 1][y];
                    boolean down = (y > 0) && chemins[x][y - 1];

                    areaGraph.addNode(coord, left, up, right, down);
                }
            }
        }
    }

    /**
     * Returns the identifier of the key associated with this area.
     * @return the key identifier
     */
    public int getKeyID() {
        return keyID;
    }

    /**
     * Returns a random walkable position in the area.
     * @return a random spawn position, or null if none is available
     */
    private DiscreteCoordinates randomSpawnPosition(){
        List<DiscreteCoordinates> possiblePositions = new ArrayList<>(areaGraph.keySet());  // Copie profonde des differentes possitions possibles

        if (!possiblePositions.isEmpty()) {
            Collections.shuffle(possiblePositions, RandomGenerator.rng);
            return new DiscreteCoordinates(possiblePositions.get(0).x, possiblePositions.get(0).y);
        }
        return null;
    }

    /**
     * Returns the title of the area.
     * @return the area title
     */
    @Override
    public String getTitle() {
        return "icmaze/" + mapName + "/" + String.valueOf(keyID);
    }

}
