package ch.epfl.cs107.icmaze.actor.collectable;

import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

public class Equipment extends CollectableAreaEntity {
    private Orientation orientation; // Orientation de l'equipement
    public Equipment(Area area, DiscreteCoordinates position, Orientation orientation){
        super(area, position);
        this.orientation = orientation;
    }

    @Override
    public void draw(Canvas canvas) {

    }

    @Override
    public void update(float deltaTime) {

    }
}
