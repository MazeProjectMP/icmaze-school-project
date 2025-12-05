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
//    public DiscreteCoordinates getWestPortalSpawn() {
//        return new DiscreteCoordinates(sideSize + 1, sideSize / 2);
//    }
//    public DiscreteCoordinates getEstPortalSpawn() {
//        return new DiscreteCoordinates(0, sideSize / 2);
//    }
//    public DiscreteCoordinates getNorthPortalSpawn(){
//        return new DiscreteCoordinates(sideSize/2, 0);
//    }
//    public DiscreteCoordinates getSouthPortalSpawn(){
//        return new DiscreteCoordinates(sideSize/2, sideSize + 1);
//    }
    @Override
    public DiscreteCoordinates getPlayerSpawnPosition() {
        return new DiscreteCoordinates(0, 0);
    }


    @Override
    public String getTitle() {
        return "icmaze/Boss";
    }
}
