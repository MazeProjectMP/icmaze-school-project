package ch.epfl.cs107.icmaze.actor.enemies;


import ch.epfl.cs107.icmaze.Difficulty;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class LogMonster extends PathFinderEnemy {


    private static final int ANIMATION_DURATION = 30;
    private static final String awake = "icmaze/logMonster";
    private static final String moving = "icmaze/logMonster_random";
    private static final String sleeping = "icmaze/logMonster.sleeping";

    private static final int perceptionRadius = 5;
    private static final int avance = 10;

    private static final int IMMUNITY_DURATION_FRAMES = 20;


    private Vector anchor;

    private Orientation[] targetingOrders;
    private OrientedAnimation targetingAnimation;

    private Orientation[] movingOrders;
    private OrientedAnimation movingAnimation;

    private Orientation[] sleepingOrders;
    private OrientedAnimation sleepingAnimation;

    private State currentState;

    private final AreaGraph areaGraph;
    private final int difficulty;

    private final Cooldown reorientationCooldown;
    private final Cooldown stateTransitionCooldown;

    private ICMazePlayer target;

    private Health health;
    private int immunityFrames;
    private boolean isImmune;
    private boolean forceToSleep; // Player won the game

    /**
     * Creates a new log monster.
     * @param area (Area): the area where the monster is located
     * @param orientation (Orientation): initial orientation of the monster
     * @param position (DiscreteCoordinates): initial position of the monster
     * @param maxHP (int): maximum health points of the monster
     * @param initialState (State): initial state of the monster
     * @param areaGraph (AreaGraph): navigation graph of the area
     * @param difficulty (int): difficulty level affecting behavior transitions
     */
    public LogMonster(Area area,
                      Orientation orientation,
                      DiscreteCoordinates position,
                      int maxHP,
                      State initialState,
                      AreaGraph areaGraph,
                      int difficulty) {

        super(area, orientation, position, maxHP, perceptionRadius);

        this.currentState = initialState;
        this.areaGraph = areaGraph;
        this.difficulty = difficulty;

        /** cooldowns */
        this.reorientationCooldown = new Cooldown(0.75f);
        this.stateTransitionCooldown = new Cooldown(3.0f);

        /** immunity */
        this.immunityFrames = 0;
        this.isImmune = false;

        /** health */
        this.health = new Health(this, Transform.I.translated(0, 2f), maxHP, false);

        /** animation data */
        this.anchor = new Vector(-0.5f, 0);

        this.targetingOrders = new Orientation[]{
                Orientation.DOWN, Orientation.UP, Orientation.RIGHT, Orientation.LEFT
        };
        this.targetingAnimation = new OrientedAnimation(
                awake, ANIMATION_DURATION / 3, this,
                new Vector(-0.5f, 0.25f), targetingOrders,
                4, 2, 2, 32, 32, true
        );

        this.movingOrders = new Orientation[]{
                Orientation.DOWN, Orientation.UP, Orientation.RIGHT, Orientation.LEFT
        };
        this.movingAnimation = new OrientedAnimation(
                moving, ANIMATION_DURATION / 3, this,
                new Vector(-0.5f, 0.25f), movingOrders,
                4, 2, 2, 32, 32, true
        );

        this.sleepingOrders = new Orientation[]{
                Orientation.DOWN, Orientation.LEFT, Orientation.UP, Orientation.RIGHT
        };
        this.sleepingAnimation = new OrientedAnimation(
                sleeping, ANIMATION_DURATION / 3, this,
                new Vector(-0.5f, 0.25f), sleepingOrders,
                1, 2, 2, 32, 32, true
        );
        forceToSleep = false;
    }

    /**
     * Represents the possible states of the log monster.
     */

    public enum State { SLEEPING, MOVING, TARGETING }
    /**
     * Handles interactions between the log monster and other entities.
     */
    private class ICMazeLogMonsterInteractionHandler implements ICMazeInteractionVisitor {
        /**
         * Handles interaction with the player.
         * @param player (ICMazePlayer): the interacting player
         * @param isCellInteraction (boolean): whether the interaction is a cell interaction
         */
        @Override
        public void interactWith(ICMazePlayer player, boolean isCellInteraction) {
            /**if the monster is sleeping does nothing */
            if (currentState == State.SLEEPING) return;

            if (!isCellInteraction) {
                /** cell in front of the  monster */
                DiscreteCoordinates inFront = getCurrentMainCellCoordinates().jump(getOrientation().toVector());

                if (player.getCurrentMainCellCoordinates().equals(inFront)) {
                        player.loseHP(1);
                } else {
                    /** keeps the player stored */
                    target = player;
                }
            }
        }
    }


    private final ICMazeLogMonsterInteractionHandler handler = new ICMazeLogMonsterInteractionHandler();
    /**
     * Interacts with the given interactable.
     * @param other (Interactable): the interactable entity
     * @param isCellInteraction (boolean): whether the interaction is a cell interaction
     */
    public void interactWith(Interactable other, boolean isCellInteraction){
        other.acceptInteraction(handler, isCellInteraction);
    }
    /**
     * Updates the monster state.
     * Updates immunity, animations, and handles behavior depending on the
     * current state.
     * @param deltaTime (float): time elapsed since last update
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        /** evolution of immunity */
        if (isImmune) {
            immunityFrames -= deltaTime;
            if (immunityFrames <= 0) {
                isImmune = false;
            }
        }

        /** updates state */
        if(!forceToSleep) {
            switch (currentState) {
                case SLEEPING:
                    sleepingAnimation.update(deltaTime);
                    handleSleepingState(deltaTime);
                    break;

                case MOVING:
                    movingAnimation.update(deltaTime);
                    handleMovingState(deltaTime);
                    break;

                case TARGETING:
                    targetingAnimation.update(deltaTime);
                    handleTargetingState(deltaTime);
                    break;
            }
        }else{
            currentState = State.SLEEPING;
            sleepingAnimation.update(deltaTime);
        }
    }
    /**
     * Makes the monster lose health points
     * Activates temporary immunity when damage is taken.
     * @param HP (int): amount of health points to remove
     */
    @Override
    public void loseHP(int HP) {
        if (!isDead() && !isImmune && health.isOn()) {
            // Activer l'immunité
            isImmune = true;
            immunityFrames = IMMUNITY_DURATION_FRAMES;
            health.decrease(HP);

            super.loseHP(HP);
        }
    }

    /**
     * Draw the log monster on the canvas.
     * Displays the appropriate animation depending on the current state
     * and immunity status
     * @param canvas (Canvas): the canvas to draw on
     */
    @Override
    public void draw(ch.epfl.cs107.play.window.Canvas canvas) {
        super.draw(canvas);
        if(!isVanishing()) {
           if(!isImmune || immunityFrames % 2 == 0){
               if(!forceToSleep) {
                   switch (currentState) {
                       case SLEEPING:
                           sleepingAnimation.draw(canvas);
                           break;
                       case MOVING:
                           movingAnimation.draw(canvas);
                           break;
                       case TARGETING:
                           targetingAnimation.draw(canvas);
                           break;
                   }
               }else{
                   movingAnimation.reset();
                   targetingAnimation.reset();
                   sleepingAnimation.draw(canvas);
               }
           }
            if(isImmune){
                health.draw(canvas);
            }
        }

    }

    /**
     * Handles the sleeping state behavior.
     * @param deltaTime (float): time elapsed since last update
     */

    private void handleSleepingState(float deltaTime) {
        if (reorientationCooldown.ready(deltaTime)) {
            orientate(getOrientation().hisLeft()); // Tourne a gauche
            reorientationCooldown.reset();
        }
        if (stateTransitionCooldown.ready(deltaTime)) {
            double pTransition = (double) Difficulty.HARDEST / difficulty;
            if (RandomGenerator.rng.nextDouble() < pTransition) {
                currentState = State.MOVING;
                movingAnimation.reset();
            }
            stateTransitionCooldown.reset();
        }
    }
    /**
     * Handles the moving state behavior.
     * @param deltaTime (float): time elapsed since last update
     */
    private void handleMovingState(float deltaTime) {
        if (reorientationCooldown.ready(deltaTime)) {
            Orientation newOrientation = Orientation.values()[RandomGenerator.rng.nextInt(4)];
            orientate(newOrientation); // Change aleatoirement d'orientation
            move(avance);
            reorientationCooldown.reset();
        }
        if (stateTransitionCooldown.ready(deltaTime)) {
            // Verifie si une cible est en vue
            if (target != null && isTargetInView()) {
                double pTransition = (double) Difficulty.HARDEST / difficulty;
                if (RandomGenerator.rng.nextDouble() < pTransition) {
                    currentState = State.TARGETING;
                    targetingAnimation.reset();
                }
            }
            stateTransitionCooldown.reset();
        }
    }
    /**
     * Checks whether the target is within the perception radius.
     * @return (boolean): {@code true} if the target is in view
     */
    private boolean isTargetInView() {
        if (target == null) return false;

        DiscreteCoordinates targetPos = target.getCurrentMainCellCoordinates();
        DiscreteCoordinates currentPos = getCurrentMainCellCoordinates();

        /** checks wether the target is visible in the radius */
        return Math.abs(targetPos.x - currentPos.x) <= perceptionRadius &&
                Math.abs(targetPos.y - currentPos.y) <= perceptionRadius;
    }
    /**
     * Handles the targeting state behavior.
     * @param deltaTime (float): time elapsed since last update
     */
    public void handleTargetingState(float deltaTime) {
        /** checks wether the target is still  visible  */
        if (target == null || !isTargetInView()) {
            target = null;
            currentState = State.MOVING;
            movingAnimation.reset();
            return;
        }

        if (reorientationCooldown.ready(deltaTime)) {
            Orientation nextOrientation = getNextOrientation();
            if (nextOrientation != null) {
                orientate(nextOrientation);
                move(ANIMATION_DURATION);
            }
            reorientationCooldown.reset();
        }

        if (stateTransitionCooldown.ready(deltaTime)) {
            double pTransition = (double) Difficulty.HARDEST / difficulty;
            if (RandomGenerator.rng.nextDouble() < (1 - pTransition)) {
                currentState = State.SLEEPING;
                sleepingAnimation.reset();
            }
            stateTransitionCooldown.reset();
        }
    }


    public void sleep(){
        forceToSleep = true;
    }
    /**
     * Gets the list of cells visible by the log monster.
     * @return (List<DiscreteCoordinates>): list of visible cells
     */
    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        /** returns cells in radius */
        List<DiscreteCoordinates> view = new ArrayList<>();
        DiscreteCoordinates current = getCurrentMainCellCoordinates();
        int radius = 5;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                view.add(new DiscreteCoordinates(current.x + dx, current.y + dy));
            }
        }
        return view;
    }
    /**
     * Computes the next orientation to reach the current target.
     * @return (Orientation): next orientation, or {@code null} if no path exists
     */
    @Override
    public Orientation getNextOrientation() {
        if (target == null) return null;
        DiscreteCoordinates start = getCurrentMainCellCoordinates();
        DiscreteCoordinates targetPos = target.getCurrentMainCellCoordinates();

        Queue<Orientation> path = areaGraph.shortestPath(start, targetPos);
        if (path == null || path.isEmpty()) {
            return null;
        }
        return path.poll();  /** first orientation in path */

    }
}
