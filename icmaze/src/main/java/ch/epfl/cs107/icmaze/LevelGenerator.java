package ch.epfl.cs107.icmaze;

import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.icmaze.area.maps.*;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Utility class responsible for procedural generation of ICMaze levels.
 * Spawn,then a sequence of maze areas, then the BossArea
 */
public final class LevelGenerator {

    private static final int DEFAULT_MAZE_COUNT = 3;
    private static final int START_KEY_ID = Integer.MAX_VALUE;
    private static final ICMazeArea.AreaPortals[] FORWARD_DIRECTIONS = { ICMazeArea.AreaPortals.N, ICMazeArea.AreaPortals.E, ICMazeArea.AreaPortals.S};
    /**
     * Generates a linear level: Spawn + numberOfMazeAreas labyrinth areas + BossArea.
     * If numberOfMazeAreas == 0, BossArea is connected directly after Spawn.
     * @param game              the game instance
     * @param numberOfMazeAreas number of labyrinth areas to generate
     * @return an array of areas in loading order (Spawn ... BossArea)
     */
    public static ICMazeArea[] generateLine(ICMaze game, int numberOfMazeAreas) {
        final int length = (numberOfMazeAreas < 0) ? DEFAULT_MAZE_COUNT : numberOfMazeAreas;

        List<ICMazeArea> areas = new ArrayList<>(length + 2);
        Set<DiscreteCoordinates> occupiedPositions = new HashSet<>();

        Spawn spawn = new Spawn();
        areas.add(spawn);

        DiscreteCoordinates currentPosition = new DiscreteCoordinates(0, 0);
        occupiedPositions.add(currentPosition);

        ICMazeArea previousArea = spawn;

        int exitKeyId = START_KEY_ID;
        int nextAreaKeyId = START_KEY_ID - 1;

        for (int i = 0; i < length; i++) {
            double progress = (double) (i + 1) / length;
            int difficulty = calculateDifficulty(progress);

            ICMazeArea.AreaPortals exitDirection;
            if (i == 0) {
                exitDirection = ICMazeArea.AreaPortals.E; // Par defaut le portail a partir du spawn est EST
            } else {
                exitDirection = chooseForwardFreeDirection(currentPosition, occupiedPositions);
            }
            DiscreteCoordinates nextPosition = getNextPosition(currentPosition, exitDirection);
            ICMazeArea.AreaPortals entryDirection = getOppositeDirection(exitDirection);

            MazeLevels newArea = createMazeAreaByProgress(progress, difficulty, entryDirection, exitDirection, nextAreaKeyId);

            connectAreasWithKey(previousArea, exitDirection, newArea, entryDirection, exitKeyId);

            areas.add(newArea);
            occupiedPositions.add(nextPosition);

            previousArea = newArea;
            currentPosition = nextPosition;

            exitKeyId = nextAreaKeyId;
            nextAreaKeyId--;
        }

        ICMazeArea.AreaPortals bossExitDirection;
        if (length == 0) {
            bossExitDirection = ICMazeArea.AreaPortals.E; // Si on vient directement du SPAWN
        } else {
            bossExitDirection = chooseForwardFreeDirection(currentPosition, occupiedPositions);
        }

        DiscreteCoordinates bossPosition = getNextPosition(currentPosition, bossExitDirection);
        ICMazeArea.AreaPortals bossEntryDirection = getOppositeDirection(bossExitDirection);
        BossArea bossArea = new BossArea();
        connectAreasWithKey(previousArea, bossExitDirection, bossArea, bossEntryDirection, exitKeyId);
        bossArea.lockPortalWithKey(bossEntryDirection, -1, previousArea.getTitle()); // Piege fatal
        areas.add(bossArea);
        occupiedPositions.add(bossPosition);

        return areas.toArray(new ICMazeArea[0]);
    }

    /**
     * Chooses a free direction among {N, E, S} such that the target position is not occupied.
     * @param currentPosition   current grid position in the fictitious reference
     * @param occupiedPositions set of already used positions
     * @return a valid forward direction
     */
    private static ICMazeArea.AreaPortals chooseForwardFreeDirection(DiscreteCoordinates currentPosition, Set<DiscreteCoordinates> occupiedPositions) {
        List<ICMazeArea.AreaPortals> candidates = new ArrayList<>(3);
        for (ICMazeArea.AreaPortals direction : FORWARD_DIRECTIONS) {
            DiscreteCoordinates target = getNextPosition(currentPosition, direction);
            if (!occupiedPositions.contains(target)) {
                candidates.add(direction);
            }
        }
        return candidates.get(RandomGenerator.rng.nextInt(candidates.size()));
    }

