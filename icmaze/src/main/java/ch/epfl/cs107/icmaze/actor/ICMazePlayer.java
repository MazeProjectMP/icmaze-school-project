package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.icmaze.KeyBindings;
import ch.epfl.cs107.icmaze.actor.collectable.Heart;
import ch.epfl.cs107.icmaze.actor.collectable.Key;
import ch.epfl.cs107.icmaze.actor.collectable.Pickaxe;
import ch.epfl.cs107.icmaze.actor.enemies.Enemy;
import ch.epfl.cs107.icmaze.actor.enemies.LogMonster;
import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.actor.MovableAreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.engine.actor.OrientedAnimation;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.engine.actor.TextGraphics;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Transform;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Button;
import ch.epfl.cs107.play.window.Keyboard;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static ch.epfl.cs107.play.math.Orientation.*;

public class ICMazePlayer extends MovableAreaEntity implements Interactor, Interactable {

    private final static KeyBindings.PlayerKeyBindings keys = KeyBindings.PLAYER_KEY_BINDINGS; // Initialise les touches du joueur
    private final static String prefix = "icmaze/player"; // Lien de l'image ?
    private final static int ANIMATION_DURATION = 1 ;
    private PlayerState state = PlayerState.IDLE; // L'etat du personnage
    private final static int MOVE_DURATION = 7;

    // Attributs d'animation :
    private final Vector anchor = new Vector(-.5f,0);
    private final  Orientation[] orders = {DOWN, RIGHT, UP, LEFT};
    private final OrientedAnimation playerAnimation = new OrientedAnimation(prefix, ANIMATION_DURATION, this,anchor, orders, 4, 1, 2, 16, 32, true);

    // Inventaire des cles
    private final ArrayList<Integer> keysID = new ArrayList<Integer>();

    // Info du portail a traverser
    private String destinationArea;
    private DiscreteCoordinates destinationCoordinates;

    // Points de vies
    private final int maxHP;
    private int currentHP;

    // Pickaxe
    private boolean hasPickaxe = false;
    private boolean isRock = false;
    private boolean hasAttacked = false;

    // Pickaxe Animation
    private final Vector pickaxeAnchor = new Vector(-0.5f, 0);
    private final Orientation[] pickaxeOrders = {DOWN, UP, RIGHT, LEFT};
    private final static int PICKAXE_ANIMATION_DURATION = 5;
    private OrientedAnimation pickaxeAnimation =
            new OrientedAnimation(prefix + ".pickaxe",
                    PICKAXE_ANIMATION_DURATION, this, pickaxeAnchor, pickaxeOrders, 4, 2, 2, 32, 32);
    private OrientedAnimation currentAnimation;

    // health interaction
    private Health health;
    private final int MAX_LIFE = 3;
    private float immunityTimer = 0f;
    private final float IMMUNITY_DURATION = 1.5f; // 2 secondes
    private boolean isImmune = false;
    private LogMonster logMonster;
    /**
     * @param owner (Area) area to which the player belong
     * @param orientation (Orientation) the initial orientation of the player
     * @param coordinates (DiscreteCoordinates) the initial position in the grid
     * @param spriteName (String) name of the sprite used as graphical representation
     */
    public ICMazePlayer(Area owner, Orientation orientation, DiscreteCoordinates coordinates, String spriteName, int maxHP) {
        super(owner, orientation, coordinates);
        this.maxHP = maxHP;
        currentHP = maxHP;
        resetMotion();
        health = new Health(this, Transform.I.translated(-0.5, 1.75f), MAX_LIFE, true);

    }

    public void loseHP(int degat) {
        currentHP -= degat;
    }

    // Differents etats du personnage
    public enum PlayerState {
        IDLE, // Le personnage est au repos
        MOVING, // Le personnage bouge
        INTERACTING, // Le personnage demande une interaction
        ATTACKING_WITH_PICKAXE, // Le personnage attaque avec la pioche
    }

