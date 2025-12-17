package ch.epfl.cs107.icmaze;

import java.util.Random;

import ch.epfl.cs107.play.math.DiscreteCoordinates;

/**
 * Utility class for generating rectangular mazes using the recursive division algorithm.
 * Provides additional helpers to ensure solvability and visualize the maze.
 */
public final class MazeGenerator {
    private static final int WALL = 1;
    private static final Random random = RandomGenerator.rng;

    private MazeGenerator(){}

    /**
     * Generates a maze grid using recursive division.
     * @param width maze width
     * @param height maze height
     * @param difficulty minimum sub-room size (smaller means harder/denser maze)
     * @return a maze grid (1 = wall, 0 = path)
     */
    public static int[][] createMaze(int width, int height, int difficulty) {

        int [][] maze = new int [height][width]; /// Empty grid

        /// Empty matrix
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                maze[i][j] = 0;
            }
        }

        /// Recursive division
        divide(maze, 0, 0, width, height, difficulty);

        return maze ;
    }

    /**
     * Recursively divides the current rectangle by drawing a wall with one passage.
     * @param maze the maze grid to modify
     * @param x left coordinate of the rectangle
     * @param y top coordinate of the rectangle
     * @param width rectangle width
     * @param height rectangle height
     * @param difficulty minimum sub-room size to stop dividing
     */
    private static void divide(int[][] maze, int x, int y, int width, int height, int difficulty) {

        /// If area is too small
        if (width <= difficulty || height <= difficulty) {
            return;
        }

         ///Choosing wall orientation
        boolean chooseHorizontal;
        if (width > height) {
            chooseHorizontal = false; ///Width is greater than height so we cannot create a horizontal wall
        } else if (height > width) {
            chooseHorizontal = true; ///Height is greater than width so we create a horizontal wall
        } else {
            chooseHorizontal = RandomGenerator.nextBoolean(); ///Random choice of orientation
        }

        if (chooseHorizontal) {
            int wallHorizontal = y + 1 + 2 * random.nextInt((height - 1) / 2); /// Odd position of the wall
            int passageHorizontal = x + 2 * random.nextInt(width / 2); /// Even position for opening

            /// Draws horizantal wall
            for (int i = x; i < x + width; i++) {
                if (i != passageHorizontal){
                    maze[wallHorizontal][i] = WALL; ///Placing a wall
                }else{
                    maze[wallHorizontal][i] = 0; /// Path
                }
            }
            ///Recursion of division
            divide(maze, x, y, width, wallHorizontal - y, difficulty);
            divide(maze, x, wallHorizontal + 1, width, y + height - wallHorizontal - 1, difficulty);
        }
        else {
            int wallVertical = x + 1 + 2 * random.nextInt((width - 1) / 2);
            int passageVertical = y + 2 * random.nextInt(height / 2);


            for (int i = y; i < y + height; i++) {
                if (i != passageVertical){
                    maze[i][wallVertical] = WALL;
                }else{
                    maze[i][wallVertical] = 0; ///Path
                }
            }

            divide(maze, x, y, wallVertical - x, height, difficulty);
            divide(maze, wallVertical + 1, y, x + width - wallVertical - 1, height, difficulty);
        }
    }

    /**
     * Prints the maze to the console, marking a start and an end cell.
     * @param grid the maze grid (1 = wall, 0 = path)
     * @param start start position
     * @param end end position
     */
    public static void printMaze(int[][] grid, DiscreteCoordinates start, DiscreteCoordinates end) {
        int height = grid.length;
        int width = grid[0].length;

        /// Print top border

        System.out.print("┌");
        for (int i = 0; i < width; i++) {
            System.out.print("───");
        }
        System.out.println("┐");

        /// Print maze rows
        for (int y = 0; y < height; y++) {
            System.out.print("│");
            for (int x = 0; x < width; x++) {
                if (x == start.x && y == start.y) System.out.print(" S ");
                else if (x == end.x && y == end.y) System.out.print(" E ");
                else System.out.print(grid[y][x] == WALL ? "███" : "   ");
            }
            System.out.println("│");
        }

        /// Print bottom border

        System.out.print("└");
        for (int i = 0; i < width; i++) {
            System.out.print("───");
        }
        System.out.println("┘");
    }

    /**
     * Returns a random odd number in [1, max] (max > 0).
     * @param max upper bound
     * @return a random odd number
     */
    private static int randomOdd(int max) {
        return 1 + 2 * random.nextInt((max + 1) / 2);
    }

    /**
     * Returns a camera scale factor based on the given size.
     * @param size area size
     * @return the camera scale factor
     */
    public static float getCameraScaleFactor(int size){
        return (float) Math.min(size * 1.375f, 30);
    }

    /**
     * Returns a random even number in [0, max] (max >= 0).
     * @param max upper bound
     * @return a random even number
     */
    private static int randomEven(int max) {
        return 2 * random.nextInt((max + 1) / 2);
    }


}

