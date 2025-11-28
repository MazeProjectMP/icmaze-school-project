package ch.epfl.cs107.icmaze;

import ch.epfl.cs107.icmaze.actor.ICMazePlayer;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.icmaze.area.maps.BossArea;
import ch.epfl.cs107.icmaze.area.maps.Spawn;
import ch.epfl.cs107.play.areagame.AreaGame;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Window;

public class ICMaze extends AreaGame {
    private final String[] areas = {"icmaze/Spawn", "icmaze/Boss"};
    private int areaIndex;
    private ICMazePlayer player;

    private DiscreteCoordinates initialPostion = new DiscreteCoordinates(5,7);

    /**
     * Add all the IC Maze areas
     */
    private void createAreas() {
        addArea(new Spawn());
        addArea(new BossArea());
    }

    /**
     * @param window (Window): display context. Not null
     * @param fileSystem (FileSystem): given file system. Not null
     * @return true if the game begins properly
     */
    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        if (super.begin(window, fileSystem)) {
            createAreas();
            areaIndex = 0;
            initArea(areas[areaIndex]);
            return true;
        }
        return false;
    }

    /**
     * @param deltaTime elapsed time since last update, in seconds, non-negative
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }

    @Override
    public void end() {

    }

    @Override
    public String getTitle() {
        return "ICMaze";
    }

    /**
     * sets the area named `areaKey` as current area in the game ICMaze
     * @param areaKey (String) title of an area
     */
    private void initArea(String areaKey) {
        ICMazeArea area = (ICMazeArea) setCurrentArea(areaKey, true);
        player = new ICMazePlayer(area, Orientation.UP, initialPostion, "player");
        player.enterArea(area, initialPostion);
        player.centerCamera();
    }
}