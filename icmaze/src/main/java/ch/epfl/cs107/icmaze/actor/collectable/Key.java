package ch.epfl.cs107.icmaze.actor.collectable;

import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.CollectableAreaEntity;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;

/**
 * Represents a collectible key in the game.
 * <p>
 * A key is a collectable entity that can be picked up by the player
 * and used to unlock specific elements of the maze.
 * </p>
 */
public class Key extends CollectableAreaEntity implements Interactable, Logic {

    private int identificator;

    private boolean isVisible ;

    private final Sprite key;

    private boolean collected;

    /**
     * Creates a new key at the given position in the specified area.
     * @param area (Area): the area where the key is located
     * @param position (DiscreteCoordinates): the initial position of the key
     * @param identificator (int): unique identifier of the key
     */
    public Key(Area area, DiscreteCoordinates position, int identificator) {
        super(area, Orientation.DOWN, position);
        this.identificator = identificator;
        isVisible = true;
        key = new Sprite("icmaze/key", .75f, .75f, this);
        collected = false;
    }

    /**
     * Gets the identifier of the key.
     * @return (int): the key identifier
     */
    public int getIdentificator() {
        return identificator;
    }

    /**
     * Draws the key on the canvas if it is visible.
     * @param canvas (Canvas): the canvas to draw on
     */
    @Override
    public void draw(Canvas canvas) {
        if (isVisible) {
            key.draw(canvas);
        }
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

    /**
     * Hides the key and removes it from the area.
     */
    public void hide() {
        if (isVisible) {
            isVisible = false;
            collected = true;
            getOwnerArea().unregisterActor(this);
        }
    }

    /**
     * Indicates whether the element is in the active (collected) state.
     * @return {@code true} if the item has been collected, {@code false} otherwise
     */
    @Override
    public boolean isOn() {
        return collected;
    }

    /**
     * Indicates whether the element is in the inactive (not collected) state.
     * @return {@code true} if the item has not been collected, {@code false} otherwise
     */
    @Override
    public boolean isOff() {
        return !collected;
    }

    /**
     * Gets the list of cells currently occupied by the key.
     * @return (List<DiscreteCoordinates>): a singleton list containing the main cell coordinates
     */
    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    /**
     * Indicates whether the key blocks the cell it occupies.
     * @return (boolean): {@code false}, as the key does not block movement
     */
    @Override
    public boolean takeCellSpace() {
        return false;
    }

    /**
     * Indicates whether the key can be interacted with via cell interaction.
     * @return (boolean): {@code true} if the key is cell-interactable
     */
    @Override
    public boolean isCellInteractable() {
        return true;
    }

    /**
     * Indicates whether the key can be interacted with from a distance.
     * @return (boolean): {@code false}, as the key is not view-interactable
     */
    @Override
    public boolean isViewInteractable() {
        return false;
    }
}
