# IC Maze — projet scolaire Java

IC Maze est un jeu de labyrinthe en 2D développé dans le cadre du mini-projet 2 de programmation CS107 à l’EPFL, édition 2025. Le joueur explore des salles, récupère une pioche et des clés, traverse des portails et affronte des ennemis jusqu’à une salle de boss.

Le projet met en pratique la programmation orientée objet, les machines à états, les interactions entre acteurs, les graphes et la génération procédurale. Il s’exécute sur ordinateur dans une fenêtre Swing ; ce n’est pas une application web.

## Contenu du jeu

- Génération d’une succession de labyrinthes de tailles et de difficultés variables. La configuration actuelle crée quatre salles entre la zone de départ et le boss.
- Pioche, rochers destructibles, clés et portails verrouillés.
- Ennemis avec déplacement et poursuite, sorciers, projectiles et combat de boss.
- Points de vie, objets à collecter, inventaire et compteurs affichés à l’écran.
- Dialogues d’introduction et de progression, puis apparition d’un trésor après la victoire.

## Compiler et lancer

Prérequis : **JDK 21 ou plus récent**, **Maven 3.9** et un environnement graphique de bureau. Vérifier que `java -version` et `mvn -version` utilisent le JDK voulu.

```sh
git clone https://github.com/MazeProjectMP/icmaze-school-project.git
cd icmaze-school-project
mvn package
java -jar icmaze/target/IC-Maze-2025-jar-with-dependencies.jar
```

Le nom du JAR ci-dessus est défini dans [icmaze/pom.xml](icmaze/pom.xml). Maven construit le moteur avant le jeu, puis regroupe les classes et les ressources dans ce JAR. Le premier build télécharge les plugins Maven nécessaires.

Dans IntelliJ IDEA, ouvrir le [pom.xml racine](pom.xml) comme projet Maven, choisir un JDK compatible, puis exécuter `ch.epfl.cs107.Play` dans le module `icmaze`. Le module `tutos` possède une autre classe `Play` : elle lance les exercices d’introduction, pas IC Maze.

## Commandes

| Touche | Action |
| --- | --- |
| Flèches directionnelles | Déplacer le personnage |
| `E` | Interagir avec l’objet ou le passage devant le personnage |
| `Espace` | Utiliser la pioche après l’avoir ramassée |
| `Entrée` | Faire avancer un dialogue |
| `R` | Recommencer la partie |

Les associations sont définies dans [KeyBindings.java](icmaze/src/main/java/ch/epfl/cs107/icmaze/KeyBindings.java). Les constantes `P` et `B` y existent, mais aucune commande correspondante n’est branchée dans la boucle actuelle du jeu.

## Organisation du dépôt

| Chemin | Rôle |
| --- | --- |
| [icmaze/](icmaze/) | Jeu : logique, acteurs, cartes et ressources |
| [Play.java](icmaze/src/main/java/ch/epfl/cs107/Play.java) | Point d’entrée du jeu, fenêtre et boucle principale |
| [ICMaze.java](icmaze/src/main/java/ch/epfl/cs107/icmaze/ICMaze.java) | Gestion des zones, déplacements entre salles et dialogues |
| [MazeGenerator.java](icmaze/src/main/java/ch/epfl/cs107/icmaze/MazeGenerator.java) et [LevelGenerator.java](icmaze/src/main/java/ch/epfl/cs107/icmaze/LevelGenerator.java) | Génération des labyrinthes et de leur enchaînement |
| [actor/](icmaze/src/main/java/ch/epfl/cs107/icmaze/actor/) | Joueur, inventaire, portails, objets, rochers et ennemis |
| [area/](icmaze/src/main/java/ch/epfl/cs107/icmaze/area/) | Comportement des cellules et types de salles |
| [resources/](icmaze/src/main/resources/) | Images, polices et dialogues XML |
| [game-engine/](game-engine/) | Moteur pédagogique : affichage Swing, grille, collisions, interactions et ressources |
| [tutos/](tutos/) | Exercices d’introduction utilisant le même moteur |
| [Submit.java](icmaze/src/main/java/ch/epfl/cs107/Submit.java) | Utilitaire local de création de l’archive de rendu ; inutile pour jouer |
| [Makefile](Makefile) | Raccourci `make run` pour compiler puis lancer le jeu |

Les modules Maven et les packages `ch.epfl.cs107` conservent leurs noms d’origine pour préserver les imports et la compilation. L’ancien nom du dépôt était `MP2`.

## Vérification et limites

La compilation des trois modules et la création du JAR ont été vérifiées avec Maven et un JDK 25 ; les 166 sources Java ont également été compilées avec `javac --release 21`. Les noms des classes publiques, des fichiers et des packages ont été contrôlés, sans incohérence de casse détectée.

Le dépôt ne contient pas de suite de tests automatisés. Cette vérification ne remplace pas un parcours manuel complet du jeu. Il s’agit d’un projet académique conservé pour présenter son code et son architecture.

## Contributions et ressources

L’historique conserve les contributions de **Hedi Fourati** et **Emna Bellouchi**. Le moteur et les ressources pédagogiques du cours sont conservés avec le projet ; ils sont distincts des développements propres au jeu. Les noms et références présents dans les fichiers sont préservés. Aucune licence globale supplémentaire n’est ajoutée à ces éléments.
