package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.Size;
import ch.epfl.cs107.icmaze.actor.collectable.Heart;
import ch.epfl.cs107.icmaze.actor.collectable.Key;
import ch.epfl.cs107.icmaze.actor.collectable.Pickaxe;
import ch.epfl.cs107.icmaze.actor.collectable.rare.Crystal;
import ch.epfl.cs107.icmaze.actor.collectable.rare.Treasure;
import ch.epfl.cs107.icmaze.actor.enemies.Wizard;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.icmaze.handler.DialogHandler;      // ✅ AJOUT
import ch.epfl.cs107.play.engine.actor.Dialog;          // ✅ AJOUT
import ch.epfl.cs107.play.math.DiscreteCoordinates;

/**
 * Spawn area of the game.
 * This area introduces the player to basic collectables and displays a welcome dialog.
 */
public class Spawn extends ICMazeArea {
    private final static String NAME = "SmallArea";

    private final DialogHandler dialogHandler;
    private boolean welcomeShown = false;

    private boolean showTreasure;


    /// collectables
    private Treasure treasure;
    private Heart heart;
    private Pickaxe pickaxe;
    private Crystal crystal;
    private Key key;

    /// Initial positions of entities on the map
    private static final DiscreteCoordinates HEART_POSITION =  new DiscreteCoordinates(4,5);
    private static final DiscreteCoordinates PICKAXE_POSITION =  new DiscreteCoordinates(5,4);
    private static final DiscreteCoordinates KEY_POSITION = new DiscreteCoordinates(6, 5);
    private static final DiscreteCoordinates TREASURE_POSITION = new DiscreteCoordinates(2, 4);
    private static final DiscreteCoordinates CRYSTAL_POSITION = new DiscreteCoordinates(3, 4);

    /**
     * Creates the spawn area.
     * @param dialogHandler handler used to display dialogs
     */
    public Spawn(DialogHandler dialogHandler) {
        super(NAME, Size.SMALL);
        this.dialogHandler = dialogHandler;

        showTreasure = false;
        treasure = new Treasure(this, TREASURE_POSITION);
        crystal = new Crystal(this, CRYSTAL_POSITION);
        pickaxe = new Pickaxe(this, PICKAXE_POSITION);
        heart = new Heart(this, HEART_POSITION);
        key = new Key(this, KEY_POSITION, Integer.MAX_VALUE);
    }

    /**
     * Registers initial collectable entities in the area.
     */
    private void onStart(){
        this.registerActor(pickaxe);

        this.registerActor(heart);

        this.registerActor(key);
    }
    /**
     * Updates the spawn area.
     * Displays the welcome dialog and spawns the treasure when activated.
     *
     * @param deltaTime elapsed time since last update
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if (!welcomeShown) {
            onStart();
            welcomeShown = true;
            registerActor(crystal);
            if (dialogHandler != null) {
                dialogHandler.publish(new Dialog("welcome"));
            }
        }

        if (isOn() && !showTreasure) {
            showTreasure = true;
            enterAreaCells(treasure, treasure.getCurrentCells());
            registerActor(treasure);
        }
    }

    /**
     * Returns the title of the spawn area.
     * @return the area title
     */
    @Override
    public String getTitle() {
        return "icmaze/Spawn";
    }
}
