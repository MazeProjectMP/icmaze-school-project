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

/**
 * Represents a high-value treasure collectable in the ICMaze game.
 * <p>
 * A {@code Treasure} is a rare and valuable item that can be collected by the player.
 * It is rendered using a specific region of a sprite sheet and displays a small text
 * label above it when visible. This class extends {@link RareItems} and inherits its
 * interaction and visibility behavior.
 * </p>
 */

public class Treasure extends RareItems {

    private Sprite treasure;

    private RegionOfInterest treasureRegion;

    private final TextGraphics message;
    /**
     * Creates a new treasure at the given position in the specified area.
     * @param currentArea (Area): the area where the treasure is located
     * @param position (DiscreteCoordinates): the initial position of the treasure
     */
    public Treasure(Area currentArea, DiscreteCoordinates position) {
        super(currentArea, position, 100);

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
        if (isVisible()) {
            treasure.draw(canvas);
            message.draw(canvas);
        }
    }
}
