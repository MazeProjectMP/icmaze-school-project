package ch.epfl.cs107.icmaze.actor.collectable.rare;

import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class GoldCoin extends RareItems{
    private final static int ANIMATION_DURATION =24;

    private Animation animatedCoin;

    public GoldCoin(Area area, DiscreteCoordinates position) {
        super(area, position, 10);

        animatedCoin = new Animation("icmaze/coin", 4, 1, 1, this, 16, 16, ANIMATION_DURATION / 4, true);
    }

    /**
     * Draws the heart on the canvas if it is visible.
     * @param canvas (Canvas): the canvas to draw on
     */
    @Override
    public void draw(ch.epfl.cs107.play.window.Canvas canvas) {
        if (isVisible()) {
            animatedCoin.draw(canvas);
        }
    }

    /**
     * Updates the heart animation if it is visible.
     * @param deltaTime (float): time elapsed since last update
     */
    @Override
    public void update(float deltaTime) {
        if (isVisible()) {
            animatedCoin.update(deltaTime);
        }
    }



}
