package ch.epfl.cs107.icmaze.actor;

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

    private DiscreteCoordinates destinationCoordinates;
    private String areaName;
    private static int NO_KEY_ID = Integer.MIN_VALUE;
    private int identificator;

    private PortalState state;
    private Sprite portal;

    private boolean isTraversable;


    //peut avoir comportement etat
    //portail ferme bois,  ouvrir passage avec cle

    public Portal(Area area, Orientation orientation, DiscreteCoordinates destinationCoordinates, int identificator) {
        super(area, orientation, destinationCoordinates);
        state = PortalState.INVISIBLE;
        this.areaName = area.getTitle();
        this.destinationCoordinates = destinationCoordinates;
        this.identificator = identificator;
    }

    public Portal(Area area, Orientation orientation, DiscreteCoordinates destinationCoordinates) {
        super(area, orientation, destinationCoordinates);
        state = PortalState.INVISIBLE;
        this.areaName = area.getTitle();
        this.destinationCoordinates = destinationCoordinates;
        this.identificator = NO_KEY_ID;
    }

    public void goTo(String areaName) {
        this.areaName = areaName;
    }


    public enum PortalState {
        //https://stackoverflow.com/questions/25761438/understanding-bufferedimage-getrgb-output-values
        OPEN,
        LOCKED,
        INVISIBLE,
    }
    public PortalState setState(PortalState state) {
        this.state = state;
        return state;

    }
    public void lock(int identificator) {
        this.identificator = identificator;
        state = PortalState.LOCKED;
    }
    public void unlock() {
        state = PortalState.INVISIBLE;
    }
    public void open(int identificator) {
        if  (this.identificator == identificator) {
            state = PortalState.OPEN;
        }
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
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

        return false;
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
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {

    }

    @Override
    public void update(float deltaTime){
        switch (state) {
            case OPEN:
                isTraversable = true; // Traversable si ouvert
                break;
            case LOCKED:
                isTraversable = false;
                portal = new Sprite("icmaze/invisibleDoor_"+this.getOrientation().ordinal(),
                        (this.getOrientation().ordinal()+1)%2+1,
                        this.getOrientation().ordinal()%2+1, this);
                break;
            case INVISIBLE:
                isTraversable = false; // Traversable si ferme
                portal = new Sprite("icmaze/chained_wood_"+ this.getOrientation().ordinal(),
                        (this.getOrientation().ordinal()+1)%2+1, this.getOrientation().ordinal()%2+1,
                        this);
                break;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        portal.draw(canvas);
    }
}