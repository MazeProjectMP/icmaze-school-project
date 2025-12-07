package ch.epfl.cs107.icmaze.actor.enemies;


import ch.epfl.cs107.icmaze.RandomGenerator;
import ch.epfl.cs107.icmaze.actor.Health;
import ch.epfl.cs107.icmaze.actor.ICMazePlayer;
import ch.epfl.cs107.icmaze.actor.util.Cooldown;
import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.OrientedAnimation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Transform;
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

    // health interaction
    private Health health;
    private final int MONSTER_MAX_LIFE = 3;
    //imunite
    private boolean isImmune  = false;
    private float immunityTimer = 0f;
    Cooldown immunityCooldown ;

    private boolean isBroken  = false;
    private boolean showHealth = false;
    private final int DAMAGE_AMOUNT = 1; // Dégâts infligés au monstre
    private int damageMonster = 0 ;


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

        health = new Health(this, Transform.I.translated(-0.5, 1.75f), MONSTER_MAX_LIFE, false);

//        this.DAMAGE_AMOUNT = pointsDiminish;
        immunityCooldown = new Cooldown(0.4f);
        isImmune  = false;
        showHealth = false;
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

        if (isImmune) {//affichage de la barre avec cooldown

            if (immunityCooldown.ready(deltaTime)) {// qd le temps est ecoule retourne true
                isImmune = false;
            }
        }
        // Gestion de l'affichage de la barre de vie
        if (showHealth) {
            if (immunityCooldown.ready(deltaTime)) {
                showHealth = false;
            }
        }

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
        // Afficher la barre de vie
        if (health.isOn() && !isBroken &&isImmune) {
            health.draw(canvas);
        }
        super.draw(canvas);
    }
    // Méthode pour prendre des dégâts
    public void takeDamage(int damage) {
        System.out.println("Taking damage " + damage);
        if (!isBroken && !isImmune&& health.isOn()) {
            //affiche barre de vie
            showHealth = true;

            isImmune = true; //active l'immunité

            immunityCooldown.reset(); //reintialise le timer

            health.decrease(damage);//inflige les degats

            // Si le monstre meurt
            if (health.isOff()) {
                isBroken = true;
                getOwnerArea().unregisterActor(this);


            }
        }
    }
    public boolean isDead() {
        return isBroken || health.isOff();
    }


    @Override
    public Orientation getOrientation() {
        return LEFT;
    }
    @Override
    public boolean isViewInteractable() {
        return true; // Le monstre peut être attaqué à distance

    }
    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICMazeInteractionVisitor) v).interactWith(this, isCellInteraction);
    }
    public boolean takeCellSpace() {
        return !isDead() && !isBroken ;
    }

}
