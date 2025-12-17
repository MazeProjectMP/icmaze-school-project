package ch.epfl.cs107.icmaze.actor.enemies;

import ch.epfl.cs107.icmaze.RandomGenerator;
import ch.epfl.cs107.icmaze.actor.Health;
import ch.epfl.cs107.icmaze.actor.collectable.Key;
import ch.epfl.cs107.icmaze.actor.util.Cooldown;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.OrientedAnimation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Transform;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.signal.logic.Logic;

import java.util.HashMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the boss enemy of the ICMaze game.
 * <p>
 * The {@code Boss} is a powerful enemy with a large amount of health, special attack
 * patterns, teleportation abilities, and temporary immunity after taking damage.
 * Once activated, it periodically casts fire-based spells and changes position
 * within a predefined arena. When defeated, the boss drops a key that can be used
 * to progress further in the game.
 * </p>
 */
public class Boss extends Enemy {

    ///Duration of the boss animation.
    private static final int ANIMATION_DURATION = 60;

    /// Anchor used to correctly position the boss animation.
    private final Vector anchor;
    private final Orientation[] orders;
    private final OrientedAnimation bossAnimation;


    ///Health bar associated with the boss.
    private Health health;

    private static final int IMMUNITY_DURATION_FRAMES = 20;
    private int immunityFrames;
    private boolean isImmune;


    /// Activation du Boss
    private boolean activated;
    private static final int SIZE = 8;
    private static final int MID = SIZE / 2 + 1;
    private static final DiscreteCoordinates CENTER = new DiscreteCoordinates(MID, MID);
    /// Puisqu'on a simplifier les lieux de teleportation, on peut utiliser une hashmap
    HashMap<DiscreteCoordinates, Orientation> teleportationList = new HashMap<>();

    private Cooldown attackCooldown;
    private boolean keyDropped = false;
    private Logic droppedKeySignal = Logic.FALSE;

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

        bossAnimation = new OrientedAnimation("icmaze/boss", ANIMATION_DURATION / 3, this, anchor, orders, 3, 2, 2, 32, 32, true);

        activated = false;

        teleportationList.put(new DiscreteCoordinates(MID, SIZE), Orientation.DOWN);
        teleportationList.put(new DiscreteCoordinates(MID, 1), Orientation.UP);
        teleportationList.put(new DiscreteCoordinates(1, MID), Orientation.RIGHT);
        teleportationList.put(new DiscreteCoordinates(SIZE, MID), Orientation.LEFT);

