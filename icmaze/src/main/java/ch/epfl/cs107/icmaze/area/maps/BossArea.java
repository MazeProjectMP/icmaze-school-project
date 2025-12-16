package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.actor.collectable.Key;
import ch.epfl.cs107.icmaze.actor.enemies.Boss;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.Logic;

public class BossArea extends ICMazeArea {
    private static final String name = "SmallArea";
    private static final int sideSize = 8;
    Boss boss;
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
        boss = new Boss(this, Orientation.LEFT, new DiscreteCoordinates(5,5), 8);
        registerActor(boss);

        setWinningSignal(new Logic() {
            @Override public boolean isOn() {
                return boss != null
                        && boss.isDead()
                        && boss.droppedKeySignal() != null
                        && boss.droppedKeySignal().isOn(); // collected = true
            }
            @Override public boolean isOff() { return !isOn(); }
        });
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
