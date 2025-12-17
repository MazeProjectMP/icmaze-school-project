package ch.epfl.cs107.icmaze.area;

import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.AreaBehavior;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.window.Window;
/**
 * Behavior class for ICMaze areas.
 * Builds the area grid from a behavior image and defines cell interactions.
 */
public class ICMazeBehavior extends AreaBehavior {
    /**
     * Default ICMazeBehavior Constructor
     *
     * @param window (Window), not null
     * @param name   (String): Name of the Behavior, not null
     */
    public ICMazeBehavior(Window window, String name) {
        super(window, name);
        int height = getHeight();
        int width = getWidth();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                ICMazeCellType color = ICMazeCellType.toType(getRGB(height - 1 - y, x));
                setCell(x, y, new ICMazeCell(x, y, color));
            }
        }
    }


    /**
     * Enumeration of ICMaze cell types.
     * Each type defines whether the cell is walkable.
     */
    public enum ICMazeCellType {
        //https://stackoverflow.com/questions/25761438/understanding-bufferedimage-getrgb-output-values
        NULL(0, false),
        GROUND(-16777216, true),
        GROUND2( -14113468, false),
        GROUND3(-14179260, false),
        GROUND4(-14180030, false),
        GROUND5(-14113211, false),
        GROUND6(-14113212, false),
        WALL(-14112955, false),
        HOLE(-65536, true);

        final int type;
        final boolean isWalkable;

        /**
         * Returns the cell type corresponding to a pixel color.
         * @param type the RGB value
         * @return the corresponding cell type, or NULL if unknown
         */
        ICMazeCellType(int type, boolean isWalkable) {
            this.type = type;
            this.isWalkable = isWalkable;
        }

        public static ICMazeCellType toType(int type) {
            for (ICMazeCellType ict : ICMazeCellType.values()) {
                if (ict.type == type)
                    return ict;
            }
            /// Prints the int value of a new color before assign it to a type
            System.out.println(type);
            return NULL;
        }
    }

    public class ICMazeCell extends Cell {
        /// Type of the cell following the enum
        private final ICMazeCellType type;

        /**
         * Default ICMazeCell Constructor
         *
         * @param x    (int): x coordinate of the cell
         * @param y    (int): y coordinate of the cell
         * @param type (EnigmeCellType), not null
         */
        public ICMazeCell(int x, int y, ICMazeCellType type) {
            super(x, y);
            this.type = type;
        }

        /**
         * Indicates whether an entity can leave this cell.
         * @param entity the interacting entity
         * @return true if the entity can leave the cell
         */
        @Override
        protected boolean canLeave(Interactable entity) {
            return true;
        }


        /**
         * Indicates whether an entity can enter this cell.
         * @param entity the interacting entity
         * @return true if the entity can enter the cell
         */
       @Override
        protected boolean canEnter(Interactable entity) {
            /// If the cell is not walkable, no one can enter
           if (!type.isWalkable){
               return false;
           }
           /// If the entity is traversable, he can enter
           if (!entity.takeCellSpace()) {
               return !entity.takeCellSpace();
           }
           /// Checks whether there are other entities in the cell
            for (Interactable other : entities){
                if (other.takeCellSpace()){
                    return false;
                }
            }
            return true;
        }

        /**
         * Indicates whether the cell supports cell interactions.
         * @return true if the cell is interactable
         */
        @Override
        public boolean isCellInteractable() {
            return true;
        }

        /**
         * Indicates whether the cell supports view interactions.
         * @return true if the cell is view interactable
         */
        @Override
        public boolean isViewInteractable() {
            return false;
        }

        /**
         * Accepts an interaction visitor.
         * @param v the interaction visitor
         * @param isCellInteraction true if the interaction is cell-based
         */
        @Override
        public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        }

    }
}
