package ch.epfl.cs107.icmaze.actor.rocks;

import ch.epfl.cs107.icmaze.actor.collectable.rare.GoldCoin;
import ch.epfl.cs107.icmaze.area.maps.MazeLevels;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class GoldenRock extends Rock{
    public GoldenRock(Area area, DiscreteCoordinates position) {
        super(area, position, 10, "icmaze/yellow_crystal_rock");
    }

    @Override
    void lootSpawn() {
        GoldCoin goldCoin = new GoldCoin(getOwnerArea(), getCurrentMainCellCoordinates());
        getOwnerArea().registerActor(goldCoin);
        ((MazeLevels) getOwnerArea()).addWalkableCellToGraph(getCurrentMainCellCoordinates());
        getOwnerArea().unregisterActor(this);
    }
}
