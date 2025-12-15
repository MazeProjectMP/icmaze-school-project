package ch.epfl.cs107.icmaze.actor.enemies;

import ch.epfl.cs107.icmaze.actor.ICMazePlayer;
import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.actor.MovableAreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

import java.util.Collections;
import java.util.List;

public abstract class Projectile extends MovableAreaEntity implements Interactor {
    private static final int SPEED = 1;
    private static final int MAX_RANGE = 7;
    private static final int MOVE_DURATION = 10;
    private boolean stopped;
    private int remainingDistance;

    /**
     * Default MovableAreaEntity constructor
     * @param area        (Area): Owner area. Not null
     * @param orientation (Orientation): Initial orientation of the entity. Not null
     * @param position    (Coordinate): Initial position of the entity. Not null
     */
    public Projectile(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position);

        stopped = false;
        remainingDistance = MAX_RANGE;
    }

    private class ICMazeProjectileInteractionHandler implements ICMazeInteractionVisitor {
        /**
         * Handles interaction with the player.
         * @param player (ICMazePlayer): the interacting player
         * @param isCellInteraction (boolean): whether the interaction is a cell interaction
         */
        @Override
        public void interactWith(ICMazePlayer player, boolean isCellInteraction) {
            if (isCellInteraction) {
                player.loseHP(1);
                stop();
            }
        }
    }

    private final Projectile.ICMazeProjectileInteractionHandler handler = new Projectile.ICMazeProjectileInteractionHandler();
    /**
     * Interacts with the given interactable.
     * @param other (Interactable): the interactable entity
     * @param isCellInteraction (boolean): whether the interaction is a cell interaction
     */

    public void interactWith(Interactable other, boolean isCellInteraction){
        other.acceptInteraction(handler, isCellInteraction);
    }
    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
    }

    private void stop() {
        stopped = true;
        getOwnerArea().leaveAreaCells(this, getCurrentCells());
        getOwnerArea().unregisterActor(this);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (remainingDistance <= 0 && !isDisplacementOccurs()) {
            stop();
            return;
        }

        // tente d’avancer d’un pas dès qu’il n’est pas déjà en déplacement
        if (!isDisplacementOccurs() && remainingDistance > 0) {
            int duration = Math.max(1, MOVE_DURATION / SPEED);
            move(duration);

            // si le move n’a pas démarré => bloqué => stop avant distance max
            if (!isDisplacementOccurs()) {
                stop();
            } else {
                remainingDistance -= 1; // 1 pas = 1 unité de distance
            }
        }
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return  Collections.singletonList(getCurrentMainCellCoordinates());
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return List.of();
    }

    @Override
    public boolean wantsCellInteraction() {
        return !stopped && remainingDistance > 0;
    }

    @Override
    public boolean wantsViewInteraction() {
        return false;
    }

    @Override
    public boolean takeCellSpace() {
        return false;
    }

    @Override
    public boolean isCellInteractable() {
        return false;
    }

    @Override
    public boolean isViewInteractable() {
        return false;
    }
}
