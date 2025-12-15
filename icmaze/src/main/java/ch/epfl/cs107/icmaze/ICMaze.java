    package ch.epfl.cs107.icmaze;

    import ch.epfl.cs107.icmaze.actor.ICMazePlayer;
    import ch.epfl.cs107.icmaze.actor.Portal;
    import ch.epfl.cs107.icmaze.actor.Rock;
    import ch.epfl.cs107.icmaze.actor.collectable.Heart;
    import ch.epfl.cs107.icmaze.actor.collectable.Key;
    import ch.epfl.cs107.icmaze.actor.collectable.Pickaxe;
    import ch.epfl.cs107.icmaze.actor.enemies.Enemy;
    import ch.epfl.cs107.icmaze.actor.enemies.LogMonster;
    import ch.epfl.cs107.icmaze.area.ICMazeArea;
    import ch.epfl.cs107.icmaze.area.maps.*;
    import ch.epfl.cs107.play.areagame.AreaGame;
    import ch.epfl.cs107.play.areagame.area.Area;
    import ch.epfl.cs107.play.io.FileSystem;
    import ch.epfl.cs107.play.math.DiscreteCoordinates;
    import ch.epfl.cs107.play.math.Orientation;
    import ch.epfl.cs107.play.math.Vector;
    import ch.epfl.cs107.play.window.Keyboard;
    import ch.epfl.cs107.play.window.Window;

    public class ICMaze extends AreaGame {
        private final String[] areas = {"icmaze/Spawn", "icmaze/Boss"};
        private int areaIndex;
        private ICMazePlayer player;

        // Initialisation des collectables
        private Heart heart;
        private Pickaxe pickaxe;

        // Initialisation des cles dans le spawn
        private Key key1;
        private Key key2;
        private static final DiscreteCoordinates key1Position = new DiscreteCoordinates(6, 5);
        private static final DiscreteCoordinates key2Position = new DiscreteCoordinates(1, 2);

        private Rock rock;
        private static final DiscreteCoordinates rockPosition = new DiscreteCoordinates(3, 3);

        // Positions initiales des entites de la map
        private DiscreteCoordinates initialPostion = new DiscreteCoordinates(5,7);
        private DiscreteCoordinates initialHeartPosition =  new DiscreteCoordinates(4,5);
        private DiscreteCoordinates initialPickaxePosition =  new DiscreteCoordinates(5,4);

        // Declaration des Aires principales :
        private Spawn spawn = new Spawn();
        private BossArea bossArea = new BossArea();


        /**
         * Add all the IC Maze areas
         */

        private void teleportation(String destinationArea, DiscreteCoordinates destinationCoordinates){
            player.leaveArea(); // Quitte la current aire

            setCurrentArea(destinationArea, false); // Change l'aire courante

            player.enterArea(getCurrentArea() , destinationCoordinates);// Teleporte le personnage dans l'aire

            player.centerCamera(); // Centre la camera sur le personnage
        }

        private void createAreas() {
//            generateHardCodedLevel();
            ICMazeArea[] generatedAreas = LevelGenerator.generateLine(this, 0); // 3 aires labyrinthiques

            // Ajouter toutes les aires au jeu
            for (ICMazeArea area : generatedAreas) {
                addArea(area);
            }

            // Garder les références pour l'initialisation
            spawn = (Spawn) generatedAreas[0];
            bossArea = (BossArea) generatedAreas[generatedAreas.length - 1];
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
                areaIndex = 0;
                initArea(areas[areaIndex]);
                return true;
            }
            return false;
        }

        /**
         * @param deltaTime elapsed time since last update, in seconds, non-negative
         */
        @Override
        public void update(float deltaTime) {
            super.update(deltaTime);

            // Variable s'initialise a l'interaction
            String destinationArea = player.getDestinationArea();
            DiscreteCoordinates destinationCoordinates = player.getDestinationCoordinates();

            // Teleporte le player si interaction
            if (destinationCoordinates != null && destinationArea != null) {
                teleportation(destinationArea, destinationCoordinates);
                player.clearPortalToTraverse();
            }

            // Reset si R est presse
            Keyboard keyboard = getWindow().getKeyboard();
            if (keyboard.get(KeyBindings.RESET_GAME).isPressed()) {
                end();
                begin(getWindow(), getFileSystem());
            }

            if(player.isDead()){
                end();
                begin(getWindow(), getFileSystem());
            }
        }
//        private void generateHardCodedLevel() {
//
//            // Création des aires
//            SmallArea smallArea = new SmallArea(ICMazeArea.AreaPortals.E,ICMazeArea.AreaPortals.W, Integer.MAX_VALUE);
//            MediumArea mediumArea = new MediumArea(ICMazeArea.AreaPortals.E,ICMazeArea.AreaPortals.W, Integer.MAX_VALUE);
//            LargeArea largeArea = new LargeArea(ICMazeArea.AreaPortals.E,ICMazeArea.AreaPortals.W, Integer.MAX_VALUE);
//
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
            player = new ICMazePlayer(area, Orientation.UP, initialPostion, "player", 5);
            player.enterArea(area, initialPostion);
            player.centerCamera();

            // Inititalise la pioche
            pickaxe = new Pickaxe(area, initialPickaxePosition);
            area.registerActor(pickaxe);

            // Inititalise le coeur
            heart = new Heart(area, initialHeartPosition);
            area.registerActor(heart);

            // Initialise les cles
            key1 = new Key(area, key1Position, Integer.MAX_VALUE);
            key2 = new Key(area, key2Position, Integer.MAX_VALUE-1);
            area.registerActor(key1);
            area.registerActor(key2);
        }
    }