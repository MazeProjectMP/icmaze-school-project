package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.actor.enemies.Boss;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.Logic;
/**
 * Area containing the boss of the maze.
 * The area is considered completed when the boss is defeated
 * and the key dropped by the boss has been collected.
 */

public class BossArea extends ICMazeArea {
    private static final String NAME = "SmallArea";
    private static final int SIDE_SIZE = 8;
    Boss boss;

    /**
     * Creates the boss area with a fixed size.
     */
    public BossArea(){
        super(NAME, SIDE_SIZE);
    }

    @Override
    public void createArea() {
        super.createArea();

        /// Spawns the Boss
        boss = new Boss(this, Orientation.LEFT, new DiscreteCoordinates(5,5), 8);
        registerActor(boss);

        /// Winning condition: boss defeated and key collected
        setWinningSignal(new Logic() {

            /**
             * Indicates whether the winning condition is satisfied.
             * @return (boolean) {@code true} if the boss is dead and its key has been collected
             */
            @Override public boolean isOn() {
                return boss != null
                        && boss.isDead()
                        && boss.droppedKeySignal() != null
                        && boss.droppedKeySignal().isOn(); /// collected = true
            }
            /**
             * Winning signal of the boss area.
             * The signal is ON when
             *   the boss exists
             *   the boss is dead
             *   the dropped key exists and has been collected
             */
            @Override public boolean isOff() { return !isOn(); }
        });
    }

    /**
     * Gets the title of the area.
     * @return (String) the area title identifier
     */
    @Override
    public String getTitle() {
        return "icmaze/Boss";
    }
}
