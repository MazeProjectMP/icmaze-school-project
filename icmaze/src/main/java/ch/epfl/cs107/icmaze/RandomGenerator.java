package ch.epfl.cs107.icmaze;

import java.util.Random;

/**
 * Pseudo-random generator used for various aspects of the game
 */
public class RandomGenerator {
    // randomly selects seed (c.f. https://docs.oracle.com/javase/8/docs/api/java/util/Random.html#Random--)
    public static Random rng = new Random();

    public static boolean nextBoolean() {
        return rng.nextBoolean();  // Simple wrapper
    }

    public static int nextInt(int bound) {
        return rng.nextInt(bound);  // Tu auras besoin de ça aussi
    }
}
