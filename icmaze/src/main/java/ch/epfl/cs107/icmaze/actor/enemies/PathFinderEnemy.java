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
     * Default AreaEntity constructor
     *
     * @param area        (Area): Owner area. Not null
     * @param orientation (Orientation): Initial orientation of the entity in the Area. Not null
     * @param position    (DiscreteCoordinate): Initial position of the entity in the Area. Not null
     * @param maxHP
     */
    public PathFinderEnemy(Area area, Orientation orientation, DiscreteCoordinates position, int maxHP, int perceptionRadius) {
        super(area, orientation, position, maxHP);
        this.perceptionRadius = perceptionRadius;
    }

    protected ICMazePlayer getTarget() {
        return target;
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        List<DiscreteCoordinates> cells = new ArrayList<>();
        DiscreteCoordinates current = getCurrentMainCellCoordinates();

        // Carré de rayon perceptionRadius
        for (int dx = -perceptionRadius; dx <= perceptionRadius; dx++) {
            for (int dy = -perceptionRadius; dy <= perceptionRadius; dy++) {
                cells.add(new DiscreteCoordinates(current.x + dx, current.y + dy));
            }
        }
        return cells;
    }

    @Override
    public boolean wantsCellInteraction() {
        return false;
    }

    @Override
    public boolean wantsViewInteraction() { return true; }


    public abstract Orientation getOrientation();


}
