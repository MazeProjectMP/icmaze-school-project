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

    public static int[][] createMaze(int width, int height, int difficulty) {

        int [][] maze = new int [height][width]; // Grille vide

        // Matrice a 0
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                maze[i][j] = 0;
            }
        }

        // Division recursive
        divide(maze, 0, 0, width, height, difficulty);

        return maze ;
    }

    private static void divide(int[][] maze, int x, int y, int width, int height, int difficulty) {

        // Si la region est trop petite
        if (width <= difficulty || height <= difficulty) {
            return;
        }

        // Choisir l'orientation du mur
        boolean chooseHorizontal;
        if (width > height) {
            chooseHorizontal = false; // La largeur est plus grande donc mur vertical
        } else if (height > width) {
            chooseHorizontal = true; // La longueur est plus grande donc mur horizontal
        } else {
            chooseHorizontal = RandomGenerator.nextBoolean(); // Choix aleatoire d'orientation
        }

        if (chooseHorizontal) {
            int wallHorizontal = y + 1 + 2 * random.nextInt((height - 1) / 2); // Position impair du mur
            int passageHorizontal = x + 2 * random.nextInt(width / 2); // Positon paire pour l'ouverture

            // Dessine le mur horizontal
            for (int i = x; i < x + width; i++) {
                if (i != passageHorizontal){
                    maze[wallHorizontal][i] = WALL; // Mettre un mur
                }else{
                    maze[wallHorizontal][i] = 0; // Passage
                }
            }

            // Reccursion de la division
            divide(maze, x, y, width, wallHorizontal - y, difficulty);
            divide(maze, x, wallHorizontal + 1, width, y + height - wallHorizontal - 1, difficulty);
        }
        else {
            int wallVertical = x + 1 + 2 * random.nextInt((width - 1) / 2); // Position impair du mur
            int passageVertical = y + 2 * random.nextInt(height / 2); // Position pair

            // Dessine le mur vertical
            for (int i = y; i < y + height; i++) {
                if (i != passageVertical){
                    maze[i][wallVertical] = WALL; // Mettre un mur
                }else{
                    maze[i][wallVertical] = 0; // Passage
                }
            }

            // Reccursion de la division
            divide(maze, x, y, wallVertical - x, height, difficulty);
            divide(maze, wallVertical + 1, y, x + width - wallVertical - 1, height, difficulty);
        }
    }

    /**
     * Print the maze
     */
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
        return 1 + 2 * random.nextInt((max + 1) / 2);
    }

    public static float getCameraScaleFactor(int size){
        return (float) Math.min(size * 1.375f, 30);
    }
    /**
     * Returns a random even number in [0, max] (assuming max >= 0).
     */
    private static int randomEven(int max) {
        return 2 * random.nextInt((max + 1) / 2);
    }


}

