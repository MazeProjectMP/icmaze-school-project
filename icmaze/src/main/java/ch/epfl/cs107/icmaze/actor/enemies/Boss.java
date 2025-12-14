package ch.epfl.cs107.icmaze.actor.enemies;

import ch.epfl.cs107.icmaze.actor.Health;
import ch.epfl.cs107.icmaze.actor.collectable.Heart;
import ch.epfl.cs107.icmaze.actor.collectable.Key;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.engine.actor.OrientedAnimation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Transform;
import ch.epfl.cs107.play.math.Vector;

import java.util.List;


public class Boss extends Enemy {


    /** Duration of the boss animation. */
    private static final int ANIMATION_DURATION = 60;

    /** Anchor used to correctly position the boss animation. */
    private final Vector anchor;
    private final Orientation[] orders;
    private final OrientedAnimation bossAnimation;


    /** Health bar associated with the boss. */
    private Health health;

    private static final int IMMUNITY_DURATION_FRAMES = 20;
    private int immunityFrames;
    private boolean isImmune;

    /**
     * Creates a new boss enemy.
     * @param area (Area): the area where the boss is located
     * @param orientation (Orientation): initial orientation of the boss
     * @param position (DiscreteCoordinates): initial position of the boss
     * @param maxHP (int): maximum health points of the boss
     */
    public Boss(Area area, Orientation orientation, DiscreteCoordinates position, int maxHP) {
        super(area, orientation, position, maxHP);

        health = new Health(this, Transform.I.translated(0, 2f), maxHP, false);

        anchor = new Vector(-0.5f, 0);
        orders = new Orientation[]{ Orientation.DOWN, Orientation.RIGHT, Orientation.UP, Orientation.LEFT };

        immunityFrames = 0;
        isImmune = false;

        bossAnimation = new OrientedAnimation(
                "icmaze/boss", ANIMATION_DURATION / 3,
                this, anchor, orders, 4, 2, 2, 32, 32, true
        );
    }

    /**
     * Updates the boss state.
     * Handles animation updates and immunity timing.
     * @param deltaTime (float): time elapsed since last update
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if(isDead()){
            Key key = new Key(getOwnerArea(), getCurrentMainCellCoordinates(), -1);
            getOwnerArea().registerActor(key);
        }
        // Handles immunity
        if (isImmune) {
            immunityFrames -= deltaTime;
            if (immunityFrames <= 0) {
                isImmune = false;
            }
        }
    }

    /**
     * Makes the boss lose health points.
     * If the boss is not immune, damage is applied and temporary immunity
     * is activated.
     * @param HP (int): amount of health points to remove
     */
    @Override
    public void loseHP(int HP) {
        if (!isDead() && !isImmune && health.isOn()) {
            isImmune = true;
            immunityFrames = IMMUNITY_DURATION_FRAMES;
            health.decrease(HP);
            super.loseHP(HP);
        }
    }

    /**
     * Draws the boss and its health bar on the canvas.
     * @param canvas (Canvas): the canvas to draw on
     */
    @Override
    public void draw(ch.epfl.cs107.play.window.Canvas canvas) {
        super.draw(canvas);

        if (!isVanishing()) {
            if (!isImmune || immunityFrames % 2 == 0) {
                bossAnimation.draw(canvas);
            }
            if (isImmune) {
                health.draw(canvas);
            }
        }
    }

    /**
     * Gets the list of cells in the boss field of view.
     * @return (List<DiscreteCoordinates>): an empty list
     */
    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return List.of();
    }

    /**
     * Indicates whether the boss wants cell interactions.
     * @return (boolean): {@code false}
     */
    @Override
    public boolean wantsCellInteraction() {
        return false;
    }

    /**
     * Indicates whether the boss wants view interactions.
     * @return (boolean): {@code false}
     */
    @Override
    public boolean wantsViewInteraction() {
        return false;
    }

    /**
     * Handles interaction with another interactable entity.
     * @param other (Interactable): the interacting entity
     * @param isCellInteraction (boolean): whether the interaction is a cell interaction
     */
    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
    }
}
