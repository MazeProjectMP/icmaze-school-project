package ch.epfl.cs107.icmaze.actor.collectable.rare;

import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

public class Crystal extends RareItems {
    private Sprite crystal;

    public Crystal(Area area, DiscreteCoordinates position) {
        super(area, position, 3);

        crystal = new Sprite("icmaze/red_crystal", 1, 1, this);
    }

    /**
     * Draws the crystal on the canvas if it is visible.
     * @param canvas (Canvas): the canvas to draw on
     */
    @Override
    public void draw(ch.epfl.cs107.play.window.Canvas canvas) {
        if (isVisible()) {
            crystal.draw(canvas);
        }
    }


}