    private class ICMazePlayerInteractionHandler implements ICMazeInteractionVisitor {
        @Override
        public void interactWith(Heart heart, boolean isCellInteraction){
            if(isCellInteraction){
                health.increase(1); // Gagner 1 point de vie
                heart.hide();
            }
        }
        @Override
        public void interactWith(Pickaxe pickaxe,boolean isCellInteraction){
            if(isCellInteraction){
                hasPickaxe = true;
                pickaxe.hide();
            }
        }

        @Override
        public void interactWith(Key key, boolean isCellInteraction){
            if(isCellInteraction){
                keysID.add(key.getIdentificator());
                key.hide();
            }
        }

        @Override
        public void interactWith(Portal portal, boolean isCellInteraction){
            if(isCellInteraction && portal.getState() == Portal.PortalState.OPEN) {
                destinationArea = portal.getDestinationArea();
                destinationCoordinates =  portal.getDestinationCoordinates();
            } else if(isViewInteractable() && state.equals(PlayerState.INTERACTING)){
                for(int i = 0; i < keysID.size(); i++){
                    if(portal.open(keysID.get(i))){
                        keysID.remove(i); // Enleve la cle si utilisee
                    }
                }
            }
        }

        @Override
        public void interactWith(Rock rock, boolean isCellInteraction) {
            isRock = !rock.isBroken();
            if (rock.isViewInteractable() && !hasAttacked && isRock && state.equals(PlayerState.ATTACKING_WITH_PICKAXE)) {
                hasAttacked = true;
//               rock.attackWithPickaxe();
                rock.takeDamage(1);

            }
        }
        public void interactWith(LogMonster monster, boolean isCellInteraction) {
            // Si collision avec le monstre (interaction de cellule)
            if (isCellInteraction) {
                if (!monster.isDead()) {

                    takeDamage(1); // Le player prend 1 dégât
            }}

            // Si attaque avec pioche (interaction de vue)
            if(monster.isViewInteractable() && !hasAttacked && !monster.isDead() && state.equals(PlayerState.ATTACKING_WITH_PICKAXE)){
                hasAttacked = true;
                monster.takeDamage(1); // Le monstre prend 1 dégât
            }
        }

        @Override
        public void interactWith(Enemy enemy, boolean isCellInteraction){
            if(enemy.isViewInteractable() && !hasAttacked && !enemy.isDead() && state.equals(PlayerState.ATTACKING_WITH_PICKAXE)){
                hasAttacked = true;
                enemy.loseHP(1);
            }
        }
    }

    private final ICMazePlayerInteractionHandler handler = new ICMazePlayerInteractionHandler();

    public void interactWith(Interactable other, boolean isCellInteraction){
        other.acceptInteraction(handler, isCellInteraction);
    }

    public String getDestinationArea(){
        return destinationArea;
    }
    public DiscreteCoordinates getDestinationCoordinates() {
        return destinationCoordinates;
    }
    // Reinitialise les infos du portail apres teleportation
    public void clearPortalToTraverse() {
        destinationArea = null;
        destinationCoordinates = null;
    }

