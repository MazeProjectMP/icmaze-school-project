package ch.epfl.cs107.icmaze.actor.enemies;

import ch.epfl.cs107.icmaze.RandomGenerator;
import ch.epfl.cs107.icmaze.actor.ICMazePlayer;
import ch.epfl.cs107.icmaze.actor.collectable.Heart;
import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.actor.MovableAreaEntity;
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

public abstract class Enemy extends MovableAreaEntity implements Interactable, Interactor {

    // Points de vies de l'enemy
    private final int maxHP;
    private int currentHP;
    private boolean isDead;

    private Sprite sprite;

    // Animation de disparition
    private Animation vanishAnimation = new Animation("icmaze/vanish", 7, 2, 2, this,
            32, 32, new Vector(-0.5f, 0.0f),
            24/7, false);;
    private boolean isVanishing = false;

    /**
     * Default AreaEntity constructor
     *
     * @param area        (Area): Owner area. Not null
     * @param orientation (Orientation): Initial orientation of the entity in the Area. Not null
     * @param position    (DiscreteCoordinate): Initial position of the entity in the Area. Not null
     */
    public Enemy(Area area, Orientation orientation, DiscreteCoordinates position, int maxHP) {
        super(area, orientation, position);
        this.maxHP = maxHP;
        this.currentHP = maxHP;
        this.isDead = false;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if (isVanishing) {
            vanishAnimation.update(deltaTime);

            if (vanishAnimation.isCompleted()) {
                getOwnerArea().unregisterActor(this);
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if(isVanishing()) {
            vanishAnimation.draw(canvas);
        }

    }
    // Prend des degats
    public void loseHP(int HP) {
        this.currentHP -= HP;
        if (currentHP <= 0) {
            disappear(); // Disparait a sa mort
        }
    }

    public boolean isDead() {
        return this.isDead;
    }

    private void disappear(){
        isDead = true;
        isVanishing = true;
    }

    public boolean isVanishing(){
        return isVanishing;
    }

    public Animation vanishAnimation(){
        return vanishAnimation;
    }

    @Override
    public boolean takeCellSpace() {
        return !isDead();
    }

    @Override
    public boolean isCellInteractable() {
        return false;
    } // Pas d'interaction de surface

    @Override
    public boolean isViewInteractable() {
        return true;
    } // Interaction a distance

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICMazeInteractionVisitor) v).interactWith(this, isCellInteraction);
    }

}
