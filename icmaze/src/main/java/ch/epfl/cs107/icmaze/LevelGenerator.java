package ch.epfl.cs107.icmaze;


import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.icmaze.area.maps.LargeArea;
import ch.epfl.cs107.icmaze.area.maps.MediumArea;
import ch.epfl.cs107.icmaze.area.maps.SmallArea;
import ch.epfl.cs107.play.areagame.area.Area;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ch.epfl.cs107.icmaze.RandomGenerator.rng;

public class LevelGenerator {
    public LevelGenerator() {
    }

    public record AreaReference (int x , int y ){}///  cree constructeur, getter,  string(), etc seuls//fictif


    private static AreaReference calculateNewDir(AreaReference current, ICMazeArea.AreaPortals d) {
        return switch (d) {
            case N -> new AreaReference(current.x(), current.y() + 1);
            case E -> {
                yield new AreaReference(current.x() + 1, current.y());
            }
            case S -> new AreaReference(current.x(), current.y() - 1);
            case W -> new AreaReference(current.x() - 1, current.y()); /// on le l'utilise pas
        };
    }

    private static ICMazeArea.AreaPortals opposite(ICMazeArea.AreaPortals d) {
        return switch (d) {
            case N -> ICMazeArea.AreaPortals.S;
            case S -> ICMazeArea.AreaPortals.N;
            case E -> ICMazeArea.AreaPortals.W;
            case W -> ICMazeArea.AreaPortals.E;
        };
    }

        public static int setDifficulty(int i, int length) {
            double progress = (double) (i + 1) / length;

                return switch ((int) (progress * 4)){
                case 0 -> 0 ; //EASY
                case 1 -> 1 ; // MEDIUM;
                case 2 -> 2 ; // HARD;
                default -> 3 ;//HARDEST;
            };
        }

    private static ICMazeArea.AreaPortals chooseFreeDirection(
            AreaReference current,
            ICMazeArea.AreaPortals lastDir,
            Set<AreaReference> occupied
    ) {
        // directions
        List<ICMazeArea.AreaPortals> candidates = new ArrayList<>(List.of(
                ICMazeArea.AreaPortals.N,
                ICMazeArea.AreaPortals.E,
                ICMazeArea.AreaPortals.S
        ));

        // pour pas aller en arriere
        candidates.remove(opposite(lastDir));

        // verifier que pas occupe
        candidates.removeIf(d -> occupied.contains(calculateNewDir(current, d)));

        // choisi au hasard parmmi les positions valides
        if (candidates.isEmpty()) {
            throw new IllegalStateException("Aucune direction libre disponible depuis " + current);
        }
        return candidates.get(RandomGenerator.nextInt(candidates.size()));
    }


//        public ICMazeArea createAreaFromDifficulty ( int i, int length){
//            double progress = (double) (i + 1) / length;
//
//            double r = rng.nextDouble();
//
//            if (r < progress * progress) {
//                return new LargeArea();
//            }
//            else if (r < progress) {
//                return new MediumArea();
//            }
//            else {
//                return new SmallArea(ICMazeArea.AreaPortals areaPortalEntry, ICMazeArea.AreaPortals areaPortalExit, int keyID);
//            }
//
//
//        }
//
//        public boolean isOccupied(int x , int y ){
//            if(occupied[0][0]!=null){
//                 return(!isOccupied) ;
//            }
//        }


        public static ICMazeArea[] generateLine(ICMaze game, int length) {
            if (length < 1) throw new IllegalArgumentException("length must be >= 1");


            /// retourne un tableau avec lorde dans lequel elle doit etre charge
            List<ICMazeArea> areas = new ArrayList<>();
            Set<AreaReference> occupied = new HashSet<>(); /// pour les positions occupes

            ICMazeArea spawn = new ch.epfl.cs107.icmaze.area.maps.Spawn();/// ajoute spawn a la position 0
            areas.add(spawn);
            game.addArea(spawn);


            AreaReference currentPos = new AreaReference(0, 0);
            occupied.add(currentPos);

            ICMazeArea departure = spawn ;
            ICMazeArea.AreaPortals lastDir = ICMazeArea.AreaPortals.E; /// premiere position arriere arbitraire

            int keyBase = Integer.MAX_VALUE; // au hasard?


            for(int i = 1; i < length ; i++){
                //select une pos
                ICMazeArea.AreaPortals dir = chooseFreeDirection(currentPos, lastDir, occupied);
                AreaReference nextPos = calculateNewDir(currentPos, dir);
                //calcule difficulte
                int diff = setDifficulty(i,length);
                int keyId = keyBase - i; // a voir

                //cree une aire avec niveau diff
                ICMazeArea nextArea = createArea(diff, dir, keyId);

                /// Connecter
                connectWithKey(departure, dir, nextArea, keyId);

                // enregistrer l’aire dans le jeu
                areas.add(nextArea);
                game.addArea(nextArea);



                /// connecter les aires
                occupied.add(nextPos);
                currentPos = nextPos;
                lastDir = dir;
                departure = nextArea;
                System.out.println("ADD AREA: " + nextArea.getTitle());


            }
            // BossArea
            ICMazeArea boss = new ch.epfl.cs107.icmaze.area.maps.BossArea();

            // même direction que la dernière progression
            ICMazeArea.AreaPortals bossDir = lastDir;
            int bossKeyId = ((ch.epfl.cs107.icmaze.area.maps.MazeLevels) departure).getKeyID();
            connectWithKey((ICMazeArea) departure, lastDir, (ICMazeArea) boss, bossKeyId);//cle du boss


            // connecter departure et boss

            connectWithKey(departure, bossDir, boss, bossKeyId);

            // enregistrer
            areas.add(boss);
            game.addArea(boss);

            return areas.toArray(new ICMazeArea[0]);

        }
    private static ch.epfl.cs107.play.math.DiscreteCoordinates spawnFrom(ICMazeArea area, ICMazeArea.AreaPortals side) {
        return switch (side) {
            case W -> area.getWestPortalSpawn();
            case E -> area.getEstPortalSpawn();
            case N -> area.getNorthPortalSpawn();
            case S -> area.getSouthPortalSpawn();
        };
    }
    private static ICMazeArea createArea(int diff, ICMazeArea.AreaPortals dir, int keyId) {
        ICMazeArea.AreaPortals entry = opposite(dir);
        ICMazeArea.AreaPortals exit  = dir;

        return switch (diff) {
            case 0 -> new SmallArea(entry, exit, keyId);
            case 1 -> new MediumArea(entry, exit, keyId);
            default -> new LargeArea(entry, exit, keyId); // 2 ou 3
        };
    }


    private static void connectWithKey(ICMazeArea from, ICMazeArea.AreaPortals dirToNext, ICMazeArea to, int keyId) {
        // portail alle  verrouillé avec clé
        from.lockPortalWithKey(dirToNext, keyId, to.getTitle());
        from.setDestinationCoordinate(dirToNext, spawnFrom(to, opposite(dirToNext)));

        // portail retour ouvert
        ICMazeArea.AreaPortals back = opposite(dirToNext);
        to.openPortal(back, from.getTitle());
        to.setDestinationCoordinate(back, spawnFrom(from, dirToNext));
    }


}