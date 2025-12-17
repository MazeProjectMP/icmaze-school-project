package ch.epfl.cs107.icmaze.actor.collectable.rare;

import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.CollectableAreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

import java.util.Collections;
import java.util.List;

public abstract class RareItems extends CollectableAreaEntity {
    private int value;
    private boolean isVisible;
    public RareItems(Area area, DiscreteCoordinates position, int value) {
        super(area, Orientation.DOWN, position);
        this.value = value;
        isVisible = true;
    }

    public int getValue() {
        return value;
    }

    /**
     * Hides the treasure and removes it from the area.
     */
    public void hide() {
        if (isVisible) {
            isVisible = false;
            getOwnerArea().unregisterActor(this);
        }
    }

    public boolean isVisible() {
        return isVisible;
    }
    
    /**
     * Gets the list of cells currently occupied by the rare item.
     * @return (List<DiscreteCoordinates>): a singleton list containing the main cell coordinates
     */
    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    /**
     * Indicates whether the rare item blocks the cell it occupies.
     * @return (boolean): {@code false}, as the rare item does not block movement
     */
    @Override
    public boolean takeCellSpace() {
        return false;
    }

    /**
     * Indicates whether the rare item can be interacted with via cell interaction.
     * @return (boolean): {@code true} if the rare item is cell-interactable
     */
    @Override
    public boolean isCellInteractable() {
        return true;
    }

    /**
     * Indicates whether the rare item can be interacted with from a distance.
     * @return (boolean): {@code false}, as the rare item is not view-interactable
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
        ((ICMazeInteractionVisitor) v).interactWith(this, isCellInteraction);
    }
}
