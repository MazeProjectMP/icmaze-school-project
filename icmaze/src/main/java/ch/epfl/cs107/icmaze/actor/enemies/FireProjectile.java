package ch.epfl.cs107.icmaze.actor.enemies;

import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;
/**
 * Represents a fire-based projectile used by enemies in the ICMaze game.
 * <p>
 * A {@code FireProjectile} is a moving projectile that travels in a fixed
 * orientation and is rendered using a looping fire animation. It extends
 * {@link Projectile} and inherits its movement and collision behavior, while
 * providing a specific visual representation.
 * </p>
 */

public class FireProjectile extends Projectile {
    private static final int ANIMATION_DURATION = 12;
    private final Animation fireBallAnimation;

    public FireProjectile(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position);
        fireBallAnimation = new Animation("icmaze/magicFireProjectile", 4, 1, 1, this, 32, 32, ANIMATION_DURATION/4, true);
    }
    /**
     * Updates the projectile state.
     * This method updates both the projectile movement (handled by the superclass)
     * and the fireball animation. The animation is looped continuously.
     * @param deltaTime (float) time elapsed since the last update
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        fireBallAnimation.update(deltaTime);
        if(fireBallAnimation.isCompleted()){
            fireBallAnimation.reset();
        }
    }
    /**
     * Draws the fire projectile on the canvas.
     * @param canvas (Canvas) the canvas to draw on
     */

    @Override
    public void draw(Canvas canvas) {
        fireBallAnimation.draw(canvas);
    }
}