package ch.epfl.cs107.icmaze.area;

import ch.epfl.cs107.icmaze.Difficulty;
import ch.epfl.cs107.icmaze.MazeGenerator;
import ch.epfl.cs107.icmaze.actor.ICMazePlayer;
import ch.epfl.cs107.icmaze.actor.Portal;
import ch.epfl.cs107.icmaze.actor.Rock;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

import static jdk.internal.jimage.decompressor.CompressedResourceHeader.getSize;

public class LargeArea extends ICMazeArea{
    private AreaPortals areaPortalEntry;
    private AreaPortals areaPortalExit;
    private int difficulty;
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
        DiscreteCoordinates posJoueur = new DiscreteCoordinates(1,1 );

        int[][] maze = MazeGenerator.createMaze(getSize(), getSize(), difficulty);

        DiscreteCoordinates entry = posJoueur;  // entrée = position du player
        DiscreteCoordinates exit = new DiscreteCoordinates(31,31);
        for (int y = 0; y < maze.length; y++) {               // y = ligne
            for (int x = 0; x < maze[0].length; x++) {
                DiscreteCoordinates pos = new DiscreteCoordinates(x, y);

//                if (pos.equals(entry)) {
//                    // portail d'entrée
//                    Portal entry = new Portal(this, Orientation.UP, pos, getExitCoordinates());
//                    registerActor(entry);
//                } else if (pos.equals(end)) {
//                    // portail de sortie
//                    Portal exit = new Portal(this, Orientation.UP, pos, getEntryCoordinates());
//                    registerActor(exit);
//                } else if (maze[y][x] == 1) {
                    // mur classique
                    Rock rock = new Rock(this, pos);
                    registerActor(rock);
                }


            }

        }
    }
        @Override
        protected void registerTheme() {}
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
