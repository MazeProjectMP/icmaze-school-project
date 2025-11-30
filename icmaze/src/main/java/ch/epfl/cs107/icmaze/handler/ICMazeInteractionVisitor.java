package ch.epfl.cs107.icmaze.handler;

import ch.epfl.cs107.icmaze.actor.ICMazePlayer;
import ch.epfl.cs107.icmaze.actor.Portal;
import ch.epfl.cs107.icmaze.actor.collectable.Heart;
import ch.epfl.cs107.icmaze.actor.collectable.Key;
import ch.epfl.cs107.icmaze.actor.collectable.Pickaxe;
import ch.epfl.cs107.icmaze.area.ICMazeBehavior.ICMazeCell;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Sprite;

/**
 * InteractionVisitor for the ICMaze entities
 */

public interface ICMazeInteractionVisitor extends AreaInteractionVisitor {
  /// Add Interaction method with all non Abstract Interactable
    default void interactWith(ICMazeCell cell, boolean isCellInteraction){}
    default void interactWith(ICMazePlayer player, boolean isCellInteraction){}
    default void interactWith(Pickaxe pickaxe, boolean isCellInteraction){}
    default void interactWith(Heart heart, boolean isCellInteraction){}
    default void interactWith(Key key, boolean isCellInteraction){}
    default void interactWith(Portal portal, boolean isCellInteraction){}
}
