package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.play.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;

public class Rock extends AreaEntity {
    private Sprite sprite ;


    public Rock(Area area, DiscreteCoordinates position) {
        super(area, Orientation.LEFT, position);
        sprite = new Sprite("rock.2", 1, 1, this);
    }

    @Override
    public void draw(Canvas canva){
        sprite.draw(canva);
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }


    @Override
    public boolean takeCellSpace() {
        return true ;  //le rocher bloque sa cellule
    }

    @Override
    public boolean isCellInteractable() {
        return false;  //pas dinteraction avec le rocher
    }

    @Override
    public boolean isViewInteractable() {
        return false;  //on ne peut pas avoir d'interaction a distance
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {}  //n'accepte pas d'interactions par défaut

}
