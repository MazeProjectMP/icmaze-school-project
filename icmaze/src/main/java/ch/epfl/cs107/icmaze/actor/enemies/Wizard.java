package ch.epfl.cs107.icmaze.actor.enemies;

import ch.epfl.cs107.icmaze.RandomGenerator;
import ch.epfl.cs107.icmaze.actor.Health;
import ch.epfl.cs107.icmaze.actor.ICMazePlayer;
import ch.epfl.cs107.icmaze.actor.util.Cooldown;
import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.AreaGraph;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.OrientedAnimation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Transform;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

/**
 * Represents a wizard enemy that alternates between movement and ranged attacks in the ICMaze game.
 * <p>
 * The {@code Wizard} is a path-finder enemy with a perception radius that can detect the player
 * through view interactions. When the player is aligned, within shooting range, and visible (no
 * obstacles in the {@link AreaGraph}), the wizard switches to an attacking state and shoots
 * {@link FireProjectile} projectiles. Otherwise, it moves randomly. The wizard has temporary
 * immunity after taking damage and displays a health bar.
 * </p>
 */

public class Wizard extends PathFinderEnemy {

    private static final int ANIMATION_DURATION = 30;

    private static final String MOVING_SPRITE = "icmaze/darkLord";
    private static final String SPELL_SPRITE  = "icmaze/darkLord.spell";

    private static final int MOVE_DURATION = 10;

    private static final int PERCEPTION_RADIUS = 6;
    private static final int SHOOT_RANGE = 3;

    private static final int MAX_HP = 3;

    private static final int IMMUNITY_DURATION = 15;
    private boolean isImmune;
    private final Health health;
    private int immunityFrames;

    /**
     * Enumeration of the possible behavior states of the wizard.
     *   {@link #MOVING}: the wizard moves around the area
     *   {@link #ATTACKING}: the wizard plays its spell animation and shoots projectiles
     */
    public enum State { MOVING, ATTACKING }
    private State currentState;

    private final AreaGraph areaGraph;

    private final Cooldown movingCooldown;
    private final Cooldown attackCooldown;

    private final Orientation[] animationOrders = new Orientation[] {
            Orientation.UP, Orientation.LEFT, Orientation.DOWN, Orientation.RIGHT
    };

    private final OrientedAnimation movingAnimation;
    private final OrientedAnimation spellAnimation;

    private ICMazePlayer targetPlayer;

    private class WizardInteractionHandler implements ICMazeInteractionVisitor {
        @Override
        public void interactWith(ICMazePlayer player, boolean isCellInteraction) {
            if (!isCellInteraction) {
                targetPlayer = player;
            }
        }
    }

    private final WizardInteractionHandler handler = new WizardInteractionHandler();

    /**
     * Creates a new wizard enemy.
     * @param area (Area): the area where the wizard is located
     * @param position (DiscreteCoordinates): initial position of the wizard
     * @param areaGraph (AreaGraph): navigation graph used to check line of sight
     */
    public Wizard(Area area, DiscreteCoordinates position, AreaGraph areaGraph) {
        super(area, Orientation.LEFT, position, MAX_HP, PERCEPTION_RADIUS);

        this.areaGraph = areaGraph;
        this.currentState = State.MOVING;

        this.movingCooldown = new Cooldown(0.75f);
        this.attackCooldown = new Cooldown(2.0f);

        this.movingAnimation = new OrientedAnimation(
                MOVING_SPRITE,
                ANIMATION_DURATION / 3,
                this,
                new Vector(-0.5f, 0.25f),
                animationOrders,
                3, 2, 2, 32, 32, true
        );

        this.spellAnimation = new OrientedAnimation(
                SPELL_SPRITE,
                ANIMATION_DURATION / 3,
                this,
                new Vector(-0.5f, 0.25f),
                animationOrders,
                3, 2, 2, 32, 32, true
        );

        this.immunityFrames = 0;
        this.isImmune = false;

        this.health = new Health(this, Transform.I.translated(0, 2.3f), MAX_HP, false);
    }

    /**
     * Forces the wizard to disappear.
     * This method triggers the enemy vanishing process by delegating to {@link Enemy#disappear()}.
     */
    public void vanish() {
        disappear();
    }

