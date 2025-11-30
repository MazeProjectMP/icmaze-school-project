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

public class Portal extends AreaEntity {

    private DiscreteCoordinates portalCoordinates;
    private String areaName;
    private final static int NO_KEY_ID = Integer.MIN_VALUE;
    private int identificator;

    private PortalState state;
    private Sprite portal;

    private boolean isNotTraversable;

    private String destinationAreaName = null;
    private DiscreteCoordinates destinationCoordinates;
    
    public Portal(Area area, Orientation orientation, DiscreteCoordinates portalCoordinates, int identificator, DiscreteCoordinates destinationCoordinates) {
        super(area, orientation, portalCoordinates);
        state = PortalState.INVISIBLE;
        this.areaName = area.getTitle();
        this.portalCoordinates = portalCoordinates;
        this.destinationCoordinates = destinationCoordinates;
        
        this.identificator = identificator;
    }

    public Portal(Area area, Orientation orientation, DiscreteCoordinates portalCoordinates, DiscreteCoordinates destinationCoordinates) {
        super(area, orientation, portalCoordinates);
        state = PortalState.INVISIBLE;
        this.areaName = area.getTitle();
        this.portalCoordinates = portalCoordinates;
        this.destinationCoordinates = destinationCoordinates;
        this.identificator = NO_KEY_ID;
    }

//    public Portal(Area area, Orientation orientation, DiscreteCoordinates portalPosition, int identificator, DiscreteCoordinates portalCoordinates) {
//        super(area, orientation, portalPosition);
//        state = PortalState.INVISIBLE;
//        this.portalCoordinates = portalCoordinates;
//        this.identificator = identificator;
//    }
//
//    public Portal(Area area, Orientation orientation, DiscreteCoordinates portalPosition, DiscreteCoordinates portalCoordinates) {
//        super(area, orientation, portalPosition);
//        state = PortalState.INVISIBLE;
//        this.portalCoordinates = portalCoordinates;
//        this.identificator = NO_KEY_ID;
//    }
//    public DiscreteCoordinates getportalCoordinates() {
//        return portalCoordinates;
//    }
    public String getDestinationArea() {
        return destinationAreaName;
    }
    public void setDestinationArea(String destinationAreaName) {
        this.destinationAreaName = destinationAreaName;
    }
    public DiscreteCoordinates getDestinationCoordinates() {
        return destinationCoordinates;
    }
    public enum PortalState {
        //https://stackoverflow.com/questions/25761438/understanding-bufferedimage-getrgb-output-values
        OPEN,
        LOCKED,
        INVISIBLE,
    }
    public void setState(PortalState state) {
        this.state = state;
    }
    public PortalState getState() {
        return state;
    }
    public void lock(int identificator, String destinationAreaName) {
        this.identificator = identificator;
        setState(PortalState.LOCKED);
        setDestinationArea(destinationAreaName);
    }
    public void unlock(String destinationAreaName) {
        setState(PortalState.OPEN);
        setDestinationArea(destinationAreaName);
    }
    public void open(int identificator) {
        if  (this.identificator == identificator) {
            setState(PortalState.OPEN);
        }
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells(){
        DiscreteCoordinates coord = getCurrentMainCellCoordinates();
        return List.of(coord , coord.jump(new
                Vector((getOrientation().ordinal()+1)%2,
                getOrientation().ordinal()%2)));
    }

    @Override
    public boolean takeCellSpace() {
        return isNotTraversable();
    }

    @Override
    public boolean isCellInteractable() {
        return true;
    }

    @Override
    public boolean isViewInteractable() {
        return true;
    }
    public void setNotTraversable(boolean isNotTraversable) {
        this.isNotTraversable = isNotTraversable;
    }
    public boolean isNotTraversable() {
        return isNotTraversable;
    }
    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICMazeInteractionVisitor) v).interactWith(this, isCellInteraction);
    }

    @Override
    public void update(float deltaTime){
        switch (this.getState()) {
            case OPEN:
                setNotTraversable(false); // Traversable si ouvert
                break;
            case LOCKED:
                setNotTraversable(true); // Pas traversable si invisible
                portal = new Sprite("icmaze/chained_wood_"+ this.getOrientation().ordinal(),
                        (this.getOrientation().ordinal()+1)%2+1, this.getOrientation().ordinal()%2+1,
                        this);
                break;
            case INVISIBLE:
                setNotTraversable(true); // Pas traversable si invisible
                portal = new Sprite("icmaze/invisibleDoor_"+this.getOrientation().ordinal(),
                        (this.getOrientation().ordinal()+1)%2+1,
                        this.getOrientation().ordinal()%2+1, this);
                break;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        // Garantir que rien ne s'affiche si le portail est ouvert
        if (PortalState.INVISIBLE.equals(getState()) ||  PortalState.LOCKED.equals(getState())) {
            portal.draw(canvas);
        }
    }
}