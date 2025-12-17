package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.List;

/**
 * Represents a portal in the game.
 * A portal allows the player to move from one area to another.
 * It may be open, locked, or invisible, and can require a key
 * to be activated. When used, it provides a destination area
 * and destination coordinates.
 */

public class Portal extends AreaEntity {

    private String areaName;
    private final static int NO_KEY_ID = Integer.MIN_VALUE;
    private int identificator;
    private PortalState state;
    private Sprite portal;
    private boolean isNotTraversable;
    private String destinationAreaName ;
    private DiscreteCoordinates destinationCoordinates;

    /**
     * Creates a new portal requiring a key identifier.
     * @param area (Area): the area where the portal is located
     * @param orientation (Orientation): initial orientation of the portal
     * @param portalCoordinates (DiscreteCoordinates): initial position of the portal
     * @param identificator (int): identifier required to open the portal
     */
    public Portal(Area area, Orientation orientation, DiscreteCoordinates portalCoordinates, int identificator) {
        super(area, orientation, portalCoordinates);
        state = PortalState.INVISIBLE;
        this.areaName = area.getTitle();
        this.identificator = identificator;
        destinationAreaName = null;
    }

    /**
     * Creates a new portal that does not require a key identifier.
     * @param area (Area): the area where the portal is located
     * @param orientation (Orientation): initial orientation of the portal
     * @param portalCoordinates (DiscreteCoordinates): initial position of the portal
     */
    public Portal(Area area, Orientation orientation, DiscreteCoordinates portalCoordinates) {
        super(area, orientation, portalCoordinates);
        state = PortalState.INVISIBLE;
        this.areaName = area.getTitle();
        this.identificator = NO_KEY_ID;
    }

    /**
     * Creates a copy of the given portal.
     * @param portal (Portal): portal to copy
     */
    public Portal(Portal portal) {
        super(portal.getOwnerArea(), portal.getOrientation(), portal.getCurrentMainCellCoordinates());
        state = portal.getState();
        this.areaName = portal.areaName;
        this.destinationCoordinates = portal.getDestinationCoordinates();
        this.identificator = portal.identificator;
        this.isNotTraversable = portal.isNotTraversable();
        this.destinationAreaName = portal.destinationAreaName;
    }

    /**
     * Gets the destination area name.
     * @return (String): destination area name, may be {@code null}
     */

    public String getDestinationArea() {
        return destinationAreaName;
    }

    /**
     * Sets the destination area name.
     * @param destinationAreaName (String): destination area name
     */
    public void setDestinationArea(String destinationAreaName) {
        this.destinationAreaName = destinationAreaName;
    }

    /**
     * Sets the destination coordinates.
     * @param coordinates (DiscreteCoordinates): destination coordinates
     */
    public void setDestinationCoordinates(DiscreteCoordinates coordinates) {
        this.destinationCoordinates = coordinates;
    }

    /**
     * Gets the destination coordinates.
     * @return (DiscreteCoordinates): destination coordinates, may be {@code null}
     */
    public DiscreteCoordinates getDestinationCoordinates() {
        return destinationCoordinates;
    }

    /**
     * Represents the possible portal states.
     */
    public enum PortalState {
        OPEN,
        LOCKED,
        INVISIBLE,
    }

    /**
     * Sets the portal state.
     * @param state (PortalState): new state
     */
    public void setState(PortalState state) {
        this.state = state;
    }

    /**
     * Gets the portal state.
     * @return (PortalState): current state
     */
    public PortalState getState() {
        return state;
    }

    /**
     * Locks the portal with the given identifier and sets its destination area.
     * @param identificator (int): required identifier
     * @param destinationAreaName (String): destination area name
     */
    public void lock(int identificator, String destinationAreaName) {
        this.identificator = identificator;
        setState(PortalState.LOCKED);
        setDestinationArea(destinationAreaName);
    }

    /**
     * Unlocks the portal and sets its destination area.
     * @param destinationAreaName (String): destination area name
     */
    public void unlock(String destinationAreaName) {
        setState(PortalState.OPEN);
        setDestinationArea(destinationAreaName);
    }

    /**
     * Attempts to open the portal with the given identifier.
     * @param identificator (int): identifier to test
     * @return (boolean): {@code true} if the portal is opened, {@code false} otherwise
     */
    public boolean open(int identificator) {
        if (this.identificator == identificator) {
            setState(PortalState.OPEN);
            return true;
        }
        return false;
    }

    /**
     * Gets the list of cells currently occupied by the portal.
     * @return (List<DiscreteCoordinates>): list of occupied cells
     */
    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        DiscreteCoordinates coord = getCurrentMainCellCoordinates();
        return List.of(coord, coord.jump(new
                Vector((getOrientation().ordinal() + 1) % 2,
                getOrientation().ordinal() % 2)));
    }

    /**
     * Indicates whether the portal blocks the cell space.
     * @return (boolean): {@code true} if the portal is not traversable
     */
    @Override
    public boolean takeCellSpace() {
        return isNotTraversable();
    }

    /**
     * Indicates whether the portal can be interacted with via cell interaction
     * @return (boolean): {@code true}
     */
    @Override
    public boolean isCellInteractable() {
        return true;
    }

    /**
     * Indicates whether the portal can be interacted with from a distance.
     * @return (boolean): {@code true}
     */
    @Override
    public boolean isViewInteractable() {
        return true;
    }

    /**
     * Sets whether the portal is not traversable.
     * @param isNotTraversable (boolean): {@code true} to make the portal not traversable
     */
    public void setNotTraversable(boolean isNotTraversable) {
        this.isNotTraversable = isNotTraversable;
    }

    /**
     * Indicates whether the portal is not traversable.
     * @return (boolean): {@code true} if the portal is not traversable
     */
    public boolean isNotTraversable() {
        return isNotTraversable;
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
     * Updates the portal state.
     * Sets traversability and updates the portal sprite depending on the current state.
     * @param deltaTime (float): time elapsed since last update
     */
    @Override
    public void update(float deltaTime) {
        switch (this.getState()) {
            case OPEN:
                setNotTraversable(false);
                break;
            case LOCKED:
                setNotTraversable(true);
                portal = new Sprite("icmaze/chained_wood_" + this.getOrientation().ordinal(),
                        (this.getOrientation().ordinal() + 1) % 2 + 1, this.getOrientation().ordinal() % 2 + 1,
                        this);
                break;
            case INVISIBLE:
                setNotTraversable(true);
                portal = new Sprite("icmaze/invisibleDoor_" + this.getOrientation().ordinal(),
                        (this.getOrientation().ordinal() + 1) % 2 + 1,
                        this.getOrientation().ordinal() % 2 + 1, this);
                break;
        }
    }

    /**
     * Draws the portal on the canvas.
     * Nothing is drawn when the portal is open.
     * @param canvas (Canvas): the canvas to draw on
     */
    @Override
    public void draw(Canvas canvas) {
        if (PortalState.INVISIBLE.equals(getState()) || PortalState.LOCKED.equals(getState())) {
            portal.draw(canvas);
        }
    }
}
