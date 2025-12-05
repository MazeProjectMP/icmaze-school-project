package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.Difficulty;
import ch.epfl.cs107.icmaze.MazeGenerator;
import ch.epfl.cs107.icmaze.actor.Rock;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class SmallArea extends MazeLevels {
    private static final int side = 8;
    public SmallArea(AreaPortals areaPortalEntry, AreaPortals areaPortalExit, int keyID){
        super(areaPortalEntry, areaPortalExit, Difficulty.HARDEST, keyID, "SmallArea", side);
//        GenerateMaze();
    }

//    public DiscreteCoordinates getWestPortalSpawn() {
//        return new DiscreteCoordinates(getSizeSide() + 1, getSizeSide() / 2);
//    }
//    public DiscreteCoordinates getEstPortalSpawn() {
//        return new DiscreteCoordinates(0, getSizeSide() / 2);
//    }
//    public DiscreteCoordinates getNorthPortalSpawn(){
//        return new DiscreteCoordinates(getSizeSide()/2, 0);
//    }
//    public DiscreteCoordinates getSouthPortalSpawn(){
//        return new DiscreteCoordinates(getSizeSide()/2, getSizeSide() + 1);
//    }


//    public void GenerateMaze(){
//        int[][] maze = MazeGenerator.createMaze(side, side, Difficulty.HARDEST);
//        for(int k = 0; k < side; k++){
//            for(int j = 0; j < side; j++){
//                if(maze[k][j] == 1){
//                    Rock rock = new Rock(this, new DiscreteCoordinates(k+1,j+1));
//                    registerActor(rock);
//                }
//            }
//        }
//    }
}
