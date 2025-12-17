package ch.epfl.cs107.icmaze;

import static ch.epfl.cs107.play.window.Keyboard.E;
import static ch.epfl.cs107.play.window.Keyboard.ENTER;
import static ch.epfl.cs107.play.window.Keyboard.P;
import static ch.epfl.cs107.play.window.Keyboard.R;
import static ch.epfl.cs107.play.window.Keyboard.B;
import static ch.epfl.cs107.play.window.Keyboard.SPACE;
import static ch.epfl.cs107.play.window.Keyboard.UP;
import static ch.epfl.cs107.play.window.Keyboard.DOWN;
import static ch.epfl.cs107.play.window.Keyboard.LEFT;
import static ch.epfl.cs107.play.window.Keyboard.RIGHT;


/**
 * Defines all keyboard bindings used in the game.
 */
public final class KeyBindings {

    /**
     * Key bindings for the player.
     */
    public static final PlayerKeyBindings PLAYER_KEY_BINDINGS = new PlayerKeyBindings(UP, LEFT, DOWN, RIGHT, SPACE, E);
    ///Key used to advance to the next dialog.
    public static final int NEXT_DIALOG = ENTER;
    ///Key used to reset the game.
    public static final int RESET_GAME = R;
    ///Key used to reset the area.
    public static final int PAUSE_GAME = P;
    /// Key used to teleport to the boss room.
    public static final int BOSS_ROOM = B;

    private KeyBindings() {

    }

    /**
     * Key bindings for a player.
     *
     * @param up movement up
     * @param left movement left
     * @param down movement down
     * @param right movement right
     * @param pickaxe use pickaxe
     * @param interact interact with entities
     */
    public record PlayerKeyBindings(int up, int left, int down, int right, int pickaxe, int interact) {
    }
}