    /**
     * Forwards an interaction to the wizard interaction handler.
     * The wizard uses view interactions to detect and store a player as a potential target.
     * @param other (Interactable): the interactable entity
     * @param isCellInteraction (boolean): whether the interaction is a cell interaction
     */
    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(handler, isCellInteraction);
    }

    /**
     * Updates the wizard state.
     * This method updates immunity timing, selects the behavior state (moving or attacking)
     * depending on player alignment and line of sight, and updates the corresponding animation.
     * @param deltaTime (float): time elapsed since last update
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        /// evolution of immunity
        if (isImmune) {
            immunityFrames -= deltaTime;
            if (immunityFrames <= 0) {
                isImmune = false;
            }
        }

        // Si le joueur est aligné -> ATTACKING, sinon MOVING
        Orientation shootingOrientation = getShootingOrientationIfPossible();
        if (shootingOrientation != null) {
            currentState = State.ATTACKING;
            orientate(shootingOrientation);
        } else {
            currentState = State.MOVING;
        }

        if (currentState == State.ATTACKING) {
            spellAnimation.update(deltaTime);
            handleAttackingState(deltaTime);
        } else {
            movingAnimation.update(deltaTime);
            handleMovingState(deltaTime);
        }
    }

    /**
     * Makes the wizard lose health points.
     * Damage is applied only if the wizard is alive and not currently immune.
     * When hit, the wizard becomes temporarily immune.
     * @param hp (int): amount of health points to remove
     */
    @Override
    public void loseHP(int hp) {
        if (!isDead() && !isImmune && health.isOn()) {
            isImmune = true;
            immunityFrames = IMMUNITY_DURATION;
            health.decrease(hp);
            super.loseHP(hp);
        }
    }

    /**
     * Handles the moving state behavior.
     * The wizard periodically chooses a random orientation and attempts to move.
     * @param deltaTime (float): time elapsed since last update
     */
    private void handleMovingState(float deltaTime) {
        if (!movingCooldown.ready(deltaTime)) return;

        Orientation randomOrientation = Orientation.values()[RandomGenerator.nextInt(4)];
        orientate(randomOrientation);
        move(MOVE_DURATION);

        movingCooldown.reset();
    }

    /**
     * Handles the attacking state behavior.
     * When the attack cooldown is ready, the wizard casts a spell and spawns a fire projectile.
     * @param deltaTime (float): time elapsed since last update
     */
    private void handleAttackingState(float deltaTime) {
        if (!attackCooldown.ready(deltaTime)) return;

        fireBallSpell();
        attackCooldown.reset();
    }

    /**
     * Spawns a fire projectile in front of the wizard attack.
     * The projectile is created in the wizard's current orientation and registered in the area.
     */
    private void fireBallSpell() {
        FireProjectile fireball = new FireProjectile(
                getOwnerArea(),
                getOrientation(),
                getCurrentMainCellCoordinates()
        );
        getOwnerArea().registerActor(fireball);
        getOwnerArea().enterAreaCells(fireball, fireball.getCurrentCells());
    }

    /**
     * Computes the shooting orientation toward the current target player if an attack is possible.
     * An attack is possible only if:
     *   a target player exists
     *   the player is aligned (same x or same y)
     *   the player is within {@code SHOOT_RANGE}
     *   the line of sight is clear (all intermediate cells exist in the {@link AreaGraph})
     * Otherwise, this method returns {@code null}.
     * @return (Orientation): the shooting orientation, or {@code null} if shooting is not possible
     */
    private Orientation getShootingOrientationIfPossible() {
        if (targetPlayer == null) return null;

        DiscreteCoordinates wizardPosition = getCurrentMainCellCoordinates();
        DiscreteCoordinates playerPosition = targetPlayer.getCurrentMainCellCoordinates();

        int deltaX = playerPosition.x - wizardPosition.x;
        int deltaY = playerPosition.y - wizardPosition.y;

        /// range
        if (Math.max(Math.abs(deltaX), Math.abs(deltaY)) > SHOOT_RANGE) return null;

        /// alignement
        Orientation direction;
        if (wizardPosition.x == playerPosition.x) {
            direction = (deltaY > 0) ? Orientation.UP : Orientation.DOWN;
        } else if (wizardPosition.y == playerPosition.y) {
            direction = (deltaX > 0) ? Orientation.RIGHT : Orientation.LEFT;
        } else {
            return null;
        }

        /// ligne de vue (pas d'obstacle entre les deux)
        DiscreteCoordinates current = wizardPosition;
        while (!current.equals(playerPosition)) {
            current = current.jump(direction.toVector());
            if (current.equals(playerPosition)) break;
            if (!areaGraph.nodeExists(current)) return null;
        }

        return direction;
    }

    /**
     * Computes the next orientation for pathfinding.
     * This implementation is not used in the current simplified version and always returns {@code null}.
     * @return (Orientation): {@code null}
     */
    @Override
    public Orientation getNextOrientation() {
        return null;
    }

    /**
     * Draws the wizard and its health bar on the canvas.
     * The displayed animation depends on the current state (moving or attacking).
     * @param canvas (Canvas): the canvas to draw on
     */
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if ((!isVanishing() && ( !isImmune || immunityFrames % 2 == 0))) {
            if (currentState == State.MOVING) {
                movingAnimation.draw(canvas);
            } else {
                spellAnimation.draw(canvas);
            }
            health.draw(canvas);
        }
    }
}
