package ch.epfl.cs107.icmaze.handler;
import ch.epfl.cs107.play.engine.actor.Dialog;

/**
 * Interface for handling dialogs in the game.
 */
public interface DialogHandler {
    /**
     * Publishes a dialog.
     * @param dialog the dialog to display
     */
    void publish(Dialog dialog);
}
