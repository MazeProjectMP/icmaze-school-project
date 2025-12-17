package ch.epfl.cs107.icmaze.actor.rocks;

import ch.epfl.cs107.icmaze.actor.Health;
import ch.epfl.cs107.icmaze.actor.collectable.Heart;
import ch.epfl.cs107.icmaze.area.maps.MazeLevels;
import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Transform;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;

/**
 * Abstract base class for destructible rocks in the ICMaze game.
 * <p>
 * A {@code Rock} is a static obstacle that blocks movement while intact. It has health points,
 * can take damage through interactions, becomes temporarily immune after being hit (blinking effect),
 * and plays a vanishing animation when destroyed. Once the vanishing animation completes, the rock
 * spawns loot by calling {@link #lootSpawn()} and is removed from the area by the concrete subclass.
 * </p>
 */

public abstract class Rock extends AreaEntity {
    private final int maxHP;
    private int currentHP;
    private boolean isBroken;

    private Animation vanishAnimation = new Animation(
            "icmaze/vanish", 7, 2, 2, this,
            32, 32, new Vector(-0.5f, 0.0f),
            24 / 7, false
    );

    private boolean isVanishing ;
    private Health health;

    private static final int IMMUNITY_DURATION = 20;
    private int immunityFrames ;
    private boolean isImmune ;
    private Sprite sprite;

    /**
     * Creates a new destructible rock at the given position in the specified area.
     * @param area (Area): the area where the rock is located
     * @param position (DiscreteCoordinates): the initial position of the rock
     * @param maxHP (int): maximum health points of the rock
     * @param spiteName (String): sprite name used to render the rock
     */
    public Rock(Area area, DiscreteCoordinates position, int maxHP, String spiteName) {
        super(area, Orientation.LEFT, position);

        this.maxHP = maxHP;
        this.currentHP = maxHP;
        this.health = new Health(this, Transform.I.translated(0, 1.3f), maxHP, true);
        this.isVanishing = false;
        this.immunityFrames = 0;
        this.isImmune= false;
        this.isBroken = false;
        this.sprite = new Sprite(spiteName, 1, 1, this);
    }

    /**
     * Draws the rock on the canvas.
     * If the rock is vanishing, draws the vanishing animation. Otherwise draws the rock sprite,
     * with a blinking effect during immunity, and draws the health bar while immune.
     * @param canva (Canvas): the canvas to draw on
     */
    @Override
    public void draw(Canvas canva) {
        if (isVanishing) {
            vanishAnimation.draw(canva);
        } else {
            if (!isImmune || immunityFrames % 2 == 0) {
                sprite.draw(canva);
            }
            if (isImmune) {
                health.draw(canva);
            }
        }
    }

    /**
     * Updates the rock state.
     * Handles immunity timing and vanishing animation. When the vanishing animation completes,
     * {@link #lootSpawn()} is called to spawn loot and perform cleanup (graph update, unregister).
     * @param deltaTime (float): time elapsed since last update
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if (isImmune) {
            immunityFrames -= deltaTime;
            if (immunityFrames <= 0) {
                isImmune = false;
            }
        }

        if (isVanishing) {
            vanishAnimation.update(deltaTime);
            if (vanishAnimation.isCompleted()) {
                lootSpawn();
            }
        }
    }
    /**
     * Spawns loot when the rock has been destroyed.
     * This method is called once the vanishing animation is completed.
     * Concrete subclasses must implement the loot spawning behavior, update
     * the walkable maze graph if needed, and unregister the rock from the area.
     */
    abstract void lootSpawn();

    /**
     * Breaks the rock immediately and starts the vanishing process.
     * This method forces the rock health to zero, marks it as broken, and triggers
     * the vanishing animation. Loot spawning will occur when the animation completes.
     */
    public void breakWall(){
        currentHP = 0;
        isBroken = true;
        isVanishing = true;
    }

    /**
     * Gets the list of cells currently occupied by the rock.
     * @return (List<DiscreteCoordinates>): a singleton list containing the main cell coordinates
     */
    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    /**
     * Indicates whether the rock blocks the cell it occupies.
     * @return (boolean): {@code true} if the rock is not broken and not vanishing
     */
    @Override
    public boolean takeCellSpace() {
        return !isBroken && !isVanishing;
    }

    /**
     * Indicates whether the rock can be interacted with via cell interaction.
     * @return (boolean): {@code false}
     */
    @Override
    public boolean isCellInteractable() {
        return false;
    }

    /**
     * Indicates whether the rock can be interacted with from a distance.
     * @return (boolean): {@code true}
     */
    @Override
    public boolean isViewInteractable() {
        return true;
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

    /**
     * Makes the rock lose health points.
     * If the rock is not immune, damage is applied and temporary immunity is activated.
     * When health reaches zero, the rock becomes broken and starts vanishing.
     * @param HP (int): amount of health points to remove
     */
    public void loseHP(int HP) {
        if (!isImmune && health.isOn()) {
            health.decrease(HP);
            this.currentHP -= HP;

            isImmune = true;
            immunityFrames = IMMUNITY_DURATION;
        }
        if (currentHP <= 0) {
            isBroken = true;
            isVanishing = true;
        }
    }

    /**
     * Indicates whether the rock is broken.
     * @return (boolean): {@code true} if the rock is broken
     */
    public boolean isBroken() {
        return isBroken;
    }
}
