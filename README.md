# The Floor is Lava

Plugin Paper/Minecraft (Java 25, API 1.21) qui transforme un serveur en mini-jeu "sol de lave" : la lave monte progressivement depuis les couches basses jusqu'à la surface, forçant les joueurs à grimper et à survivre en équipe avant d'être rattrapés.

## Sommaire

- [Utilisation](#utilisation)
- [Développement](#développement)

## Utilisation

### Déroulement d'une partie

Le jeu passe par plusieurs états gérés par `GameManager` (`LOBBY`, `STARTING`, `RUNNING`, `ENDING`) :

1. **Lobby** : les joueurs rejoignent des équipes via `/tfl team` (interface graphique).
2. **Démarrage** : `/tfl start` lance la partie. Une phase de préparation s'ouvre (PvP et perte d'inventaire désactivables via la configuration).
3. **Montée de la lave** : la lave progresse depuis `start-level` jusqu'à `end-level`, avec un changement de vitesse au passage de `surface-level`. Des dégâts périodiques sont infligés aux joueurs restés trop bas.
4. **Fin de partie** : `/tfl stop` arrête tout, et le monde peut être régénéré avant la partie suivante.

### Commandes principales

Toutes les commandes sont regroupées sous `/tfl` (réservées aux opérateurs sauf mention contraire) :

| Commande | Description |
|---|---|
| `/tfl start` | Démarre la partie |
| `/tfl stop` | Arrête la partie en cours |
| `/tfl pause` / `/tfl resume` | Met en pause / reprend la montée de la lave |
| `/tfl getLevel` / `/tfl setLevel <couche>` | Consulte ou force le niveau actuel de la lave |
| `/tfl getSpeed` / `/tfl setSpeed <nbTick>` | Consulte ou modifie la vitesse de montée |
| `/tfl config` | Ouvre l'édition des paramètres (danger / partie) |
| `/tfl give <item>` | Donne un item spécifique du plugin |
| `/tfl resetWorld random [seed]` | Régénère le monde de jeu aléatoirement |
| `/tfl resetWorld map <nom>` | Charge une map prédéfinie |
| `/tfl team` | Ouvre l'interface de gestion d'équipe (accessible à tous, hors partie) |
| `/tfl debug respawnTeam` / `/tfl debug team` | Affiche des informations de debug |
| `/shop` | Ouvre la boutique en jeu |

### Configuration

Le fichier `src/main/resources/config.yml` contient deux sections principales :

- **danger** : niveaux de départ/fin de la lave, durées des phases, dégâts, taille des bordures, etc.
- **game** : paramètres liés au déroulement (réduction de dégâts de chute, PvP en préparation, etc.)

Ces valeurs peuvent aussi être modifiées à chaud via `/tfl config` ou l'interface `ConfigGUI`, sans redémarrer le serveur.

### Objets de jeu

Le plugin ajoute des objets custom, obtenables via la boutique (`/shop`) ou distribués directement par un opérateur avec `/tfl give <clé>`. Chacun a une rareté qui reflète son coût/impact en jeu.

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
| GiveAll | `give_all` | Commune | Non | Ouvre un menu (réservé aux opérateurs) pour distribuer rapidement des objets |

Les objets non craftables sont obtenus uniquement via la boutique (`/shop`) ou par un opérateur avec `/tfl give <clé>`. Les recettes des objets craftables sont définies dans chaque classe d'item et enregistrées par `TheFloorIsLavaCrafts`.

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
│   └── game/                       # Paramètres liés au déroulement de partie
├── managers/                       # Logique centrale : état de partie, danger, monde, resource pack
├── teams/                          # Gestion des équipes (données, invitations, interface)
├── items/                          # Objets custom et leurs comportements associés
│   ├── team_inventory/              # Inventaire partagé d'équipe
│   ├── team_respawn_anchor/         # Point de réapparition d'équipe
│   └── popup_tower/                  # Tour rétractable
├── shop/                           # Boutique en jeu (interface, recettes, ingrédients)
└── utils/                         # Fonctions utilitaires diverses
```

Ressources notables (`src/main/resources/`) :

- `paper-plugin.yml` : déclaration du plugin (nom, version, classe principale, API cible).
- `config.yml` : configuration par défaut copiée au premier démarrage.
- `tfl/` : resource pack embarqué (dialogues, données custom).
- `tfl_spawn.nbt` : structure de spawn préconstruite.

### Points clés

- **Séparation danger / partie** : `DangerManager` pilote uniquement la logique de montée de la lave (niveau, vitesse, dégâts), tandis que `GameManager` orchestre l'état global de la partie (`GameState`). Cela évite que les deux logiques se mélangent.
- **Configuration extensible par enum** : `DangerConfigKey` et `GameConfigKey` centralisent la liste des paramètres exposés (clé YAML, description, getter/setter). Ajouter un nouveau paramètre de config ne nécessite pas de modifier `/tfl config` ni `ConfigGUI`, seulement d'ajouter une entrée dans l'enum correspondant.
- **Ordre d'initialisation dans `onEnable`** : `saveDefaultConfig()` et la création des managers de configuration doivent impérativement précéder toute opération sur `WorldManager`, car `resetRandomWorld()` et `loadMap()` appellent `DangerManager.stop()` en interne. Ce point est documenté directement en commentaire dans `TheFloorIsLavaManager`.
- **Items déclaratifs** : chaque objet custom implémente une interface commune enregistrée dans `ItemManager`, ce qui permet de les distribuer de façon générique via `/tfl give <clé>` sans code spécifique par item.
- **Build** : le projet utilise Gradle avec les plugins `paperweight-userdev` (dépendances serveur remappées) et `run-paper` (lancement rapide d'un serveur de test via la tâche `runServer`). La compilation cible Java 25.

### Compiler et tester en local

```bash
./gradlew build       # compile le plugin
./gradlew runServer   # lance un serveur Paper de test avec le plugin chargé
```

Le jar compilé se trouve ensuite dans `build/libs/`.

# Utils:
seeds:
- `1087643283499148055` : que de l'eau (test erreur de spread)
- `1784476098874` : plaine sympathique avec quelques collines