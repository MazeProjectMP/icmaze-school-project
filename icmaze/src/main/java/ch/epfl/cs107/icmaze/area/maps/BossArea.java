package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class BossArea extends ICMazeArea {
    private static final String name = "SmallArea";
    private static final int sideSize = 8;
    /**
     * Set le nom du ICMazeBehavior de BossArea
     */
    public BossArea(){
        super(name, sideSize);
    }
    /**
     * @return the player's spawn position in the area
     */
    @Override
    public DiscreteCoordinates getPlayerSpawnPosition() {
        return new DiscreteCoordinates(0, 0);
    }

    @Override
    protected void registerTheme() {
        registerActor(new Background(this, name)); // A verifier
        registerActor(new Foreground(this, null, name)); // A verifier
    }

    @Override
    public String getTitle() {
        return "icmaze/Boss";
    }
}
