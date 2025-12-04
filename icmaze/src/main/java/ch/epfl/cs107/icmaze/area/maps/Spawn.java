package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.actor.Rock;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Window;

public class Spawn extends ICMazeArea {
    private final static String name = "SmallArea";
    private static final int sideSize = 8;

    /**
     * Set le nom du ICMazeBehavior de Spawn
     */
    public Spawn(){

        super(name, sideSize);
        Rock rock = new Rock(new DiscreteCoordinates(3, 4), this,getPlayerSpawnPosition() );

    }
    /**
     * @return the player's spawn position in the area
     */
    @Override
    public DiscreteCoordinates getPlayerSpawnPosition() {
        return new DiscreteCoordinates(5, 7);
    }

    @Override
    public void lockPortalWithKey(AreaPortals areaPortals, int maxValue) {

    }

    @Override
    protected void registerTheme() {
        registerActor(new Background(this, name));
        registerActor(new Foreground(this, null, name));
    }
    public boolean begin(Window window, FileSystem fileSystem) {
        super.begin(window, fileSystem);
//cree une rock
        Rock rock = new Rock(new DiscreteCoordinates(5, 5), this , getPlayerSpawnPosition());
        registerActor(rock);

        return true;
    }

    @Override
    public String getTitle() {
        return "icmaze/Spawn";
    }
}
