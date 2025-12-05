package ch.epfl.cs107.icmaze.area;

import ch.epfl.cs107.icmaze.Difficulty;
import ch.epfl.cs107.icmaze.MazeGenerator;
import ch.epfl.cs107.icmaze.actor.ICMazePlayer;
import ch.epfl.cs107.icmaze.actor.Portal;
import ch.epfl.cs107.icmaze.actor.Rock;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

import java.util.List;

import static jdk.internal.jimage.decompressor.CompressedResourceHeader.getSize;

public class LargeArea extends ICMazeArea {
    private AreaPortals areaPortalEntry;
    private AreaPortals areaPortalExit;
    private int difficulty;
    private Portal portal;

    public LargeArea(AreaPortals areaPortalEntry, AreaPortals areaPortalExit, int difficulty) {
        super("LargeArea", 32);

        this.areaPortalEntry = areaPortalEntry;
        this.areaPortalExit = areaPortalExit;
        this.difficulty = difficulty;


    }

    public LargeArea() {
        super("LargeArea", 32);

    }

    @Override
    public DiscreteCoordinates getPlayerSpawnPosition() {
        return null;
    }

    @Override
    public void lockPortalWithKey(AreaPortals areaPortals, int maxValue) {

    }

    @Override
    public void createArea() {
//        ICMazePlayer player = ;

//        DiscreteCoordinates posJoueur = new DiscreteCoordinates(1, 1);

        int[][] maze = MazeGenerator.createMaze(getSize(), getSize(), difficulty);
        
        DiscreteCoordinates entry =  portal.getCurrentMainCellCoordinates(); // entrée de la map
        DiscreteCoordinates exit = portal.getDestinationCoordinates();

//        Rock premiereRock = new Rock(maze[1][1],this,maze[1][1] )
        for (int y = 0; y < maze.length; y++) {               // y = ligne
                 for (int x = 0; x < maze[0].length; x++) {
                         DiscreteCoordinates pos = new DiscreteCoordinates(x, y);

                         if (pos!= entry && pos!=exit && maze[x][y]==1 ) {
                             Rock rock =new Rock(this, pos);
                             registerActor(rock);
                         }

                 }

        }
    }

    @Override
    protected void registerTheme() {
    }
//
//    @Override
//    public AreaPortals getEntryPortal() {
//        return null;
//    }

        @Override
        public String getTitle () {
            return "";
    }
}