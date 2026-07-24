# The Floor is Lava

Plugin Paper/Minecraft (Java 25, API 1.21) qui transforme un serveur en mini-jeu "sol de lave" : la lave monte progressivement depuis les couches basses jusqu'à la surface, forçant les joueurs à grimper et à survivre en équipe avant d'être rattrapés.

## Sommaire

- [Utilisation](#utilisation)
    - [Déroulement d'une partie](#déroulement-dune-partie)
    - [Commandes](#commandes)
    - [Objets de jeu](#objets-de-jeu)
- [Développement](#développement)

## Utilisation

### Déroulement d'une partie

**1. Phase de lobby**

En dehors d'une partie, le serveur est dans un état de lobby. Les joueurs sont libres de tester tous les items disponibles, de constituer leurs équipes avec `/tfl team` et de choisir leur kit avec `/kit`.

**2. Préparation du monde par les administrateurs**

Avant de lancer la partie, un administrateur prépare le monde de jeu :

- `/tfl map reset map <map_name>` charge une map prédéfinie, ou `/tfl map reset random [seed]` génère un monde aléatoire (la seed est optionnelle, une seed aléatoire est utilisée si elle n'est pas précisée).
- `/tfl map preview` permet de se téléporter en mode spectateur dans la map de jeu sans démarrer la partie. C'est notamment utile après une génération aléatoire, pour repérer le terrain avant de définir le centre de la zone de jeu avec `/tfl map setCenter [x] [z]` (les coordonnées du joueur sont utilisées si elles ne sont pas précisées).
- `/tfl config <map|danger|game> ...` permet de configurer tous les aspects de la partie : dimensions et comportement du monde (`map`), montée de la lave (`danger`), et règles générales de la partie (`game`).

**3. Démarrage et décompte**

`/tfl start` lance la partie. Tous les joueurs sont téléportés dans la dimension de jeu et un décompte démarre. Pendant celui-ci, la partie n'a pas encore réellement commencé.

**4. Phase de préparation**

À la fin du décompte, les joueurs sont dispersés sur la map par équipe et la phase de préparation débute. La lave ne monte pas encore durant cette phase : c'est le moment de s'installer, de récupérer des ressources et de s'organiser en équipe.

**5. Montée de la lave**

Après la phase de préparation, la lave commence à monter progressivement jusqu'à atteindre la couche maximale définie dans la configuration (`danger.end-level`). C'est la phase active du jeu : les équipes doivent prendre de la hauteur et survivre.

**6. Fin de partie**

La fin de partie est détectée automatiquement lorsqu'il ne reste plus qu'une seule équipe en vie.

À noter : il n'existe pas encore de retour automatique au lobby. Une fois la partie terminée, il faut arrêter le jeu, réinitialiser le monde puis relancer une nouvelle partie.

**Interruption manuelle**

`/tfl stop` permet d'arrêter une partie prématurément, en cas de problème. La partie ne peut pas reprendre là où elle s'est arrêtée : il faut utiliser `/tfl start` pour relancer une partie depuis le début, et il est même préférable de réinitialiser le monde au préalable avec `/tfl map reset <map|random>`.

Les commandes `/tfl pause` et `/tfl resume` existent pour suspendre et reprendre la montée de la lave, mais leur fonctionnement n'est pas garanti : elles sont encore en version beta.

### Commandes

Le préfixe `/tfl` regroupe l'ensemble des commandes du plugin. Sauf mention contraire, une commande nécessite les droits d'opérateur (admin).

#### Commandes joueurs (non admin)

| Commande | Description |
|---|---|
| `/tfl team` | Ouvre l'interface de gestion d'équipe (création, invitation, choix de couleur...). Indisponible pendant une partie en cours. |
| `/kit` | Ouvre l'interface de choix de kit. |
| `/kit list` | Liste les kits disponibles. |
| `/kit choose <nom_du_kit>` | Sélectionne un kit (impossible de changer de kit une fois la partie lancée, sauf pour un administrateur). |
| `/kit give` | Redonne le kit actuellement sélectionné au joueur. |
| `/shop` | Ouvre la boutique en jeu pour acheter des objets custom. |

#### Commandes administrateur

**Gestion de la partie**

| Commande | Description |
|---|---|
| `/tfl start` | Démarre la partie (décompte puis dispersion des équipes). |
| `/tfl stop` | Arrête la partie en cours immédiatement. À utiliser en cas de problème ; la partie ne peut pas reprendre ensuite, il faut relancer avec `/tfl start`. |
| `/tfl pause` | Met en pause la montée de la lave (fonctionnalité beta, non garantie). |
| `/tfl resume` | Reprend la montée de la lave après une pause (fonctionnalité beta, non garantie). |
| `/tfl getLevel` | Affiche le niveau (la couche) actuel de la lave. |
| `/tfl setLevel <couche>` | Force le niveau actuel de la lave. |
| `/tfl getSpeed` | Affiche la vitesse de montée actuelle de la lave. |
| `/tfl setSpeed <nbTick>` | Modifie la vitesse de montée de la lave. |
| `/tfl give <item_key>` | Donne un item custom du plugin au joueur exécutant la commande. |

**Gestion de la map et du monde**

| Commande | Description |
|---|---|
| `/tfl map reset map <map_name>` | Réinitialise le monde en chargeant une map prédéfinie. |
| `/tfl map reset random [seed]` | Réinitialise le monde avec une génération aléatoire (seed optionnelle). |
| `/tfl map preview` | Téléporte l'administrateur en mode spectateur dans la map de jeu, sans démarrer de partie. |
| `/tfl map setCenter [x] [z]` | Définit le centre de la zone de jeu (coordonnées du joueur utilisées par défaut). Utile pour recentrer la bordure de jeu après une génération aléatoire. |

**Configuration**

| Commande | Description |
|---|---|
| `/tfl config <map\|danger\|game> list` | Affiche tous les paramètres de la section concernée et leur valeur actuelle. |
| `/tfl config <map\|danger\|game> get <cle>` | Affiche la valeur d'un paramètre précis. |
| `/tfl config <map\|danger\|game> set <cle> <valeur>` | Modifie un paramètre (refusé pendant qu'une partie est en cours). |
| `/tfl config <map\|danger\|game> gui` | Ouvre l'éditeur graphique du groupe de paramètres concerné. |
| `/tfl config <map\|danger\|game> save` | Sauvegarde la configuration courante dans le fichier YAML correspondant. |

Les trois groupes de paramètres :
- **map** : centre de la zone de jeu, génération de la structure de spawn, etc.
- **danger** : niveaux de départ/fin de la lave, durées des phases, dégâts, taille de bordure, etc.
- **game** : délai avant la montée de la lave, PvP et perte d'inventaire en préparation, réduction des dégâts de chute, nombre minimum d'équipes, etc.

**Debug**

| Commande | Description |
|---|---|
| `/tfl debug gameState` | Affiche l'état courant de la partie (`GameState`). |
| `/tfl debug dangerState` | Affiche l'état courant de la montée de la lave. |
| `/tfl debug team` | Liste les équipes et leurs membres. |
| `/tfl debug respawnTeam` | Liste les points de réapparition d'équipe posés en jeu. |
| `/tfl debug kit` | Liste les kits enregistrés, ou détaille un kit précis en argument. |
| `/tfl debug playerKits` | Liste le kit sélectionné par chaque joueur. |
| `/tfl debug playerStats` | Affiche les statistiques suivies pour chaque joueur. |

### Objets de jeu

Le plugin ajoute des objets custom, obtenables via la boutique (`/shop`) ou distribués directement par un administrateur avec `/tfl give <item_key>`. Chacun a une rareté qui reflète son coût/impact en jeu.

| Objet | Clé | Rareté | Craftable | Description |
|---|---|---|---|---|
| Batte | `batte` | Commune | Oui | Bâton enchanté (Recul III) permettant de repousser les autres joueurs, avec durabilité limitée |
| Ciseaux | `ciseaux` | Commune | Oui | Ciseaux enchantés (Efficacité III) pour couper et récupérer les blocs de laine |
| Pont d'œufs | `egg_bridge` | Rare | Oui | Permet de créer un pont temporaire en lançant des œufs |
| Plateforme boule de neige | `snowball_plate` | Rare | Oui | Fait apparaître une plaque de neige au sol pour se rattraper ou bloquer un passage |
| Boule de feu | `fireball` | Rare | Oui | Boule de feu lançable (nécessite le plugin externe ThrowableFireballs) |
| Bottes anti-chute | `feather_falling_boots` | Rare | Oui | Bottes en cuir avec Chute amortie et réduction de gravité pour limiter les dégâts de chute |
| Ancre de réapparition d'équipe | `team_respawn` | Rare | Oui | Posée au sol, elle définit un point de réapparition personnalisé pour l'équipe |
| Tour rétractable | `popupTower` | Épique | Oui | Fait apparaître automatiquement une tour permettant de prendre de la hauteur |
| Portail d'inventaire d'équipe | `teamInv` | Épique | Oui | Coffre enderchest donnant accès à un inventaire partagé entre les membres de l'équipe |
| Gestion des équipes | `team_manager` | Épique | Non | Ouvre l'interface de gestion des équipes |
| Laine infinie | `infinite_wool` | Épique | Non | Bloc de laine qui peut être posé sans jamais être consommé |
| Shop | `shop_item` | Commune | Non | Ouvre le menu de la boutique |
| GiveAll | `give_all` | Commune | Non | Ouvre un menu (réservé aux administrateurs) pour distribuer rapidement des objets |
| TNT | `tnt` | Rare | Pas encore (prévu) | Permet de poser un bloc de TNT qui explose après quelques secondes |
| Parachute | `parachute` | Rare | Pas encore (prévu) | Permet d'éviter les dégâts de chute mortels |
| Camp de soin | `heal_camp` | Légendaire | Pas encore (prévu) | Permet de poser un feu de camp qui soigne les membres de l'équipe présents dans sa zone d'effet |

Les objets non craftables sont obtenus uniquement via la boutique (`/shop`) ou par un administrateur avec `/tfl give <clé>`. Les recettes des objets craftables sont définies dans chaque classe d'item et enregistrées par `TheFloorIsLavaCrafts`. Le TNT, le parachute et le camp de soin sont déjà implémentés en jeu mais n'ont pas encore de recette de craft ; celle-ci est prévue dans une prochaine version.

## Développement

### Structure du projet

```
src/main/java/net/bzkgns/theFloorIsLavaManager/
├── TheFloorIsLavaManager.java      # Classe principale du plugin (onEnable/onDisable)
├── TheFloorIsLavaBootstrap.java    # Bootstrapper Paper (chargement avant le serveur)
├── TheFloorIsLavaCommands.java     # Déclaration de l'arbre de commandes /tfl
├── TheFloorIslavaListener.java     # Écouteurs d'évènements globaux
├── TheFloorIsLavaCrafts.java       # Recettes de craft custom
├── config/                        # Système de configuration générique (clé/valeur, GUI, commandes)
│   ├── danger/                     # Paramètres liés à la montée de la lave
│   ├── game/                       # Paramètres liés au déroulement de partie
│   └── map/                        # Paramètres liés à la map (centre, structure de spawn...)
├── managers/                       # Logique centrale : état de partie, danger, monde, resource pack
├── teams/                          # Gestion des équipes (données, invitations, interface)
├── kits/                           # Gestion des kits (données, choix, application)
├── items/                          # Objets custom et leurs comportements associés
│   ├── team_inventory/              # Inventaire partagé d'équipe
│   ├── team_respawn_anchor/         # Point de réapparition d'équipe
│   └── popup_tower/                  # Tour rétractable
├── shop/                           # Boutique en jeu (interface, recettes, ingrédients)
├── debug/                          # Commandes de debug pour inspecter l'état interne du plugin
└── utils/                         # Fonctions utilitaires diverses
```

Ressources notables (`src/main/resources/`) :

- `paper-plugin.yml` : déclaration du plugin (nom, version, classe principale, API cible).
- `config.yml` : configuration par défaut copiée au premier démarrage.
- `tfl/` : resource pack embarqué (dialogues, données custom).
- `tfl_spawn.nbt` : structure de spawn préconstruite.

### Points clés

- **Séparation danger / partie** : `DangerManager` pilote uniquement la logique de montée de la lave (niveau, vitesse, dégâts), tandis que `GameManager` orchestre l'état global de la partie (`GameState`). Cela évite que les deux logiques se mélangent.
- **Configuration extensible par enum** : `DangerConfigKeys`, `GameConfigKeys` et `MapConfigKeys` centralisent la liste des paramètres exposés (clé YAML, description, getter/setter). Ajouter un nouveau paramètre de config ne nécessite pas de modifier `/tfl config` ni `ConfigGUI`, seulement d'ajouter une entrée dans l'enum correspondant.
- **Ordre d'initialisation dans `onEnable`** : `saveDefaultConfig()` et la création des managers de configuration doivent impérativement précéder toute opération sur `WorldManager`, car `resetRandomWorld()` et `loadMap()` appellent `DangerManager.stop()` en interne. Ce point est documenté directement en commentaire dans `TheFloorIsLavaManager`.
- **Items déclaratifs** : chaque objet custom implémente une interface commune enregistrée dans `ItemManager`, ce qui permet de les distribuer de façon générique via `/tfl give <clé>` sans code spécifique par item.
- **Commandes indépendantes de `/tfl`** : `/kit` et `/shop` sont enregistrées séparément dans `onEnable`, elles ne font pas partie de l'arbre `/tfl`.
- **Build** : le projet utilise Gradle avec les plugins `paperweight-userdev` (dépendances serveur remappées) et `run-paper` (lancement rapide d'un serveur de test via la tâche `runServer`). La compilation cible Java 25.

### Compiler et tester en local

```bash
./gradlew build       # compile le plugin
./gradlew runServer   # lance un serveur Paper de test avec le plugin chargé
```

Le jar compilé se trouve ensuite dans `build/libs/`.

### Seeds utiles pour les tests

- `1087643283499148055` : que de l'eau (test erreur de spread)
- `1784476098874` : plaine sympathique avec quelques collines