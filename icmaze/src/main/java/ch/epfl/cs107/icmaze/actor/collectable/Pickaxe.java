package ch.epfl.cs107.icmaze.actor.collectable;

import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

public class Pickaxe extends Equipment {
    private Sprite pickaxe;
    private boolean isVisible = true;
    public Pickaxe(Area area, DiscreteCoordinates position){
        super(area, position);
        pickaxe = new Sprite("icmaze/pickaxe", .75f, .75f, this); // Initialise l'image de la pickaxe
    }

    @Override
    public void draw(Canvas canvas) {
        if(isVisible) {
            pickaxe.draw(canvas);
        }
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICMazeInteractionVisitor) v).interactWith(this, isCellInteraction);
    }

    public void hide() {
        if (isVisible) {
            isVisible = false;
            getOwnerArea().unregisterActor(this);
        }
    }
}
