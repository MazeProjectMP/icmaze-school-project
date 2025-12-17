package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.actor.collectable.rare.Crystal;
import ch.epfl.cs107.icmaze.actor.collectable.rare.Treasure;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.icmaze.handler.DialogHandler;      // ✅ AJOUT
import ch.epfl.cs107.play.engine.actor.Dialog;          // ✅ AJOUT
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class Spawn extends ICMazeArea {
    private final static String name = "SmallArea";
    private static final int SIDE_SIZE = 8;

    private final DialogHandler dialogHandler;
    private boolean welcomeShown = false;

    private boolean showTreasure;
    private Treasure treasure;
    private Crystal crystal;
    public Spawn(DialogHandler dialogHandler) {
        super(name, SIDE_SIZE);
        this.dialogHandler = dialogHandler;

        showTreasure = false;
        treasure = new Treasure(this, new DiscreteCoordinates(2, 4));
        crystal = new Crystal(this, new DiscreteCoordinates(4,4));
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if (!welcomeShown) {
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

    @Override
    public DiscreteCoordinates getPlayerSpawnPosition() {
        return new DiscreteCoordinates(5, 7);
    }

    @Override
    public String getTitle() {
        return "icmaze/Spawn";
    }
}
