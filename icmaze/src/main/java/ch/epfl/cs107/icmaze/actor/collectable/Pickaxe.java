package ch.epfl.cs107.icmaze.actor.collectable;

import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

public class Pickaxe extends Equipment {
//    private Sprite = new Sprite("icmaze/pickaxe", .75f, .75f, this);
    public Pickaxe(Area area, DiscreteCoordinates position, Orientation orientation){
        super(area, position, orientation);
    }
}
