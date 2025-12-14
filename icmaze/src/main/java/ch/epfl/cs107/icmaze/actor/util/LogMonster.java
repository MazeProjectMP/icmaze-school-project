package ch.epfl.cs107.icmaze.actor.util;

import ch.epfl.cs107.icmaze.actor.Health;
import ch.epfl.cs107.icmaze.actor.ICMazePlayer;
import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.actor.MovableAreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.math.*;
import ch.epfl.cs107.play.math.shape.Polygon;
import ch.epfl.cs107.play.window.Canvas;

import java.awt.*;
import java.util.Collections;
import java.util.List;

public class LogMonster extends MovableAreaEntity implements Interactor, Interactable {

    // health interaction
    private Health health;
    private final int MAX_LIFE = 3;
    Cooldown immunityCooldown ;
    private boolean isImmune = false;
    private final int DAMAGE_AMOUNT = 1; // Dégâts infligés au monstre
    private int damageMonster = 0 ;

    ///  animation du monstre
    private Animation monsterAnimation;
    private ICMazePlayer player;

    public LogMonster(Area owner, Orientation orientation, DiscreteCoordinates coordinates) {
        super(owner, orientation, coordinates);
        health = new Health(this, Transform.I.translated(-0.5, 1.75f), MAX_LIFE, false);

//        this.DAMAGE_AMOUNT = pointsDiminish;
        immunityCooldown = new Cooldown(24f);

    }

    @Override
    public void update(float deltaTime) {
        // Gestion de l'immunité
        if (isImmune) {
            if (immunityCooldown.ready(deltaTime)) {//
                isImmune = false;
            }
        }

        // Mettre à jour l'animation
        if (monsterAnimation != null) {
            monsterAnimation.update(deltaTime);
        }

        super.update(deltaTime);
    }

    @Override
    public void draw(Canvas canvas) {
        if (monsterAnimation != null || (System.currentTimeMillis() / 250) % 2 == 0) {
            monsterAnimation.draw(canvas);
            health.draw(canvas);
        }

    }

    // Méthode pour prendre des dégâts
    public void takeDamage(int damage) {
        if (!isImmune && health.isOn()) {
            health.decrease(damage);
            isImmune = true;
            immunityCooldown.reset(); //reintialise le timer
            // Si le monstre meurt
            if (health.isOff()) {
                getOwnerArea().unregisterActor(this);
            }
        }
    }

    // Handler d'interactions
    private class LogMonsterInteractionHandler implements ICMazeInteractionVisitor {
        @Override
        public void interactWith(ICMazePlayer player, boolean isCellInteraction) {
             if (isCellInteraction && !isImmune) {
                    player.takeDamage(DAMAGE_AMOUNT);
             }
        }

    }

    private final LogMonsterInteractionHandler handler = new LogMonsterInteractionHandler();

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICMazeInteractionVisitor) v).interactWith(this, isCellInteraction);
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    @Override
    public boolean takeCellSpace() {
        return true;
    }

    @Override
    public boolean isCellInteractable() {
        return true;
    }

    @Override
    public boolean isViewInteractable() {
        return false;
    }



    // Méthodes d'Interactor (si le monstre peut interagir)
    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        // Retourner les cellules adjacentes pour détecter le joueur
        return Collections.singletonList(
                getCurrentMainCellCoordinates().jump(getOrientation().toVector())
        );
    }

    @Override
    public boolean wantsCellInteraction() {
        return true; // Le monstre veut interagir par cellule
    }

    @Override
    public boolean wantsViewInteraction() {
        return false; // Le monstre n'interagit pas à distance
    }

    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(handler, isCellInteraction);
    }
}
