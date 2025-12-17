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

    public class ICMaze extends AreaGame implements DialogHandler {
        private ICMazeArea[] areas;
        private ICMazePlayer player;
        private DiscreteCoordinates initialPosition = new DiscreteCoordinates(5,7);

        private Dialog activeDialog = null;
        /**
         * Add all the IC Maze areas
         */

        public void setActiveDialog(Dialog dialog) {
            activeDialog = dialog;
        }

        @Override
        public void publish(Dialog dialog) {
            setActiveDialog(dialog);
        }

        @Override
        public void draw() {
            super.draw(); // dessine l'aire courante
            if (activeDialog != null) {
                // Le Dialog veut un Canvas, pas un Window
                activeDialog.draw(getWindow());
            }
        }

        private void teleportation(String destinationArea, DiscreteCoordinates destinationCoordinates){
            player.leaveArea(); // Quitte la current aire

            setCurrentArea(destinationArea, false); // Change l'aire courante

            player.enterArea(getCurrentArea() , destinationCoordinates);// Teleporte le personnage dans l'aire

            player.centerCamera(); // Centre la camera sur le personnage
        }

        private void createAreas() {
            //     generateHardCodedLevel();
            areas = LevelGenerator.generateLine(this, 4); // 3 aires labyrinthiques

            // Ajouter toutes les aires au jeu
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

            // si dialogue actif: on ne met PAS à jour l'aire (freeze)
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

//        private void generateHardCodedLevel() {
//
//            // Création des aires
//            spawn = new Spawn();
//            SmallArea smallArea = new SmallArea(ICMazeArea.AreaPortals.E,ICMazeArea.AreaPortals.W, Integer.MAX_VALUE, Difficulty.HARD);
//            MediumArea mediumArea = new MediumArea(ICMazeArea.AreaPortals.E,ICMazeArea.AreaPortals.W, Integer.MAX_VALUE, Difficulty.HARD);
//            LargeArea largeArea = new LargeArea(ICMazeArea.AreaPortals.E,ICMazeArea.AreaPortals.W, Integer.MAX_VALUE, Difficulty.HARD);
//            bossArea = new BossArea();
//            spawn.lockPortalWithKey(ICMazeArea.AreaPortals.E, Integer.MAX_VALUE, smallArea.getTitle());
//            spawn.setDestinationCoordinate(ICMazeArea.AreaPortals.E, smallArea.getEstPortalSpawn());
//
//            smallArea.openPortal(ICMazeArea.AreaPortals.W, spawn.getTitle());
//            smallArea.lockPortalWithKey(ICMazeArea.AreaPortals.E, smallArea.getKeyID(),mediumArea.getTitle());
//            smallArea.setDestinationCoordinate(ICMazeArea.AreaPortals.W, spawn.getWestPortalSpawn());
//            smallArea.setDestinationCoordinate(ICMazeArea.AreaPortals.E, mediumArea.getEstPortalSpawn());
//
//            mediumArea.openPortal(ICMazeArea.AreaPortals.W, smallArea.getTitle());
//            mediumArea.lockPortalWithKey(ICMazeArea.AreaPortals.E, mediumArea.getKeyID(), largeArea.getTitle());
//            mediumArea.setDestinationCoordinate(ICMazeArea.AreaPortals.W, smallArea.getWestPortalSpawn());
//            mediumArea.setDestinationCoordinate(ICMazeArea.AreaPortals.E, largeArea.getEstPortalSpawn());
//
//            largeArea.openPortal(ICMazeArea.AreaPortals.W, mediumArea.getTitle());
//            largeArea.lockPortalWithKey(ICMazeArea.AreaPortals.E,  largeArea.getKeyID(), bossArea.getTitle());
//            largeArea.setDestinationCoordinate(ICMazeArea.AreaPortals.W, mediumArea.getWestPortalSpawn());
//            largeArea.setDestinationCoordinate(ICMazeArea.AreaPortals.E, bossArea.getEstPortalSpawn());
//
//            bossArea.lockPortalWithKey(ICMazeArea.AreaPortals.W, -1,largeArea.getTitle());
//            bossArea.setDestinationCoordinate(ICMazeArea.AreaPortals.W, largeArea.getWestPortalSpawn());
//
//            // Ajout des aires au jeu
//            addArea(spawn);
//            addArea(smallArea);
//            addArea(mediumArea);
//            addArea(largeArea);
//            addArea(bossArea);
//        }

        @Override
        public void end() {

        }

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

            // Inititalise le main character
            player = new ICMazePlayer(area, Orientation.UP, initialPosition, "player", 5);
            player.enterArea(area, initialPosition);
            player.centerCamera();
            player.getInventory().setWindowSize(getWindow().getWidth(), getWindow().getHeight());
        }
    }