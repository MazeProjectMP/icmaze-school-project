package ch.epfl.cs107.icmaze.actor.rocks;

import ch.epfl.cs107.icmaze.RandomGenerator;
import ch.epfl.cs107.icmaze.actor.collectable.Heart;
import ch.epfl.cs107.icmaze.area.maps.MazeLevels;
import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

import java.util.Collections;
import java.util.List;


/**
 * Represents a destructible rock in the game.
 * <p>
 * A rock is an obstacle that blocks movement while intact. It can be damaged from a distance,
 * becomes temporarily immune after taking damage, and disappears with a vanishing animation
 * when destroyed. When the animation completes, it may spawn a {@link Heart} and updates the
 * walkable graph of the current {@link MazeLevels}.
 * </p>
 */

public class CommonRock extends Rock {
    /**
     * Creates a new rock at the given position in the specified area.
     * @param area (Area): the area where the rock is located
     * @param position (DiscreteCoordinates): the initial position of the rock
     */
    public CommonRock(Area area, DiscreteCoordinates position) {
        super(area, position, 3, "rock.2");
    }

    /**
     * Spawns loot when the rock is destroyed and removes it from the area.
     * With a probability of 50%, a {@link Heart} is spawned at the rock's
     * current position. The cell is then marked as walkable in the maze graph,
     * and the rock is unregistered from the area.
     */
    @Override
    public void lootSpawn(){
        if (RandomGenerator.rng.nextDouble() < 0.5) {
            Heart heart = new Heart(getOwnerArea(), getCurrentMainCellCoordinates());
            getOwnerArea().registerActor(heart);
        }
        ((MazeLevels) getOwnerArea()).addWalkableCellToGraph(getCurrentMainCellCoordinates());
        getOwnerArea().unregisterActor(this);
    }
}

