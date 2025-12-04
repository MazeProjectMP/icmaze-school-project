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
    private Canvas canvas;

    private DiscreteCoordinates coordinates;

    private Area area;
    private Orientation orientation;
    private DiscreteCoordinates position;

    public Rock(DiscreteCoordinates coordinates, Area area, DiscreteCoordinates position) {
        super(area,  Orientation.UP, position);
        this.coordinates = coordinates ;
        sprite = new Sprite("rock.2", 1, 1 , this );

    }
    @Override
    // Dessine le sprite du rocher

    public void draw(Canvas canva){
        sprite.draw(canva);

    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());

    }

    @Override
    public boolean takeCellSpace() {
        //le rocher bloque sa cellule
        return true ;
    }

    @Override
    public boolean isCellInteractable() {
        //pas dinteraction avec le rocher
        return false;
    }

    @Override
    public boolean isViewInteractable() {
        //on ne peut pas avoir d'interaction a distance
        return false;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
//naccepte pas d'interactions par défaut
    }
}
