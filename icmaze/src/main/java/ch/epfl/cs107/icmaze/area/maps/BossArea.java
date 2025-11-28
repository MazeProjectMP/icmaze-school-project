package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class BossArea extends ICMazeArea {
    static final String name = "SmallArea";
    /**
     * Set le nom du ICMazeBehavior de BossArea
     */
    public BossArea(){
        super(name);
    }
    /**
     * @return the player's spawn position in the area
     */
    @Override
    public DiscreteCoordinates getPlayerSpawnPosition() {
        return new DiscreteCoordinates(0, 0);
    }

    @Override
    protected void createArea() {
        registerActor(new Background(this, name)); // A verifier
        registerActor(new Foreground(this, null, name)); // A verifier
    }

    @Override
    public String getTitle() {
        return "icmaze/Boss";
    }
}
