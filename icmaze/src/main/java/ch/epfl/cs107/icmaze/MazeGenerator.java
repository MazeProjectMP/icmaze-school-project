package ch.epfl.cs107.icmaze;

import java.util.Random;

import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

/**
 * Utility class for generating rectangular mazes using the recursive division algorithm.
 * Provides additional helpers to ensure solvability and visualize the maze.
 */
public final class MazeGenerator extends Area {
    private static final int WALL = 1;
    private static final int EMPTY = 0;
    private static final Random random = RandomGenerator.rng;

    private MazeGenerator() {}

    /**
     * Print the maze
     */
    /**
     * Print the maze
     */
   static boolean horizontal ;// pour avoir un mur horrizontal sinon mur vertical
// creates an empty maze
    public static int[][] createMaze(int width, int height, int difficulty) {

        int [][] maze = new int [height][width];

    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            maze[y][x] = EMPTY;
        }
    }

    //creates the walls that surround the maze

        for (int x = 0; x < width; x++) {
            maze[0][x] = WALL;
            maze[height - 1][x] = WALL;
        }
        for (int y = 0; y < height; y++) {
            maze[y][0] = WALL;
            maze[y][width - 1] = WALL;
        }

        // Apply recursive division
        divide(maze, 1, 1, width - 2, height - 2, difficulty);

        return maze ;
    }

    private static void divide(int[][] grid, int x, int y, int width, int height, int difficulty) {

            // case base
         if (width <= difficulty || height <= difficulty) {
                    return;
         }

        //choose wall orientation algorithm

        if (width<height) {
             horizontal = true;}
        else if (height<width) {
            horizontal = false;
        }
        else {
            horizontal = random.nextBoolean();
        }

        if (horizontal) {
            int wallY = y + 1 + 2 * random.nextInt((height - 1) / 2); // odd

            // dessine le mur
            for (int j = x; j < j + width; j++) {
                grid[wallY][j] = WALL;
            }

            // creation de passage aux positions paires
            int passageX = x + 2 * random.nextInt(width / 2);
            grid[wallY][passageX] = EMPTY;

            // recurence sur les positions dessus et dessous
            divide(grid, x, y, width, wallY - y, difficulty);
            divide(grid, x, wallY + 1, width, y + height - wallY - 1, difficulty);

        }

        else {
             int wallX = x + 1 + 2 * random.nextInt((width - 1) / 2);

             // poistionne le mur vertical
             for (int i = y; i < y + height; i++) {
                 grid[i][wallX] = WALL;
             }

             // creation de passage a une pos paire
             int passageY = y + 2 * random.nextInt(height / 2);
             grid[passageY][wallX] = EMPTY;

             //Recursivite sur les cotés
             divide(grid, x, y, wallX - x, height, difficulty);
             divide(grid, wallX + 1, y, x + width - wallX - 1, height, difficulty);
         }




}
    public static void printMaze(int[][] grid, DiscreteCoordinates start, DiscreteCoordinates end) {
        int height = grid.length;
        int width = grid[0].length;

        // Print top border
        System.out.print("┌");
        for (int i = 0; i < width; i++) {
            System.out.print("───");
        }
        System.out.println("┐");

        // Print maze rows
        for (int y = 0; y < height; y++) {
            System.out.print("│");
            for (int x = 0; x < width; x++) {
                if (x == start.x && y == start.y) System.out.print(" S ");
                else if (x == end.x && y == end.y) System.out.print(" E ");
                else System.out.print(grid[y][x] == WALL ? "███" : "   ");
            }
            System.out.println("│");
        }

        // Print bottom border
        System.out.print("└");
        for (int i = 0; i < width; i++) {
            System.out.print("───");
        }
        System.out.println("┘");
    }

    /**
     * Returns a random odd number in [1, max] (assuming max > 0).
     */
    private static int randomOdd(int max) {
        return 1 + 2 * MazeGenerator.random.nextInt((max + 1) / 2);
    }

    /**
     * Returns a random even number in [0, max] (assuming max >= 0).
     */
    private static int randomEven(int max) {
        return 2 * MazeGenerator.random.nextInt((max + 1) / 2);

}

    @Override
    public float getCameraScaleFactor() {
        return (float) Math.min(size * DYNAMIC_SCALE_MULTIPLIER ,
                MAXIMUM_SCALE);
    }

    @Override
    public String getTitle() {
        return "";
    }
}

