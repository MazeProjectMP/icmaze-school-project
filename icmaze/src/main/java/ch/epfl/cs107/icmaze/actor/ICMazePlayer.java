package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.icmaze.KeyBindings;
import ch.epfl.cs107.icmaze.actor.collectable.Heart;
import ch.epfl.cs107.icmaze.actor.collectable.Key;
import ch.epfl.cs107.icmaze.actor.collectable.Pickaxe;
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

    /**
     * @param owner (Area) area to which the player belong
     * @param orientation (Orientation) the initial orientation of the player
     * @param coordinates (DiscreteCoordinates) the initial position in the grid
     * @param spriteName (String) name of the sprite used as graphical representation
     */
    public ICMazePlayer(Area owner, Orientation orientation, DiscreteCoordinates coordinates, String spriteName) {
        super(owner, orientation, coordinates);
        resetMotion();
    }

    // Differents etats du personnage
    public enum PlayerState {
        IDLE, // Le personnage est au repos
        MOVING, // Le personnage bouge
        INTERACTING, // Le personnage demande une interaction

    }

    private class ICMazePlayerInteractionHandler implements ICMazeInteractionVisitor {
        @Override
        public void interactWith(Heart heart, boolean isCellInteraction){
            if(isCellInteraction){
                heart.hide();
            }
        }
        @Override
        public void interactWith(Pickaxe pickaxe,boolean isCellInteraction){
            if(isCellInteraction){
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
                for(Integer keyID : keysID){
                    portal.open(keyID);
                }
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
        // Change l'etat du personnage si il ne bouge plus
        if(!isDisplacementOccurs()){
            state = PlayerState.IDLE;
        }
        Keyboard keyboard = getOwnerArea().getKeyboard();
        switch (state){
            case IDLE :
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
                break;
            case INTERACTING:
                // Repasse en idle
                if (!keyboard.get(keys.interact()).isDown()){
                    state = PlayerState.IDLE;
                }
                break;
        }
        super.update(deltaTime);
    }

    /**
     * @param canvas target, not null
     */
    @Override
    public void draw(ch.epfl.cs107.play.window.Canvas canvas) {
        playerAnimation.draw(canvas);
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
        return (state == PlayerState.INTERACTING); // Verifie l'etat
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
