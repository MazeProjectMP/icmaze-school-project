package ch.epfl.cs107.icmaze.actor.enemies;

import ch.epfl.cs107.icmaze.actor.ICMazePlayer;
import ch.epfl.cs107.icmaze.actor.rocks.Rock;
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

/**
 * Abstract base class for projectile entities in the ICMaze game.
 * <p>
 * A {@code Projectile} is a movable entity that travels in a fixed orientation
 * for a limited range. Projectiles interact with other entities such as players
 * and rocks, applying effects (e.g. damage) and stopping when a collision occurs
 * or when their maximum travel distance is reached.
 * </p>
 */

public abstract class Projectile extends MovableAreaEntity implements Interactor {
    private static final int SPEED = 1;
    private static final int MAX_RANGE = 7;
    private static final int MOVE_DURATION = 10;
    private boolean stopped;
    private int remainingDistance;

    /**
     * Creates a new projectile.
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

        @Override
        public void interactWith(Rock rock, boolean isCellInteraction) {
            if (rock.isBroken()) return;

            DiscreteCoordinates inFront = getCurrentMainCellCoordinates().jump(getOrientation().toVector());

            if (rock.getCurrentMainCellCoordinates().equals(inFront)) {
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
    /**
     * Accepts an interaction from an area interaction visitor.
     * Projectiles do not handle area interaction visitors directly.
     * @param v (AreaInteractionVisitor): the interaction visitor
     * @param isCellInteraction (boolean): whether the interaction is a cell interaction
     */
    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
    }

    /**
     * Stops the projectile and removes it from the area.
     * This method is called when the projectile reaches its maximum range
     * or collides with another entity.
     */
    private void stop() {
        stopped = true;
        getOwnerArea().leaveAreaCells(this, getCurrentCells());
        getOwnerArea().unregisterActor(this);
    }

    /**
     * Updates the projectile state.
     * The projectile advances step by step in its current orientation until
     * it reaches its maximum range or becomes blocked. Once stopped, it is
     * removed from the area.
     * @param deltaTime (float): time elapsed since last update
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (remainingDistance <= 0 && !isDisplacementOccurs()) {
            stop();
            return;
        }

        /// Tries to make a step as soon as he is not already moving
        if (!isDisplacementOccurs() && remainingDistance > 0) {
            int duration = Math.max(1, MOVE_DURATION / SPEED);
            move(duration);

            /// If the move did not start, the entity is blocked, blocks movement before max distance
            if (!isDisplacementOccurs()) {
                stop();
            } else {
                remainingDistance -= 1; /// One step is equivalent to one distance unity
            }
        }
    }

    /**
     * Gets the list of cells currently occupied by the projectile.
     * @return (List<DiscreteCoordinates>): a singleton list containing the current cell
     */
    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return  Collections.singletonList(getCurrentMainCellCoordinates());
    }

    /**
     * Gets the cell immediately in front of the projectile.
     * @return (List<DiscreteCoordinates>): a singleton list containing the cell in front
     */
    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates().jump(getOrientation().toVector())
        );
    }

    /**
     * Indicates whether the projectile wants cell interactions.
     * @return (boolean): {@code true} if the projectile is still moving
     */
    @Override
    public boolean wantsCellInteraction() {
        return !stopped && remainingDistance > 0;
    }

    /**
     * Indicates whether the projectile wants view interactions.
     * @return (boolean): {@code false}
     */
    @Override
    public boolean wantsViewInteraction() {
        return false;
    }

    /**
     * Indicates whether the projectile blocks the cell it occupies.
     * @return (boolean): {@code false}
     */
    @Override
    public boolean takeCellSpace() {
        return false;
    }

    /**
     * Indicates whether the projectile can be interacted with via cell interaction.
     * @return (boolean): {@code false}
     */
    @Override
    public boolean isCellInteractable() {
        return false;
    }

    /**
     * Indicates whether the projectile can be interacted with via view interaction.
     * @return (boolean): {@code false}
     */
    @Override
    public boolean isViewInteractable() {
        return false;
    }
}
