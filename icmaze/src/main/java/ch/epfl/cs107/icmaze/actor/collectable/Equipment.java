package ch.epfl.cs107.icmaze.actor.collectable;

import ch.epfl.cs107.play.areagame.actor.CollectableAreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;


public class Equipment extends CollectableAreaEntity {

    /**
     * Creates a new equipment at the given position in the specified area.
     * @param area (Area): the area where the equipment is located
     * @param position (DiscreteCoordinates): the initial position of the equipment
     */
    public Equipment(Area area, DiscreteCoordinates position) {
        super(area, Orientation.DOWN, position);
    }

    /**
     * Draws the equipment on the canvas.
     * @param canvas (Canvas): the canvas to draw on
     */
    @Override
    public void draw(Canvas canvas) {
    }

    /**
     * Updates the equipment state.
     * @param deltaTime (float): time elapsed since last update
     */
    @Override
    public void update(float deltaTime) {
    }

    /**
     * Gets the list of cells currently occupied by the equipment.
     * @return (List<DiscreteCoordinates>): a singleton list containing the main cell coordinates
     */
    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    /**
     * Indicates whether the equipment blocks the cell it occupies.
     * @return (boolean): {@code false}, as equipment does not block movement
     */
    @Override
    public boolean takeCellSpace() {
        return false;
    }

    /**
     * Indicates whether the equipment can be interacted with via cell interaction.
     * @return (boolean): {@code true} if the equipment is cell-interactable
     */
    @Override
    public boolean isCellInteractable() {
        return true;
    }

    /**
     * Indicates whether the equipment can be interacted with from a distance.
     * @return (boolean): {@code false}, as equipment is not view-interactable
     */
    @Override
    public boolean isViewInteractable() {
        return false;
    }

    /**
     * Accepts an interaction from an area interaction visitor.
     * @param v (AreaInteractionVisitor): the interaction visitor
     * @param isCellInteraction (boolean): whether the interaction is a cell interaction
     */
    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
    }
}
