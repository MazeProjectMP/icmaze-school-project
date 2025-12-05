package ch.epfl.cs107.icmaze.area;

import ch.epfl.cs107.icmaze.MazeGenerator;
import ch.epfl.cs107.icmaze.actor.Portal;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.Actor;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Window;

import java.util.HashMap;

public abstract class ICMazeArea extends Area {
    private String title;
    public final static float DEFAULT_SCALE_FACTOR = 11.f;
    private int sizeSide; // Longueur du cote de l'aire

    // Declaration des 4 portails
    private Portal portalWest;
    private Portal portalEast;
    private Portal portalNorth;
    private Portal portalSouth;

    /**
     * Area specific callback to initialise the instance
     */

    /**
     * @return the player's spawn position in the area
     */
    public abstract DiscreteCoordinates getPlayerSpawnPosition();

    public ICMazeArea(String titre, int sizeSide) {
        title = titre;
        this.sizeSide = sizeSide;
        initPortals();
    }
    /**
     * Callback to initialise the instance of the area
     * @param window (Window): display context. Not null
     * @param fileSystem (FileSystem): given file system. Not null
     * @return true if the area is instantiated correctly, false otherwise
     */
    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        if (super.begin(window, fileSystem)) {
            setBehavior(new ICMazeBehavior(window, title));
            createArea();
            return true;
        }
        return false;
    }

    public enum AreaPortals {
        N(Orientation.UP),
        W(Orientation.LEFT),
        S(Orientation.DOWN),
        E(Orientation.RIGHT);
        private final Orientation orientation;
        AreaPortals(Orientation orientation) {
            this.orientation = orientation;
        }
        public Orientation getOrientation() {
            return orientation;
        }
    }

    public void initPortals(){
        portalNorth  = new Portal(this ,  Orientation.UP.opposite(), new DiscreteCoordinates(sizeSide / 2 , sizeSide + 1));
        portalSouth  = new Portal(this ,  Orientation.DOWN.opposite(), new DiscreteCoordinates(sizeSide / 2, 0));
        portalEast = new Portal(this ,  Orientation.RIGHT.opposite(), new DiscreteCoordinates(sizeSide + 1, sizeSide / 2));
        portalWest = new Portal(this ,  Orientation.LEFT.opposite(), new DiscreteCoordinates(0, sizeSide / 2));
    }

    protected void createArea(){
        registerPortals(); // Enregistrer les portails dans l'aire
        registerTheme();
    }

    public int getSizeSide() {
        return sizeSide;
    }

    public void openPortal(AreaPortals portal, String destinationAreaName) {
        switch (portal) {
            case W -> portalWest.unlock(destinationAreaName);
            case E -> portalEast.unlock(destinationAreaName);
            case N -> portalNorth.unlock(destinationAreaName);
            case S -> portalSouth.unlock(destinationAreaName);
        }
    }

    public void lockPortalWithKey(AreaPortals portal, int keyId, String destinationAreaName) {
        switch (portal) {
            case W -> portalWest.lock(keyId, destinationAreaName);
            case E -> portalEast.lock(keyId, destinationAreaName);
            case N -> portalNorth.lock(keyId, destinationAreaName);
            case S -> portalSouth.lock(keyId, destinationAreaName);
        }
    }

    public void setDestinationCoordinate(AreaPortals portal, DiscreteCoordinates destinationCoordinates){
        switch (portal) {
            case W -> portalWest.setDestinationCoordinates(destinationCoordinates);
            case E -> portalEast.setDestinationCoordinates(destinationCoordinates);
            case N -> portalNorth.setDestinationCoordinates(destinationCoordinates);
            case S -> portalSouth.setDestinationCoordinates(destinationCoordinates);
        }
    }



    public DiscreteCoordinates getWestPortalSpawn() {
        return new DiscreteCoordinates(sizeSide, sizeSide / 2);
    }
    public DiscreteCoordinates getEstPortalSpawn() {
        return new DiscreteCoordinates(1, sizeSide / 2);
    }
    public DiscreteCoordinates getNorthPortalSpawn(){
        return new DiscreteCoordinates(sizeSide/2, 1);
    }
    public DiscreteCoordinates getSouthPortalSpawn() {
        return new DiscreteCoordinates(sizeSide / 2, sizeSide);
    }

    private void registerTheme() {
        registerActor(new Background(this, title));
    }

    private void registerPortals(){
        this.registerActor(portalWest);
        this.registerActor(portalEast);
        this.registerActor(portalNorth);
        this.registerActor(portalSouth);
    }


    /**
     * Getter for ICMazeArea's scale factor
     * @return Scale factor in both the x-direction and the y-direction
     */
    public float getCameraScaleFactor(){
        return (float) Math.min(sizeSide * 1.375f, 30);
    }
}
