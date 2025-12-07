package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.icmaze.RandomGenerator;
import ch.epfl.cs107.icmaze.actor.collectable.Heart;
import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;

public class Rock extends AreaEntity {
    private Sprite sprite ;
    private boolean isBroken;

    // Points de vies
    private final int maxHP;
    private int currentHP;

    // Animation de disparition
    private Animation vanishAnimation = new Animation("icmaze/vanish", 7, 2, 2, this,
            32, 32, new Vector(-0.5f, 0.0f),
            24/7, false);;
    private boolean isVanishing = false;

    public Rock(Area area, DiscreteCoordinates position) {
        super(area, Orientation.LEFT, position);
        sprite = new Sprite("rock.2", 1, 1, this);

        // Points de vies
        maxHP = 3;
        currentHP = maxHP;
    }

    @Override
    public void draw(Canvas canva){
        if(isVanishing){
            vanishAnimation.draw(canva);
        }else {
            sprite.draw(canva);
        }
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if (isVanishing) {
            vanishAnimation.update(deltaTime);

            if (vanishAnimation.isCompleted()) {
                // 50 % de chance de faire spawn un coeur
                if (RandomGenerator.rng.nextDouble() < 0.5) {
                    Heart heart = new Heart(getOwnerArea(), getCurrentMainCellCoordinates());
                    getOwnerArea().registerActor(heart);
                }

                getOwnerArea().unregisterActor(this);
            }
        }
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }


    @Override
    public boolean takeCellSpace() {
        return !isBroken && !isVanishing;
    }

    @Override
    public boolean isCellInteractable() {
        return false;  // Pas dinteraction avec le rocher
    }

    @Override
    public boolean isViewInteractable() {
        return true;  // Peut se faire casser
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICMazeInteractionVisitor) v).interactWith(this, isCellInteraction);
    }

    public void attackWithPickaxe() {
        if (!isBroken) {
            currentHP -= 1;

            if (currentHP <= 0) {
                isBroken = true;
                isVanishing = true;
            }
        }
    }
    public boolean isBroken() {
        return isBroken;
    }
}
