package ch.epfl.cs107.icmaze.actor.collectable;

import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.CollectableAreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.math.*;

import java.util.Collections;
import java.util.List;

public class Heart extends CollectableAreaEntity {
    private final static int ANIMATION_DURATION = 24; // Duree de l'animation du coeur

    private boolean isVisible = true;
    // Animation du coeur
    private Animation animatedHeart = new Animation("icmaze/heart", 4, 1, 1, this, 16, 16, ANIMATION_DURATION/4, true);
    public Heart(Area currentArea, DiscreteCoordinates position){
        super(currentArea, Orientation.DOWN, position);
    }
    @Override
    public void draw(ch.epfl.cs107.play.window.Canvas canvas) {
        if(isVisible) {
            animatedHeart.draw(canvas);
        };
    }

    @Override
    public void update(float deltaTime){
        if (isVisible) {
            animatedHeart.update(deltaTime);
        }
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    @Override
    public boolean takeCellSpace() {
        return false;
    }

    @Override
    public boolean isCellInteractable() {
        return true;
    }

    @Override
    public boolean isViewInteractable() {
        return false;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICMazeInteractionVisitor) v).interactWith(this, isCellInteraction);
    }

    public void hide() {
        if (isVisible) {
            isVisible = false;
            getOwnerArea().unregisterActor(this);
        }
    }
}
