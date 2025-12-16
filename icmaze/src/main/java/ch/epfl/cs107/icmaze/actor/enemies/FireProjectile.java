package ch.epfl.cs107.icmaze.actor.enemies;

import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

public class FireProjectile extends Projectile {
    private static final int ANIMATION_DURATION = 12;
    private final Animation fireBallAnimation;

    public FireProjectile(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position);
        fireBallAnimation = new Animation("icmaze/magicFireProjectile", 4, 1, 1, this, 32, 32, ANIMATION_DURATION/4, true);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        fireBallAnimation.update(deltaTime);
        if(fireBallAnimation.isCompleted()){
            fireBallAnimation.reset();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        fireBallAnimation.draw(canvas);
    }
}