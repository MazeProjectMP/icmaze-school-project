package ch.epfl.cs107.icmaze.actor.rocks;

import ch.epfl.cs107.icmaze.RandomGenerator;
import ch.epfl.cs107.icmaze.actor.collectable.Heart;
import ch.epfl.cs107.icmaze.actor.collectable.rare.Crystal;
import ch.epfl.cs107.icmaze.area.maps.MazeLevels;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class CrystalRock extends Rock {
    public CrystalRock(Area area, DiscreteCoordinates position) {
        super(area, position, 5, "icmaze/red_crystal_rock");
    }

    @Override
    void lootSpawn() {
        Crystal crystal = new Crystal(getOwnerArea(), getCurrentMainCellCoordinates());
        getOwnerArea().registerActor(crystal);
        ((MazeLevels) getOwnerArea()).addWalkableCellToGraph(getCurrentMainCellCoordinates());
        getOwnerArea().unregisterActor(this);
    }
}
