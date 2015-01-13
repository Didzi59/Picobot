PICOBOT-ALLOY
=============

* Jérémy BOSSUT
* Quentin BAILLEUL
* Romain PHILIPPON

[Lien du dépôt chez GitHub](https://github.com/Didzi59/Picobot)

[Lien du commit correspondant à la dernière version](https://github.com/Didzi59/Picobot/commit/0bdf5f7401dfa7aedc1e8f2eadff18768a1ca932)

PRÉSENTATION
------------

Picobot Alloy est un programme qui génère un ensemble de règles pour [Picobot](http://www.cs.hmc.edu/picobot/) en utilisant le solveur SAT [Alloy](http://alloy.mit.edu/alloy/) et qui teste celles-ci en utilisant une version modifiée d'un simulateur Picobot JAVA.

Le programme est en SCALA et utilise l'outil de build SBT. Pour le lancer, il faut utiliser la commande `sbt "run [nombre-solutions]"` où `nombre-solutions` est un paramètre entier déterminant le nombre d'ensembles de règles que le programme doit générer avec Alloy.

ARCHITECTURE
------------

L'ensemble des sources de Picobot Alloy se trouve dans le package `opl.iagl.alloy`.

Du côté SCALA, vous retrouverez deux objets à la racine du package :
* __Main__ qui sert de point d'entrée et lance la génération des ensembles de règles et des simulations.
* __alloy__ contenu dans `package.scala` qui contient (en plus des fonctions permettant de lancer les simulations) les déclarations des classes symbolisant d'un point de vue données une règle pour le Picobot.

Vous trouverez également dans un sous package `util` la définition de trois objets :
* __AlloyOutputParser__ qui possède l'ensemble des fonctions pour parser sous forme de chaines de caractères une solution Alloy.
* __Export__ qui possède les définitions des différentes fonctions permettant d'exporter au _format CSV_ les résultats d'une simulation.
* __Extractor__ permet grâce à ses fonctions d'extraire des positions ou des règles à partir d'un fichier.

Du côté JAVA, les classes qui ont du être développées se trouvent dans le package `opl.iagl.alloy.picobot` contenant :
* __OracleSimulator__ qui lance une simulation du Picobot.
* __SimulationPicobotException__ qui est une exception qui contient, en plus du message d'erreur, le nombre de cases que le Picobot a parcouru durant la simulation.

FONCTIONNEMENT
--------------

### RESSOURCES

Pour fonctionner, le programme nécessite au minimum deux éléments :
1. Un modèle Alloy décrivant un ensemble de règles pour le Picobot
2. Une map (_fermée_) pour le Picobot où le caractère # définit un mur

Chacun de ces éléments doit être situé dans le dossier resources situé dans `src/main/resources`. Le modèle Alloy doit être disposé dans le sous-dossier `alloy`. Il doit être un fichier un fichier Alloy avec l'extension _.als_. Quant aux maps, celles-ci doivent être déposées dans le sous-dossier `maps`. Ce fichier doit être un fichier texte, de préférence comportant l'extension _.map_ pour signifier que c'est un fichier contenant une map.

### GÉNÉRATION DES RÉSULTATS

#### NOTE TECHNIQUE

Pour optimiser la vitesse des simulations, le programme utilise les [futures](http://docs.scala-lang.org/overviews/core/futures.html) de SCALA. Ainsi, une future correspond à une simulation pour un modèle Alloy et une map particulière. Lorsque toutes les futures sont construites, celles-ci sont chainées et lancées de manière concurrente.

Pour qu'une simulation fonctionne, celle-ci stocke un fichier de règles dans le dossier `temp` de votre système pour les charger dans le simulateur.

Lorsqu'une future termine sa simulation, son état d'acceptance (qui est une fonction) génère un fichier résultat dans le dossier `picobot-alloy-res` qui, s'il n'existe pas, est créé dans le dossier `home` de votre système. C'est la raison pour laquelle tous les fichiers de résultats ne sont pas générés en même temps. Vous devez attendre l'arrêt du programme si vous voulez être sûr de disposer de tous les fichiers de résultats.

#### À PROPOS DES FICHIERS RÉSULTATS

Pour rappel, __un fichier résultat est formaté en CSV__. Pour chaque simulation, la future dans son état d'acceptance génère trois fichiers de résultats différents. Ces résultats sont la couverture globale de la map parcourue par le Picobot, le nombre moyen de cases parcourues par le Picobot pour chaque ensemble de règles et pour chaque ensemble de règles le nombre fois où le Picobot a parcouru toutes les cases d'une map et le nombre de fois où le Picobot n'a pas réussi à parcourir toutes les cases.

La désignation d'un fichier suit la règle suivante :
* Nom du modèle Alloy - sans extension - qui a généré les règles
* Nom de la map utilisée lors de la simulation
* Type du calcul sur les résultats
    * Si c'est la couverture parcourue : _coveringRate_
    * Si c'est le nombre moyen de cases parcourues : _average-crossed-cells_
    * Si c'est le nombre de cas où le Picobot a parcouru ou non toute la map : _nb-passed-failed-positions_
* Extension du modèle : _.csv_

Par exemple, si nous avons un modèle nommé PicoRules.als et une map dénommée PicoMap.map, le fichier comportera le nom *PicoRules-PicoMap__covering-rate.csv*.

#### FORMATAGE DES FICHIERS RÉSULTATS

Dans le cas de la couverture de parcours :

Listes des règles | Taux de couverture
----------------- | -------------------
règles au format Picobot | taux normalisé (valeur appartenant à [0 ; 1])

Dans le cas du nombre moyen de cases parcourues :

Listes des règles | Nombre total de cases | Nombre moyen de cases parcourues
----------------- | --------------------- | --------------------------------
règles au format Picobot | nombre de cases à parcourir de la map | nombre moyen au format entier compris entre 0 et le nombre total de cases

Dans le cas du nombre de parcours passants ou échouants pour chaque ensemble de règles :

Listes des règles | Nombre total de cases | Nombre de cas passants | Nombre de cas échouants
----------------- | --------------------- | ---------------------- | -----------------------
règles au format Picobot | nombre de cases à parcourir de la map | nombre moyen au format entier compris entre 0 et le nombre total de cases | nombre moyen au format entier compris entre 0 et le nombre total de cases

DOCUMENTATION
-------------

Pour générer la documentation utilisez la commande suivante : `sbt doc`. Vous trouverez ensuite celle-ci dans `target/scala-2.11/api`
