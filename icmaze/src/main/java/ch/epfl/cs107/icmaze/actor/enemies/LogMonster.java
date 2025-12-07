package ch.epfl.cs107.icmaze.actor.enemies;


import ch.epfl.cs107.icmaze.RandomGenerator;
import ch.epfl.cs107.icmaze.actor.ICMazePlayer;
import ch.epfl.cs107.icmaze.actor.util.Cooldown;
import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.OrientedAnimation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Vector;

import java.util.Collections;
import java.util.List;

import static ch.epfl.cs107.play.math.Orientation.*;
import static ch.epfl.cs107.play.math.Orientation.LEFT;

public class LogMonster extends PathFinderEnemy {

    // Attributs d'animation
    private final static int ANIMATION_DURATION = 30 ;
    private final Vector anchor = new Vector(-.5f,0);

    private final static String awake = "icmaze/logMonster";
    private final  Orientation[] targetingOrders = new Orientation []{ Orientation.DOWN ,
            Orientation.UP, Orientation.RIGHT , Orientation.LEFT};
    private final OrientedAnimation targetingAnimation = new OrientedAnimation(awake,
            ANIMATION_DURATION/3, this ,
            new Vector(-0.5f, 0.25f), targetingOrders ,
            4, 2, 2, 32, 32, true);

    private final static String moving = "icmaze/logMonster.wakingUp";
    private final Orientation[] movingOrders = new Orientation []{ Orientation.DOWN , Orientation.UP,
            Orientation.RIGHT , Orientation.LEFT};
    private final OrientedAnimation movingAnimation = new OrientedAnimation(moving,
            ANIMATION_DURATION/3, this ,
            new Vector(-0.5f, 0.25f), movingOrders ,
            4, 2, 2, 32, 32, true);


//    private final static String wakingUp = "icmaze/logMonster.wakingUp";
    private final static String sleeping = "icmaze/logMonster.sleeping";
    private final Orientation[] sleepingOrders = new Orientation []{ Orientation.DOWN , Orientation.LEFT ,
            Orientation.UP, Orientation.RIGHT};
    private final OrientedAnimation sleepingAnimation = new OrientedAnimation(sleeping,
            ANIMATION_DURATION/3, this ,
            new Vector(-0.5f, 0.25f), sleepingOrders ,
            1, 2, 2, 32, 32, true);

    private State currentState;
    private static final int perceptionRadius = 5;

    // Temps d'attentes
    private final Cooldown reorientationCooldown;
    private final Cooldown stateTransitionCooldown;

    /**
     * Default AreaEntity constructor
     *
     * @param area             (Area): Owner area. Not null
     * @param orientation      (Orientation): Initial orientation of the entity in the Area. Not null
     * @param position         (DiscreteCoordinate): Initial position of the entity in the Area. Not null
     * @param maxHP
     */
    public LogMonster(Area area, Orientation orientation, DiscreteCoordinates position, int maxHP, State initialState) {
        super(area, orientation, position, maxHP, perceptionRadius);
        this.currentState = initialState;

        // Initialiser les cooldowns
        this.reorientationCooldown = new Cooldown(0.75f);
        this.stateTransitionCooldown = new Cooldown(3.0f);
    }

    public enum State{
        SLEEPING,
        MOVING,
        TARGETING
    }

    private class ICMazeEnemyInteractionHandler implements ICMazeInteractionVisitor {
        @Override
        public void interactWith(ICMazePlayer player, boolean isCellInteraction){
            if(isViewInteractable() && !currentState.equals(State.SLEEPING)){
                player.loseHP(1);
            }
        }

    }

    private final ICMazeEnemyInteractionHandler handler = new ICMazeEnemyInteractionHandler();

    public void interactWith(Interactable other, boolean isCellInteraction){
        other.acceptInteraction(handler, isCellInteraction);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        // Mettre à jour l'animation
        switch(currentState) {
            case SLEEPING:
                sleepingAnimation.update(deltaTime);

                // Utilise ready(deltaTime) qui gère le compteur
                if(reorientationCooldown.ready(deltaTime)) {
                    // Tourne à gauche
                    orientate(getOrientation());
                }
                break;

            case MOVING:  // Renomme-le en WANDERING pour cohérence
                movingAnimation.update(deltaTime);

                if(reorientationCooldown.ready(deltaTime)) {
                    // Change d'orientation aléatoirement
                    Orientation[] allOrientations = Orientation.values();
                    int randomIndex = RandomGenerator.rng.nextInt(allOrientations.length);
                    orientate(allOrientations[randomIndex]);

                    // Essaie de bouger
                    move(10);  // Tu as 10 frames pour le déplacement
                }
                break;

            case TARGETING:
                // On verra plus tard
                break;
        }
    }
    @Override
    public void draw(ch.epfl.cs107.play.window.Canvas canvas) {
        super.draw(canvas);
        if(!isVanishing()) {
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
        }
    }

    @Override
    public Orientation getOrientation() {
        return LEFT;
    }
}
