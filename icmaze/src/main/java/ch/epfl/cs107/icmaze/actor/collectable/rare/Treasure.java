package ch.epfl.cs107.icmaze.actor.collectable.rare;

import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.CollectableAreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.engine.actor.TextGraphics;
import ch.epfl.cs107.play.math.*;

import java.awt.*;
import java.util.Collections;
import java.util.List;


public class Treasure extends RareItems {

    private final static int ANIMATION_DURATION =24;

    private boolean isVisible;

    private Sprite treasure;

    private RegionOfInterest treasureRegion;

    private final TextGraphics message;

    /**
     * Creates a new treasure at the given position in the specified area.
     *
     * @param currentArea (Area): the area where the treasure is located
     * @param position (DiscreteCoordinates): the initial position of the treasure
     */
    public Treasure(Area currentArea, DiscreteCoordinates position) {
        super(currentArea, Orientation.DOWN, position, 100);

        isVisible = true;

        message = new TextGraphics("treasure", 0.2f, new Color(133,21,0));
        message.setParent(this);
        message.setAnchor(new Vector(0.05f, 1f));
        treasureRegion = new RegionOfInterest(0, 128, 18, 20);
        treasure = new Sprite("icmaze/supplies_objects", 1, 1, this, treasureRegion);
    }

    /**
     * Draws the treasure on the canvas if it is visible.
     * @param canvas (Canvas): the canvas to draw on
     */
    @Override
    public void draw(ch.epfl.cs107.play.window.Canvas canvas) {
        if (isVisible) {
            treasure.draw(canvas);
            message.draw(canvas);
        }
    }

    /**
     * Updates the treasure animation if it is visible.
     * @param deltaTime (float): time elapsed since last update
     */
    @Override
    public void update(float deltaTime) {

    }

    /**
     * Gets the list of cells currently occupied by the treasure.
     * @return (List<DiscreteCoordinates>): a singleton list containing the main cell coordinates
     */
    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    /**
     * Indicates whether the treasure blocks the cell it occupies.
     * @return (boolean): {@code false}, as the treasure does not block movement
     */
    @Override
    public boolean takeCellSpace() {
        return false;
    }

    /**
     * Indicates whether the treasure can be interacted with via cell interaction.
     * @return (boolean): {@code true} if the treasure is cell-interactable
     */
    @Override
    public boolean isCellInteractable() {
        return true;
    }

    /**
     * Indicates whether the treasure can be interacted with from a distance.
     * @return (boolean): {@code false}, as the treasure is not view-interactable
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
     * Hides the treasure and removes it from the area.
     */
    public void hide() {
        if (isVisible) {
            isVisible = false;
            getOwnerArea().unregisterActor(this);
        }
    }
}
