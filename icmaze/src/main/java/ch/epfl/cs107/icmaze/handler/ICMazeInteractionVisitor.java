package ch.epfl.cs107.icmaze.handler;

import ch.epfl.cs107.icmaze.actor.ICMazePlayer;
import ch.epfl.cs107.icmaze.actor.Portal;
import ch.epfl.cs107.icmaze.actor.collectable.Heart;
import ch.epfl.cs107.icmaze.actor.collectable.Key;
import ch.epfl.cs107.icmaze.actor.collectable.Pickaxe;
import ch.epfl.cs107.icmaze.actor.collectable.rare.RareItems;
import ch.epfl.cs107.icmaze.actor.enemies.Enemy;
import ch.epfl.cs107.icmaze.actor.enemies.Projectile;
import ch.epfl.cs107.icmaze.actor.rocks.Rock;
import ch.epfl.cs107.icmaze.area.ICMazeBehavior.ICMazeCell;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;

/**
 * Interaction visitor for all ICMaze entities.
 * Defines interaction methods for each concrete interactable type.
 */
public interface ICMazeInteractionVisitor extends AreaInteractionVisitor {
    /**
     * Interaction with a maze cell.
     */
    default void interactWith(ICMazeCell cell, boolean isCellInteraction){}
    /**
     * Interaction with the player.
     */
    default void interactWith(ICMazePlayer player, boolean isCellInteraction){}
    /**
     * Interaction with a pickaxe.
     */
    default void interactWith(Pickaxe pickaxe, boolean isCellInteraction){}
    /**
     * Interaction with a heart.
     */
    default void interactWith(Heart heart, boolean isCellInteraction){}
    /**
     * Interaction with a key.
     */
    default void interactWith(Key key, boolean isCellInteraction){}
    /**
     * Interaction with a portal.
     */
    default void interactWith(Portal portal, boolean isCellInteraction){}
    /**
     * Interaction with a rock.
     */
    default void interactWith(Rock rock, boolean isCellInteraction){}
    /**
     * Interaction with an enemy.
     */
    default void interactWith(Enemy enemy, boolean isCellInteraction){}
    /**
     * Interaction with a projectile.
     */
    default void interactWith(Projectile projectile, boolean isCellInteraction){}
    /**
     * Interaction with a rare items .
     */
    default void interactWith(RareItems rareItems, boolean isCellInteraction){}
}