    /**
     * Computes the next position in the fictitious placement grid for a given direction.
     * @param currentPosition current position
     * @param direction       movement direction
     * @return next position
     */
    private static DiscreteCoordinates getNextPosition(DiscreteCoordinates currentPosition, ICMazeArea.AreaPortals direction) {
        return switch (direction) {
            case N -> new DiscreteCoordinates(currentPosition.x, currentPosition.y + 1);
            case S -> new DiscreteCoordinates(currentPosition.x, currentPosition.y - 1);
            case E -> new DiscreteCoordinates(currentPosition.x + 1, currentPosition.y);
            case W -> new DiscreteCoordinates(currentPosition.x - 1, currentPosition.y);
        };
    }

    /**
     * Converts the generation progress (0..1] into a difficulty level.
     * The mapping follows the specification using (int)(progress * 4).
     * @param progress generation progress (in (0, 1])
     * @return one of {@link Difficulty#EASY}, {@link Difficulty#MEDIUM}, {@link Difficulty#HARD}, {@link Difficulty#HARDEST}
     */
    private static int calculateDifficulty(double progress) {
        int bucket = (int) (progress * 4);
        return switch (bucket) {
            case 0 -> Difficulty.EASY;
            case 1 -> Difficulty.MEDIUM;
            case 2 -> Difficulty.HARD;
            default -> Difficulty.HARDEST;
        };
    }

    /**
     * Creates a maze area (Small/Medium/Large) using the probabilistic rule based on progress.
     * @param progress   generation progress
     * @param difficulty difficulty constant for the created area
     * @param entry      entry portal direction for the created area
     * @param exit       exit portal direction for the created area
     * @param keyId      key id associated with the created maze area (used later for its exit logic)
     * @return a new maze area instance
     */
    private static MazeLevels createMazeAreaByProgress(double progress, int difficulty, ICMazeArea.AreaPortals entry, ICMazeArea.AreaPortals exit, int keyId) {
        double r = RandomGenerator.rng.nextDouble();
        if (r < progress * progress) return new LargeArea(entry, exit, keyId, difficulty);
        if (r < progress) return new MediumArea(entry, exit, keyId, difficulty);
        return new SmallArea(entry, exit, keyId, difficulty);
    }

    /**
     * Returns the opposite portal direction.
     * @param direction portal direction
     * @return opposite direction
     */
    private static ICMazeArea.AreaPortals getOppositeDirection(ICMazeArea.AreaPortals direction) {
        return switch (direction) {
            case N -> ICMazeArea.AreaPortals.S;
            case S -> ICMazeArea.AreaPortals.N;
            case E -> ICMazeArea.AreaPortals.W;
            case W -> ICMazeArea.AreaPortals.E;
        };
    }

    /**
     * Connects two areas with a bidirectional portal setup.
     * The portal in fromArea is locked with keyID.
     * @param fromArea      source area
     * @param fromDirection portal direction in source area
     * @param toArea        destination area
     * @param toDirection   portal direction in destination area (entry side)
     * @param keyId         key identifier required to unlock the source portal
     */
    private static void connectAreasWithKey(ICMazeArea fromArea, ICMazeArea.AreaPortals fromDirection, ICMazeArea toArea, ICMazeArea.AreaPortals toDirection, int keyId) {
        fromArea.lockPortalWithKey(fromDirection, keyId, toArea.getTitle());
        fromArea.setDestinationCoordinate(fromDirection, getArrivalCoordinates(toArea, toDirection));
        toArea.openPortal(toDirection, fromArea.getTitle());
        toArea.setDestinationCoordinate(toDirection, getArrivalCoordinates(fromArea, fromDirection));
    }

    /**
     * Computes arrival coordinates inside an area when entering through a given portal.
     * @param area   the area being entered
     * @param portal entry portal direction
     * @return arrival coordinates inside the area
     */
    private static DiscreteCoordinates getArrivalCoordinates(ICMazeArea area, ICMazeArea.AreaPortals portal) {
        int size = area.getSizeSide();
        return switch (portal) {
            case N -> new DiscreteCoordinates(size / 2 + 1, size);
            case S -> new DiscreteCoordinates(size / 2 + 1, 1);
            case E -> new DiscreteCoordinates(size, size / 2 + 1);
            case W -> new DiscreteCoordinates(1, size / 2 + 1);
        };
    }
}
