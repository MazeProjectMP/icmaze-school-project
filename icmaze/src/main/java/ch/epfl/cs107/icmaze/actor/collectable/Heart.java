package ch.epfl.cs107.icmaze.actor.collectable;

import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.math.*;

public class Heart extends CollectableAreaEntity implements Positionable {
    private final static int ANIMATION_DURATION = 24; // Duree de l'animation du coeur
    private Animation animatedHeart = new Animation("icmaze/heart", 4, 1, 1, this, 16, 16, ANIMATION_DURATION/4, true);
    public Heart(Area currentArea, DiscreteCoordinates position){
        super(currentArea, position);
    }
    @Override
    public void draw(ch.epfl.cs107.play.window.Canvas canvas) {
        animatedHeart.draw(canvas);
    }

    public void enterArea(Area area, DiscreteCoordinates position) {
        area.registerActor(this);
        area.setViewCandidate(this);
        setCurrentPosition(position.toVector());
    }

    @Override
    public void update(float deltaTime){
        animatedHeart.update(deltaTime);
    }
    @Override
    public Transform getTransform() {
        return null;
    }

    @Override
    public Vector getVelocity() {
        return null;
    }
}
