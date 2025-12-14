package ch.epfl.cs107.icmaze.actor.collectable;

import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;


public class Pickaxe extends Equipment {

    private final Sprite pickaxe;

    /** Indicates whether the pickaxe is visible. */
    private boolean isVisible ;

    /**
     * Creates a new pickaxe at the given position in the specified area.
     *
     * @param area (Area): the area where the pickaxe is located
     * @param position (DiscreteCoordinates): the initial position of the pickaxe
     */
    public Pickaxe(Area area, DiscreteCoordinates position) {
        super(area, position);
        pickaxe = new Sprite("icmaze/pickaxe", .75f, .75f, this);
        isVisible = true;
    }

    /**
     * Draws the pickaxe on the canvas if it is visible.
     * @param canvas (Canvas): the canvas to draw on
     */
    @Override
    public void draw(Canvas canvas) {
        if (isVisible) {
            pickaxe.draw(canvas);
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
     * Hides the pickaxe and removes it from the area.
     * This method is typically called when the pickaxe is collected by the player.
     */
    public void hide() {
        if (isVisible) {
            isVisible = false;
            getOwnerArea().unregisterActor(this);
        }
    }
}
