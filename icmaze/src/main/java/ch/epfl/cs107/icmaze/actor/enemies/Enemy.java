package ch.epfl.cs107.icmaze.actor.enemies;

import ch.epfl.cs107.icmaze.RandomGenerator;
import ch.epfl.cs107.icmaze.actor.ICMazePlayer;
import ch.epfl.cs107.icmaze.actor.collectable.Heart;
import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.actor.MovableAreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;

/**
 * Abstract base class for all enemy entities in the ICMaze game.
 * <p>
 * An {@code Enemy} is a movable and interactable entity with health points that can
 * take damage and eventually disappear from the game area. Enemies block movement
 * while alive, support view-based interactions, and play a vanishing animation when
 * defeated. Concrete enemy types (such as bosses or monsters) extend this class to
 * implement specific behaviors.
 * </p>
 */

public abstract class Enemy extends MovableAreaEntity implements Interactable, Interactor {

    private final int maxHP;
    private int currentHP;
    private boolean isDead;
    private Animation vanishAnimation ;
    private boolean isVanishing ;

    /**
     * Creates a new enemy.
     * @param area (Area): the area where the enemy is located
     * @param orientation (Orientation): initial orientation of the enemy
     * @param position (DiscreteCoordinates): initial position of the enemy
     * @param maxHP (int): maximum health points of the enemy
     */
    public Enemy(Area area, Orientation orientation, DiscreteCoordinates position, int maxHP) {
        super(area, orientation, position);
        this.maxHP = maxHP;
        this.currentHP = maxHP;
        this.isDead = false;

        isVanishing = false;
        vanishAnimation = new Animation(
                "icmaze/vanish", 7, 2, 2, this,
                32, 32, new Vector(-0.5f, 0.0f),
                24 / 7, false
        );
    }

    /**
     * Updates the enemy state.
     * Handles the vanishing animation when the enemy is dying.
     * @param deltaTime (float): time elapsed since last update
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if (isVanishing) {
            vanishAnimation.update(deltaTime);
            if (vanishAnimation.isCompleted()) {
                getOwnerArea().unregisterActor(this);
            }
        }
    }

    /**
     * Draws the enemy on the canvas.
     * Only the vanishing animation is drawn when the enemy is disappearing.
     * @param canvas (Canvas): the canvas to draw on
     */
    @Override
    public void draw(Canvas canvas) {
        if (isVanishing()) {
            vanishAnimation.draw(canvas);
        }
    }

    /**
     * Makes the enemy lose health points.
     * When health reaches zero, the enemy starts vanishing.
     * @param HP (int): amount of health points to remove
     */
    public void loseHP(int HP) {
        this.currentHP -= HP;
        if (currentHP <= 0) {
            disappear();
        }
    }

    /**
     * Indicates whether the enemy is dead
     * @return (boolean): {@code true} if the enemy is dead
     */
    public boolean isDead() {
        return this.isDead;
    }

    /**
     * Makes the enemy disappear.
     */
    protected void disappear() {
        isDead = true;
        isVanishing = true;
    }

    /**
     * Indicates whether the enemy is currently vanishing.
     * @return (boolean): {@code true} if the enemy is vanishing
     */
    protected boolean isVanishing() {
        return isVanishing;
    }

    /**
     * Indicates whether the enemy blocks the cell it occupies.
     * @return (boolean): {@code true} if the enemy is alive
     */
    @Override
    public boolean takeCellSpace() {
        return !isDead();
    }

    /**
     * Indicates whether the enemy can be interacted with via cell interaction.
     * @return (boolean): {@code false}
     */
    @Override
    public boolean isCellInteractable() {
        return false;
    }

    /**
     * Indicates whether the enemy can be interacted with from a distance.
     * @return (boolean): {@code true}
     */
    @Override
    public boolean isViewInteractable() {
        return true;
    }

    /**
     * Gets the list of cells currently occupied by the enemy.
     * @return (List<DiscreteCoordinates>): a singleton list containing the main cell coordinates
     */
    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
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
}