        attackCooldown = new Cooldown(3);
    }

    /**
     * Updates the boss state.
     * Handles animation updates and immunity timing.
     * @param deltaTime (float): time elapsed since last update
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        bossAnimation.update(deltaTime);
        if(activated && attackCooldown.ready(deltaTime)){
            fireBallSpell();
            attackCooldown.reset();
        }

        if(isDead() && !keyDropped){
            keyDropped = true;
            Key key = new Key(getOwnerArea(), getCurrentMainCellCoordinates(), -1);
            getOwnerArea().registerActor(key);
            droppedKeySignal = key;
        }

        if (isImmune) {
            immunityFrames -= deltaTime;
            if (immunityFrames <= 0) {
                isImmune = false;
            }
        }
        if(bossAnimation.isCompleted()){
            bossAnimation.reset();
        }
    }

    /**
     * Provides a logic signal indicating whether the boss has dropped its key.
     * @return a {@link Logic} signal that is {@code TRUE} once the key has been dropped,
     * {@code FALSE} otherwise
     */
    public Logic droppedKeySignal() {
        return droppedKeySignal;
    }

    /**
     * Casts a fireball spell in front of the boss.
     * This attack spawns multiple {@link FireProjectile} instances in a straight line
     * oriented according to the boss orientation. One random position in the line is
     * left empty, creating a gap that the player can use to avoid the attack.
     */

    private void fireBallSpell() {
        DiscreteCoordinates bossPosition = getCurrentMainCellCoordinates();
        Orientation bossOrientation = getOrientation();

        List<DiscreteCoordinates> spawnPositions = fireballsPositions(bossOrientation, bossPosition);

        for (DiscreteCoordinates spawnPosition : spawnPositions) {
            FireProjectile fireball = new FireProjectile(getOwnerArea(), bossOrientation, spawnPosition);
            getOwnerArea().registerActor(fireball);
            getOwnerArea().enterAreaCells(fireball, fireball.getCurrentCells());
        }
    }


    /**
     * Computes the positions where the boss fireball spell will be spawned.
     * <p>
     * The spell generates {@code SIZE - 1} fireball positions aligned either
     * horizontally or vertically depending on the boss orientation.
     * One randomly chosen position is left empty, creating a gap that allows
     * the player to avoid the attack.
     * </p>
     *
     * @param bossOrientation (Orientation) the current orientation of the boss
     * @param bossPosition (DiscreteCoordinates) the current position of the boss
     * @return a list of discrete coordinates where fireballs will be spawned
     */
    private List<DiscreteCoordinates> fireballsPositions(Orientation bossOrientation, DiscreteCoordinates bossPosition) {
        List<DiscreteCoordinates> positions = new ArrayList<>(SIZE - 1);

        int hole = RandomGenerator.nextInt(SIZE) + 1 ;

        switch (bossOrientation) {
            case LEFT, RIGHT: {
                int columnX = bossPosition.x;
                for (int y = 1; y <= SIZE; y++) {
                    if (y == hole) continue; ///Random hole
                    positions.add(new DiscreteCoordinates(columnX, y));
                }
                break;
            }

            case UP, DOWN: {
                int rowY = bossPosition.y;
                for (int x = 1; x <= SIZE; x++) {
                    if (x == hole) continue; ///Random hole
                    positions.add(new DiscreteCoordinates(x, rowY));
                }
                break;
            }
        }

        return positions;
    }
    /**
     * Makes the boss lose health points.
     * If the boss is not immune, damage is applied and temporary immunity
     * is activated.
     * @param HP (int)  : amount of health points to remove
     */

    @Override
    public void loseHP(int HP) {
        if (isDead()) return;

        if (!isImmune && health.isOn()) {
            isImmune = true;
            immunityFrames = IMMUNITY_DURATION_FRAMES;

            if (!activated) {
                activated = true;
                teleportRandomly();
            }else{
                teleportRandomly();
            }

            health.decrease(HP);
            super.loseHP(HP);
        }
    }

    /**
     * Chooses a random teleport destination among the predefined teleportation cells.
     * The current position is excluded from the candidates when the boss is not
     * located at the center, to avoid teleporting to the same location.
     * @return a randomly selected teleport destination coordinate
     */
    private DiscreteCoordinates randomTeleportCoordinates() {
        HashMap<DiscreteCoordinates, Orientation> possiblePositions = new HashMap<>(teleportationList);
        if(!getCurrentMainCellCoordinates().equals(CENTER)) {
            possiblePositions.remove(getCurrentMainCellCoordinates()); // Si il n'est pas au centre, pour s'assurer qu'il ne prend pas la meme position
        }
        List<DiscreteCoordinates> possibleCoordinates = new ArrayList<>(possiblePositions.keySet()); // Copie permettant de faire une position aleatoire
        DiscreteCoordinates randomCoordinate = possibleCoordinates.get(RandomGenerator.nextInt(possibleCoordinates.size()));

        return randomCoordinate;
    }
    /**
     * Teleports the boss to a random predefined destination and updates its orientation.
     * This method moves the boss by unregistering it from the area, updating its
     * position and orientation, resetting its motion, and registering it again while
     * updating the occupied cells accordingly.
     */

    private void teleportRandomly() {
        DiscreteCoordinates randomCoordinate = randomTeleportCoordinates();
        Orientation orientation = teleportationList.get(randomCoordinate);
        Area currentArea = getOwnerArea();

        currentArea.unregisterActor(this);
        currentArea.leaveAreaCells(this, getCurrentCells());
        orientate(orientation);
        setCurrentPosition(randomCoordinate.toVector());
        resetMotion();
        currentArea.enterAreaCells(this, getCurrentCells());
        currentArea.registerActor(this);
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
