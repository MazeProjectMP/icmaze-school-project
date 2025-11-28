package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class Spawn extends ICMazeArea {
    final static String name = "SmallArea";
    /**
     * Set le nom du ICMazeBehavior de Spawn
     */
    public Spawn(){
        super(name);
    }
    /**
     * @return the player's spawn position in the area
     */
    @Override
    public DiscreteCoordinates getPlayerSpawnPosition() {
        return new DiscreteCoordinates(5, 7);
    }

    @Override
    protected void createArea() {
        registerActor(new Background(this, name));
        registerActor(new Foreground(this, null, name));
    }

    @Override
    public String getTitle() {
        return "icmaze/Spawn";
    }
}
