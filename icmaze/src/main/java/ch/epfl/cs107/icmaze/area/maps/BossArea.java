package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.actor.collectable.Key;
import ch.epfl.cs107.icmaze.actor.enemies.Boss;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

public class BossArea extends ICMazeArea {
    private static final String name = "SmallArea";
    private static final int sideSize = 8;
    /**
     * Set le nom du ICMazeBehavior de BossArea
     */
    public BossArea(){
        super(name, sideSize);
    }

    @Override
    public void createArea() {
        super.createArea();

        // Ajoute le Boss
        Boss boss = new Boss(this, Orientation.LEFT, new DiscreteCoordinates(5,5), 10);
        registerActor(boss);
    }

    /**
     * @return the player's spawn position in the area
     */
    @Override
    public DiscreteCoordinates getPlayerSpawnPosition() {
        return new DiscreteCoordinates(0, 0);
    }


    @Override
    public String getTitle() {
        return "icmaze/Boss";
    }
}
