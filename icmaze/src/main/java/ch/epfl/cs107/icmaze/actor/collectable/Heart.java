package ch.epfl.cs107.icmaze.actor.collectable;

import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.CollectableAreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.math.*;

import java.util.Collections;
import java.util.List;

/**
 * Represents a collectable heart item in the ICMaze game.
 * <p>
 * A {@code Heart} is a non-blocking collectable entity that restores or represents
 * player health. It is displayed using a looping animation and can be interacted
 * with through cell interactions. Once collected, the heart becomes invisible and
 * is removed from the area.
 * </p>
 */


public class Heart extends CollectableAreaEntity {

    private final static int ANIMATION_DURATION =24;

    private boolean isVisible;

    private Animation animatedHeart;

    /**
     * Creates a new heart at the given position in the specified area.
     * @param currentArea (Area): the area where the heart is located
     * @param position (DiscreteCoordinates): the initial position of the heart
     */
    public Heart(Area currentArea, DiscreteCoordinates position) {
        super(currentArea, Orientation.DOWN, position);

        isVisible = true;

        animatedHeart = new Animation("icmaze/heart", 4, 1, 1, this, 16, 16, ANIMATION_DURATION / 4, true);
    }

    /**
     * Draws the heart on the canvas if it is visible.
     * @param canvas (Canvas): the canvas to draw on
     */
    @Override
    public void draw(ch.epfl.cs107.play.window.Canvas canvas) {
        if (isVisible) {
            animatedHeart.draw(canvas);
        }
    }

    /**
     * Updates the heart animation if it is visible.
     * @param deltaTime (float): time elapsed since last update
     */
    @Override
    public void update(float deltaTime) {
        if (isVisible) {
            animatedHeart.update(deltaTime);
        }
    }

    /**
     * Gets the list of cells currently occupied by the heart.
     * @return (List<DiscreteCoordinates>): a singleton list containing the main cell coordinates
     */
    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    /**
     * Indicates whether the heart blocks the cell it occupies.
     * @return (boolean): {@code false}, as the heart does not block movement
     */
    @Override
    public boolean takeCellSpace() {
        return false;
    }

    /**
     * Indicates whether the heart can be interacted with via cell interaction.
     * @return (boolean): {@code true} if the heart is cell-interactable
     */
    @Override
    public boolean isCellInteractable() {
        return true;
    }

    /**
     * Indicates whether the heart can be interacted with from a distance.
     * @return (boolean): {@code false}, as the heart is not view-interactable
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

    /**
     * Hides the heart and removes it from the area.
     */
    public void hide() {
        if (isVisible) {
            isVisible = false;
            getOwnerArea().unregisterActor(this);
        }
    }
}
