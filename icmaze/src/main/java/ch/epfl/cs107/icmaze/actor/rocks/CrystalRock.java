package ch.epfl.cs107.icmaze.actor.rocks;

import ch.epfl.cs107.icmaze.RandomGenerator;
import ch.epfl.cs107.icmaze.actor.collectable.Heart;
import ch.epfl.cs107.icmaze.actor.collectable.rare.Crystal;
import ch.epfl.cs107.icmaze.area.maps.MazeLevels;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

/**
 * Represents a rock containing a crystal in the ICMaze game.
 * <p>
 * A {@code CrystalRock} is a destructible obstacle that blocks movement while intact.
 * When destroyed, it always spawns a {@link Crystal} at its position, updates the
 * walkable graph of the current {@link MazeLevels}, and is then removed from the area.
 * </p>
 */

public class CrystalRock extends Rock {
    /**
     * Creates a new crystal rock at the given position in the specified area.
     * @param area (Area): the area where the rock is located
     * @param position (DiscreteCoordinates): the initial position of the rock
     */
    public CrystalRock(Area area, DiscreteCoordinates position) {
        super(area, position, 5, "icmaze/red_crystal_rock");
    }

    /**
     * Spawns a crystal when the rock is destroyed and removes it from the area.
     * A {@link Crystal} is always spawned at the rock's current position.
     * The cell is then marked as walkable in the maze graph, and the rock
     * is unregistered from the area.
     */
    @Override
    void lootSpawn() {
        Crystal crystal = new Crystal(getOwnerArea(), getCurrentMainCellCoordinates());
        getOwnerArea().registerActor(crystal);
        ((MazeLevels) getOwnerArea()).addWalkableCellToGraph(getCurrentMainCellCoordinates());
        getOwnerArea().unregisterActor(this);
    }
}
