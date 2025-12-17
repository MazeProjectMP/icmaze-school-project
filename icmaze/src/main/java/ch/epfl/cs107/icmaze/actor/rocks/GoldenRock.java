package ch.epfl.cs107.icmaze.actor.rocks;

import ch.epfl.cs107.icmaze.actor.collectable.rare.GoldCoin;
import ch.epfl.cs107.icmaze.area.maps.MazeLevels;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
/**
 * Represents a rock containing a gold coin in the ICMaze game.
 * <p>
 * A {@code GoldenRock} is a destructible obstacle that blocks movement while intact.
 * When destroyed, it always spawns a {@link GoldCoin} at its position, updates the
 * walkable graph of the current {@link MazeLevels}, and is then removed from the area.
 * </p>
 */

public class GoldenRock extends Rock{
    /**
     * Creates a new golden rock at the given position in the specified area.
     * @param area (Area): the area where the rock is located
     * @param position (DiscreteCoordinates): the initial position of the rock
     */
    public GoldenRock(Area area, DiscreteCoordinates position) {
        super(area, position, 10, "icmaze/yellow_crystal_rock");
    }

    /**
     * Spawns a gold coin when the rock is destroyed and removes it from the area.
     * A {@link GoldCoin} is always spawned at the rock's current position.
     * The cell is then marked as walkable in the maze graph, and the rock
     * is unregistered from the area.
     */
    @Override
    void lootSpawn() {
        GoldCoin goldCoin = new GoldCoin(getOwnerArea(), getCurrentMainCellCoordinates());
        getOwnerArea().registerActor(goldCoin);
        ((MazeLevels) getOwnerArea()).addWalkableCellToGraph(getCurrentMainCellCoordinates());
        getOwnerArea().unregisterActor(this);
    }
}
