package ch.epfl.cs107.icmaze.actor.collectable.rare;

import ch.epfl.cs107.play.areagame.actor.CollectableAreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

public class Cristal extends RareItems {

    public Cristal(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position, 3);
    }
}
