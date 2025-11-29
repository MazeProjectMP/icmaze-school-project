package ch.epfl.cs107.icmaze.actor.collectable;

import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

public class Pickaxe extends Equipment {
    private final Sprite pickaxe;
    public Pickaxe(Area area, DiscreteCoordinates position, Orientation orientation){
        super(area, position, orientation);
        pickaxe = new Sprite("icmaze/pickaxe", .75f, .75f, this); // Initialise l'image de la pickaxe
    }

    @Override
    public void draw(Canvas canvas) {
        pickaxe.draw(canvas);
    }

}
