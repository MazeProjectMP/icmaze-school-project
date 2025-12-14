package ch.epfl.cs107.icmaze.actor.enemies;

import ch.epfl.cs107.icmaze.ICMaze;
import ch.epfl.cs107.icmaze.actor.ICMazePlayer;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

import java.util.ArrayList;
import java.util.List;

public abstract class PathFinderEnemy extends Enemy {

    private final int perceptionRadius;
    private ICMazePlayer target;

    /**
     * Creates a new path-finder enemy.
     * @param area (Area): the area where the enemy is located
     * @param orientation (Orientation): initial orientation of the enemy
     * @param position (DiscreteCoordinates): initial position of the enemy
     * @param maxHP (int): maximum health points of the enemy
     * @param perceptionRadius (int): perception radius of the enemy
     */
    public PathFinderEnemy(Area area,
                           Orientation orientation,
                           DiscreteCoordinates position,
                           int maxHP,
                           int perceptionRadius) {
        super(area, orientation, position, maxHP);
        this.perceptionRadius = perceptionRadius;
    }

    /**
     * Gets the current target of the enemy.
     * @return (ICMazePlayer): the targeted player, may be {@code null}
     */
    protected ICMazePlayer getTarget() {
        return target;
    }

    /**
     * Gets the list of cells visible by the enemy.
     * The field of view is a square centered on the enemy with a radius
     * equal to the perception radius.
     * @return (List<DiscreteCoordinates>): list of visible cells
     */
    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        List<DiscreteCoordinates> cells = new ArrayList<>();
        DiscreteCoordinates current = getCurrentMainCellCoordinates();

        for (int dx = -perceptionRadius; dx <= perceptionRadius; dx++) {
            for (int dy = -perceptionRadius; dy <= perceptionRadius; dy++) {
                cells.add(new DiscreteCoordinates(current.x + dx, current.y + dy));
            }
        }
        return cells;
    }

    /**
     * Indicates whether the enemy wants cell interactions.
     * @return (boolean): {@code false}
     */
    @Override
    public boolean wantsCellInteraction() {
        return false;
    }

    /**
     * Indicates whether the enemy wants view interactions.
     * @return (boolean): {@code true}
     */
    @Override
    public boolean wantsViewInteraction() {
        return true;
    }

    /**
     * Computes the next orientation of the enemy.
     * @return (Orientation): the next orientation to take
     */
    public abstract Orientation getNextOrientation();
}
