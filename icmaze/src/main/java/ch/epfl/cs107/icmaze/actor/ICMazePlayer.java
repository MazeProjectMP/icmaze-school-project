package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.icmaze.KeyBindings;
import ch.epfl.cs107.icmaze.actor.collectable.Heart;
import ch.epfl.cs107.icmaze.actor.collectable.Key;
import ch.epfl.cs107.icmaze.actor.collectable.Pickaxe;
import ch.epfl.cs107.icmaze.actor.collectable.rare.RareItems;
import ch.epfl.cs107.icmaze.actor.enemies.Enemy;
import ch.epfl.cs107.icmaze.actor.rocks.CommonRock;
import ch.epfl.cs107.icmaze.actor.rocks.Rock;
import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.actor.MovableAreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.OrientedAnimation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Transform;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Button;
import ch.epfl.cs107.play.window.Keyboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static ch.epfl.cs107.play.math.Orientation.*;

/**
 * Represents the main player character in the ICMaze game.
 * <p>
 * The {@code ICMazePlayer} is a movable entity controlled via {@link KeyBindings}. The player
 * can collect items (e.g. {@link Heart}, {@link Pickaxe}, {@link Key}, {@link RareItems}),
 * interact with the environment (e.g. {@link Rock}, {@link Portal}), and fight enemies using
 * the pickaxe attack state. The player has health points with temporary immunity after taking
 * damage and stores collected resources in an {@link Inventory}.
 * </p>
 */
public class ICMazePlayer extends MovableAreaEntity implements Interactor, Interactable {

    private final static KeyBindings.PlayerKeyBindings keys = KeyBindings.PLAYER_KEY_BINDINGS; // Initialise les touches du joueur
    private final static String prefix = "icmaze/player"; // Lien de l'image
    private final static int ANIMATION_DURATION = 1 ;
    private PlayerState state = PlayerState.IDLE; // L'etat du personnage
    private final static int MOVE_DURATION = 5;

    /// Attributs d'animation :
    private final Vector anchor = new Vector(0,0);
    private final  Orientation[] orders = {DOWN, RIGHT, UP, LEFT};
    private final OrientedAnimation playerAnimation = new OrientedAnimation(prefix, ANIMATION_DURATION, this,anchor, orders, 4, 1, 2, 16, 32, true);

    /// Info du portail a traverser
    private String destinationArea;
    private DiscreteCoordinates destinationCoordinates;

    /// Points de vies
    private final int maxHP;
    private int currentHP;
    private boolean isDead = false;

    /// Pickaxe
    private boolean isRock = false;
    private boolean hasAttacked = false;

    /// Pickaxe Animation
    private final Vector pickaxeAnchor = new Vector(-0.5f, 0);
    private final Orientation[] pickaxeOrders = {DOWN, UP, RIGHT, LEFT};
    private final static int PICKAXE_ANIMATION_DURATION = 5;
    private OrientedAnimation pickaxeAnimation = new OrientedAnimation(prefix + ".pickaxe", PICKAXE_ANIMATION_DURATION, this, pickaxeAnchor, pickaxeOrders, 4, 2, 2, 32, 32);
    private OrientedAnimation currentAnimation;

    /// health interaction
    private Health health;
    private static final int IMMUNITY_DURATION = 40;
    private int immunityFrames = 0;
    private boolean isImmune = false;

    private final Inventory inventory = new Inventory();

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

    /**
     * Enumeration of the possible states of the player.
     *   {@link #IDLE}: the player is not moving and not performing an action
     *   {@link #MOVING}: the player is currently moving</li>
     *   {@link #INTERACTING}: the player requests a view interaction (e.g. opening a portal)
     *   {@link #ATTACKING_WITH_PICKAXE}: the player plays the pickaxe attack animation
     */    public enum PlayerState {
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
            if(isCellInteraction && !getInventory().hasPickaxe()){
                getInventory().addPickaxe();
                pickaxe.hide();
            }
        }

        @Override
        public void interactWith(Key key, boolean isCellInteraction){
            if(isCellInteraction){
                getInventory().addKey(key.getIdentificator());
                key.hide();
            }
        }

