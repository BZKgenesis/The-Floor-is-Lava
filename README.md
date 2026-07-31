# The Floor is Lava

## WIKI
Pour acceder à la documentation complète du plugin, rendez-vous sur le [wiki](wiki/HOME.md).

## Développement

### Structure du projet

```
src/main/java/net/bzkgns/theFloorIsLava/
├── TheFloorIsLava.java      # Classe principale du plugin (onEnable/onDisable)
├── TheFloorIsLavaBootstrap.java    # Bootstrapper Paper (chargement avant le serveur)
├── TheFloorIsLavaCommands.java     # Déclaration de l'arbre de commandes /tfl
├── TheFloorIslavaListener.java     # Écouteurs d'évènements globaux
├── TheFloorIsLavaCrafts.java       # Recettes de craft custom
├── config/                        # Système de configuration générique (clé/valeur, GUI, commandes)
│   ├── danger/                     # Paramètres liés à la montée de la lave
│   ├── gambling/                   # Paramètres liés aux paris
│   ├── items/                      # Paramètres liés aux items personnalisés
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

### Compiler et tester en local

```bash
./gradlew build       # compile le plugin
./gradlew runServer   # lance un serveur Paper de test avec le plugin chargé
```

Le jar compilé se trouve ensuite dans `build/libs/`.

### Outils
```bash
python -m scripts.lang_scan
```

```bash
python -m scripts.gambling_main
```