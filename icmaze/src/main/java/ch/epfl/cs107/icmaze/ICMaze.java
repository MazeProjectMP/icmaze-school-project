    package ch.epfl.cs107.icmaze;

    import ch.epfl.cs107.icmaze.actor.ICMazePlayer;
    import ch.epfl.cs107.icmaze.actor.rocks.CommonRock;
    import ch.epfl.cs107.icmaze.actor.collectable.Heart;
    import ch.epfl.cs107.icmaze.actor.collectable.Key;
    import ch.epfl.cs107.icmaze.actor.collectable.Pickaxe;
    import ch.epfl.cs107.icmaze.area.ICMazeArea;
    import ch.epfl.cs107.icmaze.area.maps.*;
    import ch.epfl.cs107.icmaze.handler.DialogHandler;
    import ch.epfl.cs107.play.areagame.AreaGame;
    import ch.epfl.cs107.play.engine.actor.Dialog;
    import ch.epfl.cs107.play.io.FileSystem;
    import ch.epfl.cs107.play.math.DiscreteCoordinates;
    import ch.epfl.cs107.play.math.Orientation;
    import ch.epfl.cs107.play.window.Keyboard;
    import ch.epfl.cs107.play.window.Window;

    /**
     * Main game class for ICMaze.
     * Manages areas, player transitions, and dialog display.
     */
    public class ICMaze extends AreaGame implements DialogHandler {
        private ICMazeArea[] areas;
        private ICMazePlayer player;
        private DiscreteCoordinates initialPosition = new DiscreteCoordinates(5,7);

        private Dialog activeDialog = null;
        /**
         * Sets the currently displayed dialog.
         * @param dialog the dialog to display (can be null)
         */
        public void setActiveDialog(Dialog dialog) {
            activeDialog = dialog;
        }

        /**
         * Displays the given dialog (DialogHandler implementation).
         * @param dialog the dialog to display
         */
        @Override
        public void publish(Dialog dialog) {
            setActiveDialog(dialog);
        }

        /**
         * Draws the current area and the active dialog if any.
         */
        @Override
        public void draw() {
            super.draw();
            if (activeDialog != null) {
                activeDialog.draw(getWindow());
            }
        }

        /**
         * Teleports the player to another area at the given coordinates.
         * @param destinationArea the destination area title
         * @param destinationCoordinates the destination coordinates in the destination area
         */
        private void teleportation(String destinationArea, DiscreteCoordinates destinationCoordinates){
            player.leaveArea();

            setCurrentArea(destinationArea, false);

            player.enterArea(getCurrentArea() , destinationCoordinates); /// Makes the player travel to the area

            player.centerCamera(); ///Centers the camera on the player
        }

        /**
         * Creates and registers all game areas.
         */
        private void createAreas() {
            areas = LevelGenerator.generateLine(this, 4); // 3 aires labyrinthiques

            /// adds all areas in the game
            for (ICMazeArea area : areas) {
                addArea(area);
            }
        }

        /**
         * @param window (Window): display context. Not null
         * @param fileSystem (FileSystem): given file system. Not null
         * @return true if the game begins properly
         */
        @Override
        public boolean begin(Window window, FileSystem fileSystem) {
            if (super.begin(window, fileSystem)) {
                createAreas();
                initArea(areas[0].getTitle());
                return true;
            }
            return false;
        }

        /**
         * @param deltaTime elapsed time since last update, in seconds, non-negative
         */
        @Override
        public void update(float deltaTime) {
            Keyboard keyboard = getWindow().getKeyboard();

            if (keyboard.get(KeyBindings.RESET_GAME).isPressed()) {
                end();
                activeDialog = null;
                begin(getWindow(), getFileSystem());
                return;
            }

            /// If dialog is on, it doesn't update the area
            if (activeDialog != null) {
                if (keyboard.get(KeyBindings.NEXT_DIALOG).isPressed()) {
                    activeDialog.update(deltaTime);
                    if (activeDialog.isCompleted()) {
                        activeDialog = null;
                    }
                }
                return;
            }
            super.update(deltaTime);

            String destinationArea = player.getDestinationArea();
            DiscreteCoordinates destinationCoordinates = player.getDestinationCoordinates();

            if (destinationCoordinates != null && destinationArea != null) {
                teleportation(destinationArea, destinationCoordinates);
                player.clearPortalToTraverse();
            }

            if (player.isDead()) {
                end();
                begin(getWindow(), getFileSystem());
            }
        }
        /**
         * Ends the game (called when the game is stopped).
         */
        @Override
        public void end() {

        }

        /**
         * Returns the game title.
         * @return the game title
         */
        @Override
        public String getTitle() {
            return "ICMaze";
        }

        /**
         * sets the area named `areaKey` as current area in the game ICMaze
         * @param areaKey (String) title of an area
         */
        private void initArea(String areaKey) {
            ICMazeArea area = (ICMazeArea) setCurrentArea(areaKey, true);
            player = new ICMazePlayer(area, Orientation.UP, initialPosition, "player", 5);
            player.enterArea(area, initialPosition);
            player.centerCamera();
            player.getInventory().setWindowSize(getWindow().getWidth(), getWindow().getHeight());
        }
    }