package ch.epfl.cs107.icmaze;

import java.util.Random;

/**
 * Pseudo-random generator used for various aspects of the game
 */
public class RandomGenerator {
    // randomly selects seed (c.f. https://docs.oracle.com/javase/8/docs/api/java/util/Random.html#Random--)
    public static Random rng = new Random();


    /**
     * Returns a random boolean value
     * @return a random boolean
     */
    public static boolean nextBoolean() {
        return rng.nextBoolean();  // Simple wrapper
    }

    /**
     * Returns a random integer in [0, bound).
     * @param bound the upper bound (exclusive)
     * @return a random integer
     */
    public static int nextInt(int bound) {
        return rng.nextInt(bound);  // Tu auras besoin de ça aussi
    }
}
