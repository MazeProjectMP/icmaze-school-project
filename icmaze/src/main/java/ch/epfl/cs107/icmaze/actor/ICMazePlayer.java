package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.icmaze.KeyBindings;
import ch.epfl.cs107.icmaze.actor.collectable.Heart;
import ch.epfl.cs107.icmaze.actor.collectable.Key;
import ch.epfl.cs107.icmaze.actor.collectable.Pickaxe;
import ch.epfl.cs107.icmaze.actor.enemies.Boss;
import ch.epfl.cs107.icmaze.actor.enemies.Enemy;
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
    private final static String prefix = "icmaze/player"; // Lien de l'image
    private final static int ANIMATION_DURATION = 1 ;
    private PlayerState state = PlayerState.IDLE; // L'etat du personnage
    private final static int MOVE_DURATION = 5;

    // Attributs d'animation :
    private final Vector anchor = new Vector(0,0);
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
    private boolean isDead = false;

    // Pickaxe
    private boolean hasPickaxe = false;
    private boolean isRock = false;
    private boolean hasAttacked = false;

    // Pickaxe Animation
    private final Vector pickaxeAnchor = new Vector(-0.5f, 0);
    private final Orientation[] pickaxeOrders = {DOWN, UP, RIGHT, LEFT};
    private final static int PICKAXE_ANIMATION_DURATION = 5;
    private OrientedAnimation pickaxeAnimation = new OrientedAnimation(prefix + ".pickaxe", PICKAXE_ANIMATION_DURATION, this, pickaxeAnchor, pickaxeOrders, 4, 2, 2, 32, 32);
    private OrientedAnimation currentAnimation;

    // health interaction
    private Health health;
    private static final int IMMUNITY_DURATION = 40;
    private int immunityFrames = 0;
    private boolean isImmune = false;
    /**
     * @param owner (Area) area to which the player belong
     * @param orientation (Orientation) the initial orientation of the player
     * @param coordinates (DiscreteCoordinates) the initial position in the grid
     * @param spriteName (String) name of the sprite used as graphical representation
     */
    public ICMazePlayer(Area owner, Orientation orientation, DiscreteCoordinates coordinates, String spriteName, int maxHP) {
        super(owner, orientation, coordinates);
        this.maxHP = maxHP;
        currentHP = this.maxHP;
        resetMotion();


        health = new Health(this, Transform.I.translated(0, 1.95f), this.currentHP, true);
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
                currentHP += 1;
                isImmune = true;
                immunityFrames = IMMUNITY_DURATION;
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
                rock.loseHP(1);
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
        /** changes state of player if he's not moving or attacking */

        if(!isDisplacementOccurs() && !state.equals(PlayerState.ATTACKING_WITH_PICKAXE)){
            state = PlayerState.IDLE;
        }
        /** dealing with immunity */
        if (isImmune) {
            immunityFrames -= deltaTime;
            if (immunityFrames <= 0) {
                isImmune = false;
            }
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
                /** changes state if player moves  */
                if(isDisplacementOccurs()){
                    state = PlayerState.MOVING;
                }
                /**  does animation if the player is moving  */

                if(state == PlayerState.MOVING){
                    playerAnimation.update(deltaTime);
                }else{
                    playerAnimation.reset();
                }

                if(keyboard.get(keys.interact()).isPressed()){
                    state = PlayerState.INTERACTING;
                }

                if (keyboard.get(keys.pickaxe()).isPressed()&& hasPickaxe) {
                    state = PlayerState.ATTACKING_WITH_PICKAXE;
                }
                break;
            case INTERACTING:
                if (!keyboard.get(keys.interact()).isDown()){
                    state = PlayerState.IDLE;
                }
                break;

            case ATTACKING_WITH_PICKAXE:
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

    /**
     * @param canvas target, not null
     */
    @Override
    public void draw(ch.epfl.cs107.play.window.Canvas canvas) {
        if (isImmune) {
            if (immunityFrames % 2 == 0) {
                currentAnimation.draw(canvas);
            }
        } else {
            currentAnimation.draw(canvas);
        }
        /** draws animation  */

        if (health.isOn()) {
            health.draw(canvas);
        }
    }

    public void loseHP(int HP) {
        if (!isImmune && health.isOn()) {
            health.decrease(HP);
            this.currentHP -= HP;


            isImmune = true;
            immunityFrames = IMMUNITY_DURATION;
        }

        if (currentHP <= 0 || health.isOff()) {
            isDead = true;
        }
    }

    public boolean isDead(){
        return isDead;
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