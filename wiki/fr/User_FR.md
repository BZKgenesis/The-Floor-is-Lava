# The Floor is Lava WIKI
Bienvenue dans le wiki du plugin The Floor is Lava, vous trouverez ici toutes les informations disponibles sur le plugin, ses fonctionnalités, et comment l'utiliser.

The Floor is Lava est un mini-jeu compétitif où les joueurs doivent récolter des ressources avant que la lave commence à monter progressivement.
Les équipes doivent construire rapidement, acheter des objets dans la boutique et éliminer leurs adversaires tout en survivant à la montée de la lave.

## Installation

Le plugin est compatible avec les serveurs Minecraft (26.1+) utilisant Paper. Pour l'installer, suivez ces étapes :
1. Téléchargez le fichier JAR du plugin depuis la page officielle ou le dépôt GitHub.
2. Placez le fichier JAR dans le dossier `plugins` de votre serveur Minecraft.
3. Redémarrez votre serveur pour que le plugin soit chargé.

> Vous pouvez ajouter le plugin [TreeFeller](https://modrinth.com/plugin/thizzyz-tree-feller) pour couper les arbres plus rapidement et ainsi récolter plus de ressources.

## Fonctionnalités

- Gestion de parties multijoueurs avec des équipes. (Voir la section [Gestion équipes](#gestion-équipes))
- Système de montée de lave progressive. (Voir la section [Déroulement d'une partie](#déroulement-dune-partie))
- Gestion de ressources et d'économie pour acheter des objets. (Voir la section [économie](#économie))
- Système de pari pour les joueurs. (Voir la section [Pari](#pari))
- Gestion de maps personnalisées et aléatoires. (Voir la section [gestion de map de jeu](#gestion-de-map-de-jeu))
- Système de statistiques pour chaque joueur. (Voir la section [Statistiques](#statistiques))
- Configuration facile via des fichiers YAML et des commandes en jeu. (Voir la section [Configuration](#configuration))

## Gestion équipes

La gestion des équipes se fait entièrement en jeu avec l'item de gestion d'équipe.
Les joueurs peuvent créer des équipes, rejoindre des équipes existantes, et gérer les membres de leur équipe.

## Commandes

### Commandes directes
- `/shop` : Ouvre la boutique en jeu.
- `/kit choose <nom kit>` : Permet de choisir un kit de départ.

### Commandes joueur
- `/tfl team` : Ouvre le menu de gestion d'équipe.

### Commandes opérateur

#### Gestion de partie
- `/tfl start` : Lance une partie.
- `/tfl stop` : Arrête complètement la partie en cours, tous les joueurs sont téléportés au lobby.
- `/tfl earlyRise` : Déclenche la montée de la lave avant la fin du temps de préparation.
- `/tfl pause` : Met la partie en pause, la montée de la lave est stoppée. (DEPRECATED)
- `/tfl resume` : Reprend la partie après une pause. (DEPRECATED)
- `/tfl setLevel <couche>` : Permet de définir le niveau de la lave. (DEPRECATED)
- `/tfl getLevel` : Permet de connaître le niveau actuel de la lave. (DEPRECATED)
- `/tfl setSpeed <nbTicks>` : Permet de définir la vitesse de montée de la lave. (DEPRECATED)
- `/tfl getSpeed` : Permet de connaître la vitesse actuelle de montée de la lave. (DEPRECATED)

#### Gestion de map
- `/tfl map reset map <nom de la map>` : Charge une map personnalisée.
- `/tfl map reset random [seed]` : Génère une map aléatoire.
- `/tfl map preview` : Permet de prévisualiser la map générée en spectateur.
- `/tfl map setCenter [<x> <z>]` : Permet de définir le centre de la map générée.

#### Commandes de configuration
- `/tfl config <section> set <clé> <valeur>` : Permet de modifier un paramètre de configuration.
- `/tfl config <section> get <clé>` : Permet de connaître la valeur actuelle d'un paramètre de configuration.
- `/tfl config <section> gui` : Ouvre l'interface graphique de configuration.
- `/tfl config <section> save` : Sauvegarde les paramètres de configuration dans le fichier correspondant.
- `/tfl config <section> list` : Permet de lister tous les paramètres de configuration disponibles pour une section.

## Configuration

### Paramètres de jeu

Aucune configuration n'est nécessaire pour utiliser le plugin.
Cependant, vous pouvez personnaliser certains aspects du jeu en modifiant les fichiers de configuration situés dans le dossier `plugins/TheFloorIsLava/`.
Fichiers de configuration disponibles :
- `game.yml` : Contient les paramètres relatifs au déroulé d'une partie.
- `danger.yml` : Contient les paramètres relatifs à la montée de la lave.
- `items.yml` : Contient les paramètres relatifs aux objets disponibles dans le jeu.
- `shop.yml` : Contient les paramètres relatifs aux prix de ventes et d'achats des items.
- `gambling.yml` : Contient les paramètres relatifs aux probabilités et gains de l'objet de pari (Voir section [pari](#pari)) (Un outil dédié est disponible pour simuler et configurer ses paramètres (Voir [outils](#outil-de-simulation-de-pari))).
- `defaultMapConfig.yml` : Contient les paramètres relatifs à la configuration par défaut des maps.

Chaque paramètre numérique peut être modifié en jeu via la commande `/tfl config <section> set <valeur>`.
Une interface est également disponible pour configurer les paramètres de manière plus intuitive via la commande `/tfl config <section> gui`.

> [!CAUTION]
> La modification des paramètres n'est effective que jusqu'au prochain redémarrage du serveur. Pour rendre les modifications permanentes, vous devez sauvegarder la configuration via la commande `/tfl config <section> save`, ce qui écrasera le fichier de configuration correspondant.

### paramètres de la boutique
Vous pouvez ajouter des items à l'achat en ajoutant des lignes dans le fichier `shop.yml` avec le format suivant :
```yaml
shop:
  buyable_items:
    vanilla:
    - id: "<nom de l'item en snake case en majuscules>"
      material: <prix en matériaux>
      resource: <prix en ressources>
      quantity: <quantité d'item acheté>
```

Vous pouvez ajouter des items à la vente en ajoutant des lignes dans le fichier `shop.yml` avec le format suivant :
```yaml
shop:
  sellable_items:
  - id: "<nom de l'item en snake case en majuscules>"
    material: <prix en matériaux>
    resource: <prix en ressources>
```

L'interface de modification de paramètres en jeu ne permet pas de ajouter ou de supprimer des items, il est donc nécessaire de modifier le fichier `shop.yml` pour cela.
Vous pouvez néanmoins modifier les prix des items déjà présents via l'interface de configuration en jeu (n'oubliez pas de sauvegarder les modifications avec `/tfl config shop save` pour qu'elles soient permanentes).

### Langue
Le plugin est disponible en plusieurs langues, le choix de la langue se fait automatiquement en fonction de la langue de jeu du joueur.
Si le message de la langue du joueur n'est pas disponible, le plugin utilisera la langue par défaut du serveur (modifiable dans `game.yml`).
Si le message est global et ne peut pas être traduit indépendamment de la langue du joueur, le plugin utilisera la langue par défaut du serveur (modifiable dans `game.yml`).

### Gestion de map de jeu

#### Gestion de map personnalisée

Les maps personnalisées doivent être placées dans le dossier `plugins/TheFloorIsLava/maps/`.

Pour charger une map personnalisée, les opérateurs peuvent utiliser la commande `/tfl map reset map <nom de la map>`.
Les paramètres de la map sont alors chargés depuis le fichier `mapConfig.yml` de la map.
Si ce fichier n'est pas présent, les paramètres par défaut sont utilisés.
L'opérateur peut toujours modifier les paramètres de la map via la commande `/tfl config map set <valeur>` ou via l'interface graphique `/tfl config map gui`.
!! ATTENTION !! Lors du chargement d'une map personnalisée, les paramètres actuellement configurés sont écrasés par ceux définis dans le `mapConfig.yml` de la nouvelle map.
Il est donc recommandé de sauvegarder les paramètres de la map avant de charger une map personnalisée.

#### Gestion de map aléatoire

Pour générer une map aléatoire, les opérateurs peuvent utiliser la commande `/tfl map reset random [seed]`. Si aucune seed n'est fournie, une seed aléatoire sera générée.
Les paramètres de la map sont alors chargés depuis le fichier `defaultMapConfig.yml`. L'opérateur peut toujours modifier les paramètres de la map via la commande `/tfl config map set <valeur>` ou via l'interface graphique `/tfl config map gui`.

Une map générée aléatoirement n'est pas forcément jouable (si par exemple la map est générée dans un océan, il n'y a aucun point de spawn). Il est donc recommandé de vérifier la map avant de lancer une partie avec la commande `/tfl map preview`. Cette commande téléporte l'opérateur sur la map générée en spectateur et lui permet de vérifier si la map est jouable. L'opérateur peut également utiliser la commande `/tfl map setCenter [<x> <z>]` pour modifier le centre de la map si celui-ci n'est pas optimal.
Le centre de jeu est par défaut généré à la position (0, 0) de la map.

## Déroulement d'une partie

### Lobby
Avant le début d'une partie, les joueurs apparaissent dans une dimension dédiée au lobby.
Pendant ce temps, les joueurs peuvent tester les objets disponibles dans le jeu, choisir leur kit et former les équipes.
Les joueurs sont en survie, mais peuvent voler.

### Commencement
Avant de commencer une partie, les opérateurs peuvent charger une map via la commande `/tfl map reset map <nom de la map>`, (Voir la section de [gestion de map custom](#gestion-de-map-personnalisée)) ou générer une map aléatoire via la commande `/tfl map reset random [seed]`, (Voir la section de [génération de map aléatoire](#gestion-de-map-aléatoire)).

Une fois la map chargée, les opérateurs peuvent lancer la partie via la commande `/tfl start`. Un décompte est alors lancé, les joueurs sont téléportés sur la map et la partie commence.

### Préparation

Pendant la phase de préparation, les joueurs ont un temps limité pour se préparer avant que la lave ne commence à monter.
C'est à ce moment que les joueurs doivent récolter des ressources pour acheter des objets, construire des structures pour se protéger de la lave, et se préparer à survivre le plus longtemps possible.

### Montée de la lave

Une fois la phase de préparation terminée, la lave commence à monter progressivement. Les joueurs subissent des dégâts à partir du moment où ils se retrouvent sous le niveau de la lave, même s'ils ne sont pas en contact avec celle-ci.

### Fin de partie

S'il ne reste plus qu'une équipe en vie, la partie se termine et cette équipe est déclarée gagnante. Si tous les joueurs sont morts, la partie se termine sans gagnant.
Pour qu'une équipe soit éliminée, il faut qu'elle ne possède plus d'ancre de réapparition (Voir objets disponibles) et qu'il n'y ait plus de joueurs en vie.

Au bout d'un certain temps, tous les joueurs sont téléportés dans le lobby. Les opérateurs peuvent recommencer une partie en réinitialisant la map (Voir la section de [gestion de map custom](#gestion-de-map-personnalisée)) ou en générant une nouvelle map aléatoire (Voir la section de [gestion de la map de jeu](#gestion-de-map-de-jeu)).

## Boutique

La boutique est accessible via la commande `/shop` ou via l'item de boutique disponible dans le lobby. La boutique permet d'acheter des objets et de vendre des ressources et des matériaux (Voir section [économie](#économie)).

### Objets disponibles achetables

Plusieurs objets sont disponibles ; ils se trouvent dans le lobby et peuvent être achetés avec les ressources récoltées pendant la phase de préparation.
Les opérateurs peuvent se donner les objets via la commande `/tfl give <objet>`.

Tableau des objets disponibles :

| Nom de l'objet                 | Identifiant (pour les obtenir avec la commande ou pour modifier leur traduction) | Description                                                                                                                                                                                                                                                                                                                                                                                                      | Prix Matériaux ([économie](#économie)) | Prix Ressources ([économie](#économie)) |
|--------------------------------|----------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------:|----------------------------------------:|
| Batte de baseball              | `batte`                                                                          | Une batte de baseball enchantée avec recul pour repousser les autres joueurs                                                                                                                                                                                                                                                                                                                                     |                                      0 |                                      45 |
| Ciseaux                        | `shears`                                                                         | Une paire de ciseaux pour couper les blocs de laine plus vite                                                                                                                                                                                                                                                                                                                                                    |                                     10 |                                      20 |
| Pont Oeuf                      | `egg_bridge`                                                                     | Un oeuf qui crée un pont en laine sur son chemin (un peu inspiré de Bed Wars de Hypixel)                                                                                                                                                                                                                                                                                                                         |                                     30 |                                      45 |
| Tour dépliable                 | `popup_tower`                                                                    | Une tour en laine qui apparaît quand on pose le bloc (pareil, un peu inspiré de Bed Wars de Hypixel)                                                                                                                                                                                                                                                                                                             |                                     45 |                                      25 |
| Boule de neige Plateforme      | `snowball_plate`                                                                 | Une plateforme de laine qui apparaît là où la boule de neige atterrit                                                                                                                                                                                                                                                                                                                                            |                                     35 |                                      15 |
| Ancre de réapparition d'équipe | `team_respawn_anchor`                                                            | Un point de réapparition d'équipe, tant qu'une équipe a une ancre de réapparition, l'équipe ne peut pas être éliminée. Une équipe ne peut avoir qu'une seule ancre de réapparition. Si une autre ancre de réapparition est placée, l'ancienne sera détruite. Si l'ancre de réapparition se trouve sous le niveau de la lave, elle sera détruite. Les joueurs adverses peuvent détruire le point de réapparition. |                                    100 |                                     150 |
| Laine Infinie                  | `infinite_wool`                                                                  | Une laine qui peut être posée à l'infini. La laine n'est pas une ressource compliquée, son utilité réside surtout dans la place gagnée dans l'inventaire.                                                                                                                                                                                                                                                        |                                     60 |                                      20 |
| Bottes de chute lente          | `feather_falling_boots`                                                          | Une paire de bottes qui réduit la gravité et les dégâts de chute. Très utile pour se déplacer. C'est un objet peu cher car il est prévu pour être acheté par tout le monde pour avoir les mêmes qualités de déplacement.                                                                                                                                                                                         |                                     10 |                                      35 |
| Boule de feu                   | `fireball`                                                                       | Une boule de feu qui peut être lancée. Elle n'inflige pas de dégâts aux alliés mais inflige un gros recul, pratique pour des déplacements verticaux rapides.                                                                                                                                                                                                                                                     |                                     20 |                                      45 |
| Tnt                            | `tnt`                                                                            | Un bloc de TNT qui s'active directement et qui peut être redirigé en frappant dedans. Le bloc de TNT a une vélocité verticale initiale pour rapidement le frapper dans la direction voulue.                                                                                                                                                                                                                      |                                     25 |                                      35 |
| Parachute                      | `parachute`                                                                      | Un parachute à usage unique qui réinitialise la vitesse du joueur et ralentit sa chute pendant une certaine période.                                                                                                                                                                                                                                                                                             |                                     12 |                                      18 |
| Camp de soin                   | `heal_camp`                                                                      | Un camp de soin temporaire qui régénère la vie des joueurs alliés dans une petite zone, qui peut être détruite par les joueurs adverses. Une équipe peut avoir plusieurs camps de soin. C'est la seule façon de régénérer efficacement en dehors des pommes d'or et des soupes suspectes.                                                                                                                        |                                     40 |                                      90 |
| Pari                           | `gambling`                                                                       | Permet d'ouvrir le menu de pari, usage illimité. Attention à ne pas trop en abuser, vous pouvez rapidement vous ruiner. Pour plus d'informations sur la partie Pari, voir la section [Pari](#pari)                                                                                                                                                                                                               |                                     50 |                                      50 |
| Golem de fer lançable          | `throwable_iron_golem`                                                           | Un golem de fer temporaire qui peut être lancé, le golem attaque tous les joueurs ennemis dans un certain rayon. Le golem subit des dégâts à chaque tick jusqu'à disparaître.                                                                                                                                                                                                                                    |                                     50 |                                      50 |
| Inventaire d'équipe            | `team_inventory`                                                                 | Un inventaire d'équipe portatif. Très pratique pour le partage de ressources. Chaque joueur doit l'acheter s'il veut y avoir accès.                                                                                                                                                                                                                                                                              |                                     80 |                                      70 |

### Ressources et matériaux vendables

Le prix indiqué est le prix à l'unité, le joueur peut vendre plusieurs ressources ou matériaux en même temps.

| Nom de l'item     | Prix Matériaux ([économie](#économie)) | Prix Ressources ([économie](#économie)) |
|-------------------|---------------------------------------:|----------------------------------------:|
| `oak_planks`      |                                      2 |                                         |
| `birch_planks`    |                                      2 |                                         |
| `dark_oak_planks` |                                      2 |                                         |
| `spruce_planks`   |                                      2 |                                         |
| `jungle_planks`   |                                      2 |                                         |
| `acacia_planks`   |                                      2 |                                         |
| `mangrove_planks` |                                      5 |                                         |
| `bamboo_planks`   |                                      5 |                                         |
| `cherry_planks`   |                                      7 |                                         |
| `pale_oak_planks` |                                     10 |                                         |
| `cobblestone`     |                                      1 |                                         |
| `dirt`            |                                        |                                       1 |
| `sand`            |                                        |                                       1 |
| `red_sand`        |                                        |                                       1 |
| `granite`         |                                      3 |                                         |
| `diorite`         |                                      3 |                                         |
| `andesite`        |                                      3 |                                         |
| `coal`            |                                        |                                       2 |
| `copper_ingot`    |                                        |                                       3 |
| `iron_ingot`      |                                        |                                       5 |
| `redstone`        |                                        |                                       2 |
| `lapis_lazuli`    |                                        |                                       3 |
| `gold_ingot`      |                                        |                                       8 |
| `diamond`         |                                        |                                      15 |
| `emerald`         |                                        |                                      50 |

## Économie

L'économie du jeu consiste en 2 devises. Les ressources et les matériaux.

Les ressources sont liées aux minerais, c'est la devise la plus précieuse.
Les matériaux sont liés aux blocs de construction.
Chaque objet a un prix qui consiste en une certaine quantité de ressources et de matériaux.
Plus un objet est lié à la construction, plus il est cher en matériaux.
Plus un objet est lié au combat, plus il est cher en ressources.

Les devises sont individuelles, chaque joueur possède sa propre quantité de ressources et de matériaux.

## Pari

Les probabilités et les gains du pari sont configurables dans le fichier `gambling.yml` et sont disponibles en jeu pour tous les joueurs dans le menu de pari.

Le RTP de base est réglé à 116% (Return to Player), ce qui signifie que pour chaque 100 ressources misées, le joueur peut s'attendre à récupérer en moyenne 116 ressources.
Sur plusieurs paris, les probabilités sont réglées pour que le joueur soit gagnant dans 40% des cas après 100 paris consécutifs en misant 10% de ses ressources à chaque fois.

## Statistiques

Certaines statistiques sont enregistrées pour chaque joueur.

WIP