    /**
     * @param deltaTime elapsed time since last update, in seconds, non-negative
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        // Gere l'immunite
        if (isImmune) {
            immunityTimer -= deltaTime;
            if (immunityTimer <= 0) {
                isImmune = false;
            }
        }
        // Change l'etat du personnage si il ne bouge plus et n'attacke pas
        if(!isDisplacementOccurs() && !state.equals(PlayerState.ATTACKING_WITH_PICKAXE)){
            state = PlayerState.IDLE;
        }
        if (state != PlayerState.ATTACKING_WITH_PICKAXE) {
            hasAttacked = false;
        }
        Keyboard keyboard = getOwnerArea().getKeyboard();
        switch (state){
            case IDLE :
                currentAnimation = playerAnimation;
                moveIfPressed(Orientation.LEFT, keyboard.get(keys.left()));
                moveIfPressed(Orientation.UP, keyboard.get(keys.up()));
                moveIfPressed(Orientation.RIGHT, keyboard.get(keys.right()));
                moveIfPressed(Orientation.DOWN, keyboard.get(keys.down()));
                // Change l'etat du personnage si un deplacement a Lieu
                if(isDisplacementOccurs()){
                    state = PlayerState.MOVING;
                }
                // Effectue l'animation du personnage lors d'un mouvement
                if(state == PlayerState.MOVING){
                    playerAnimation.update(deltaTime);
                }else{
                    playerAnimation.reset();
                }

                // Mode interaction
                if(keyboard.get(keys.interact()).isPressed()){
                    state = PlayerState.INTERACTING;
                }

                // Mode Attack
                if (keyboard.get(keys.pickaxe()).isPressed()&& hasPickaxe) {
                    state = PlayerState.ATTACKING_WITH_PICKAXE;
                }
                break;
            case INTERACTING:
                // Repasse en idle
                if (!keyboard.get(keys.interact()).isDown()){
                    state = PlayerState.IDLE;
                }
                break;

            case ATTACKING_WITH_PICKAXE:
                // Jouer l'animation de la pioche
                currentAnimation = pickaxeAnimation;
                pickaxeAnimation.update(deltaTime);

                if (pickaxeAnimation.isCompleted() ) {
                    state = PlayerState.IDLE;
                    pickaxeAnimation.reset();
                    hasAttacked = false;
                }
                break;
        }

    }
    public void takeDamage(int damage) {
        if (!isImmune && health.isOn()) {
            health.decrease(damage);
            isImmune = true;
            immunityTimer = IMMUNITY_DURATION;

            // clignotement AAAA
        }
    }

    /**
     * @param canvas target, not null
     */
    @Override
    public void draw(ch.epfl.cs107.play.window.Canvas canvas) {
// Gestion du clignotement pendant l'immunité
        if (isImmune) {
            // Affiche seulement quand le compteur est pair (clignotement)
            long currentTime = System.currentTimeMillis();
            if ((currentTime / 250) % 2 == 0) { // 250ms = 4 fois par seconde
                currentAnimation.draw(canvas);
            }
        } else {
            currentAnimation.draw(canvas);
        }
        health.draw(canvas); // Dessiner la barre de vie

    }


    @Override
    public boolean takeCellSpace() {
        return true;
    }

    @Override
    public boolean isCellInteractable() {
        return true;
    }

    @Override
    public boolean isViewInteractable() {
        return true;
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates().jump(getOrientation().toVector()));
    }

    @Override
    public boolean wantsCellInteraction() {
        return true;
    }

    @Override
    public boolean wantsViewInteraction() {
        // Retourne true pour INTERACTING et ATTACKING_WITH_PICKAXE
        return (state == PlayerState.INTERACTING || state == PlayerState.ATTACKING_WITH_PICKAXE);
    }
    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICMazeInteractionVisitor) v).interactWith(this, isCellInteraction);
    }

    /**
     * Orientate and Move this player in the given orientation if the given button is down
     *
     * @param orientation (Orientation): given orientation, not null
     * @param b           (Button): button corresponding to the given orientation, not null
     */
    private void moveIfPressed(Orientation orientation, Button b) {
        if (b.isDown()) {
            if (!isDisplacementOccurs()) {
                orientate(orientation);
                move(MOVE_DURATION);
            }
        }
    }

    /**
     * Leave an area by unregister this player
     */
    public void leaveArea() {
        getOwnerArea().unregisterActor(this);
    }

    /**
     * makes the player entering a given area
     * @param area     (Area):  the area to be entered, not null
     * @param position (DiscreteCoordinates): initial position in the entered area, not null
     */
    public void enterArea(Area area, DiscreteCoordinates position) {
        area.registerActor(this);
        area.setViewCandidate(this);
        setOwnerArea(area);
        setCurrentPosition(position.toVector());
        resetMotion();
    }

    /**
     * Center the camera on the player
     */
    public void centerCamera() {
        getOwnerArea().setViewCandidate(this);
    }

}