        @Override
        public void interactWith(Portal portal, boolean isCellInteraction){
            if(isCellInteraction && portal.getState() == Portal.PortalState.OPEN) {
                destinationArea = portal.getDestinationArea();
                destinationCoordinates =  portal.getDestinationCoordinates();
            } else if(isViewInteractable() && state.equals(PlayerState.INTERACTING)){
                ArrayList<Integer> keys = getInventory().getKeysIds();
                for(int i = 0; i < keys.size(); i++){
                    if(portal.open(keys.get(i))){
                        getInventory().useKey(keys.get(i));
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

        @Override
        public void interactWith(RareItems rareItems, boolean isCellInteraction){
            if(isCellInteraction){
                getInventory().addValue(rareItems.getValue());
                rareItems.hide();
            }
        }
    }

    private final ICMazePlayerInteractionHandler handler = new ICMazePlayerInteractionHandler();

    /**
     * Forwards an interaction to the player interaction handler.
     * @param other (Interactable): the interactable entity
     * @param isCellInteraction (boolean): whether the interaction is a cell interaction
     */
    public void interactWith(Interactable other, boolean isCellInteraction){
        other.acceptInteraction(handler, isCellInteraction);
    }
    /**
     * Gets the destination area set by a portal interaction.
     * @return (String): the destination area name, or {@code null} if none is set
     */
    public String getDestinationArea(){
        return destinationArea;
    }

    /**
     * Gets the destination coordinates set by a portal interaction.
     * @return (DiscreteCoordinates): the destination coordinates, or {@code null} if none is set
     */
    public DiscreteCoordinates getDestinationCoordinates() {
        return destinationCoordinates;
    }

    /**
     * Clears the stored portal destination information after teleportation.
     */    public void clearPortalToTraverse() {
        destinationArea = null;
        destinationCoordinates = null;
    }

    /**
     * Gets the player inventory.
     * @return (Inventory): the player inventory
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Updates the player state.
     * Handles movement, state transitions (idle/moving/interacting/attacking),
     * immunity timing, and animation updates depending on inputs.
     * @param deltaTime (float): time elapsed since last update
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

                if (keyboard.get(keys.pickaxe()).isPressed() && getInventory().hasPickaxe()) {
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
     * Draws the player on the canvas
     * Draws the inventory UI, the current animation (blinking if immune),
     * and the health bar if enabled.
     * @param canvas (Canvas): the canvas to draw on
     */
    @Override
    public void draw(ch.epfl.cs107.play.window.Canvas canvas) {
        getInventory().draw(canvas, getOwnerArea().getCameraScaleFactor());
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

    /**
     * Makes the player lose health points.
     * Damage is applied only if the player is not currently immune. After taking damage,
     * the player becomes temporarily immune. If health reaches zero, the player is marked dead.
     * @param HP (int): amount of health points to remove
     */
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

    /**
     * Indicates whether the player is dead.
     * @return (boolean): {@code true} if the player is dead
     */
    public boolean isDead(){
        return isDead;
    }

    /**
     * Indicates whether the player blocks the cell it occupies.
     * @return (boolean): {@code true}, as the player occupies space on the grid
     */
    @Override
    public boolean takeCellSpace() {
        return true;
    }

    /**
     * Indicates whether the player can be interacted with via cell interaction.
     * @return (boolean): {@code true}
     */
    @Override
    public boolean isCellInteractable() {
        return true;
    }

    /**
     * Indicates whether the player can be interacted with via view interaction.
     * @return (boolean): {@code true}
     */
    @Override
    public boolean isViewInteractable() {
        return true;
    }

    /**
     * Gets the list of cells currently occupied by the player.
     * @return (List<DiscreteCoordinates>): a singleton list containing the current cell coordinates
     */
    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    /**
     * Gets the cell immediately in front of the player.
     * @return (List<DiscreteCoordinates>): a singleton list containing the cell in front of the player
     */
    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates().jump(getOrientation().toVector()));
    }

    /**
     * Indicates whether the player wants cell interactions.
     * @return (boolean): {@code true}
     */
    @Override
    public boolean wantsCellInteraction() {
        return true;
    }

    /**
     * Indicates whether the player wants view interactions.
     * View interactions are enabled while interacting or attacking with the pickaxe.
     * @return (boolean): {@code true} if the player requests view interactions
     */
    @Override
    public boolean wantsViewInteraction() {
        // Retourne true pour INTERACTING et ATTACKING_WITH_PICKAXE
        return (state == PlayerState.INTERACTING || state == PlayerState.ATTACKING_WITH_PICKAXE);
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
     * Centers the camera on the player
     */
    public void centerCamera() {
        getOwnerArea().setViewCandidate(this);
    }
}