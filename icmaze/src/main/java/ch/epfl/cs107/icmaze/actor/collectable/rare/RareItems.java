package ch.epfl.cs107.icmaze.actor.collectable.rare;

import ch.epfl.cs107.play.areagame.actor.CollectableAreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

import java.util.List;

public abstract class RareItems extends CollectableAreaEntity {
    private int value;

    public RareItems(Area area, Orientation orientation, DiscreteCoordinates position, int value) {
        super(area, orientation, position);
    }

    public int getValue() {
        return value;
    }
    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return List.of();
    }

    @Override
    public boolean takeCellSpace() {
        return false;
    }

    @Override
    public boolean isCellInteractable() {
        return false;
    }

    @Override
    public boolean isViewInteractable() {
        return false;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {

    }
}
