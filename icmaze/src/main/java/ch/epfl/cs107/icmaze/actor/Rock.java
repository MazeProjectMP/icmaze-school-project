package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.icmaze.RandomGenerator;
import ch.epfl.cs107.icmaze.actor.collectable.Heart;
import ch.epfl.cs107.icmaze.actor.util.Cooldown;
import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Transform;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;

public class Rock extends AreaEntity {
    private Sprite sprite ;
    private boolean isBroken=false;

    // health interaction
    private Health health;
    private final int ROCK_MAX_LIFE = 3;
    Cooldown immunityCooldown ;



    // Points de vies
//    private final int maxHP;


    // Animation de disparition
    private Animation vanishAnimation = new Animation("icmaze/vanish", 7, 2, 2, this,
        32, 32, new Vector(-0.5f, 0.0f),
        24/7, false);;
    private boolean isVanishing = false;
    ///  animation health
    private Animation rockAnmation;
    private final float HEALTH_DISPLAY_DURATION = 1.0f; // 1 seconde d'affichage
    private boolean showHealth = false;

    public Rock(Area area, DiscreteCoordinates position) {
        super(area, Orientation.LEFT, position);
        sprite = new Sprite("rock.2", 1, 1, this);
        health = new Health(this, Transform.I.translated(-0.15, 0.85f), ROCK_MAX_LIFE, false);

        immunityCooldown = new Cooldown(1.0f); //1 s d'affichage

        showHealth =false;
//        // Points de vies
//        maxHP = 3;
//        currentHP = maxHP;
    }
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
         if (showHealth) {//affichage de la barre avec cooldown
            if (immunityCooldown.ready(deltaTime)) {// qd le temps est ecoule retourne true
                showHealth = false;
            }
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




    }
    @Override
    public boolean takeCellSpace() {
        return !isBroken && !isVanishing;

    }

    public boolean isBroken() {
        return isBroken;
    }
    @Override
    public void draw(Canvas canva){
        if(isVanishing){
            vanishAnimation.draw(canva);
        }
        else if (sprite != null || (System.currentTimeMillis() / 250) % 2 == 0) {
            sprite.draw(canva);
        }
        // Afficher la barre de vie seulement si showHealth est vrai
        if (showHealth && !isBroken) {
            health.draw(canva);
        }



    }
    // Méthode pour prendre des dégâts
    public void takeDamage(int damage) {
        if (!isBroken && health.isOn()) {
            //affiche barre de vie
            showHealth = true;
            immunityCooldown.reset(); //reintialise le timer
            health.decrease(damage);//inflige les degats

            // Si le rocher est casse
            if (health.isOff()) {
                isBroken = true;
                isVanishing = true ;
            }
        }
    }


    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());

    }
    @Override
    public boolean isCellInteractable() {
        return false;  //pas dinteraction avec le rocher
    }

    @Override
    public boolean isViewInteractable() {
        return true;  //peut se faire casser
    }


    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
            ((ICMazeInteractionVisitor) v).interactWith(this, isCellInteraction);

    }

//    public void attackWithPickaxe() {
//        if (!isBroken) {
//            currentHP -= 1;
//
//            if (currentHP <= 0) {
//                isBroken = true;
//                isVanishing = true;
//            }
//        }
//    }

}
