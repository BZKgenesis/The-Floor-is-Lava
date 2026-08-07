# The Floor is Lava WIKI - Dev WIKI

## Commandes

#### Commandes de debug
- `/tfl debug dangerState` : Permet de connaître l'état actuel de la montée de la lave.
- `/tfl debug gamble computeRtp` : Permet de calculer le RTP actuel du système de pari.
- `/tfl debug gameState` : Permet de connaître l'état actuel de la partie.
- `/tfl debug kit` : Permet de lister tous les kits disponibles.
- `/tfl debug kit <nom kit>` : Permet de connaître les détails d'un kit spécifique.
- `/tfl debug oreCount <rayon>` : Permet de compter le nombre de minerais dans un rayon autour du joueur. Utile pour régler les paramètres liés à l'économie.
- `/tfl debug playerKits` : Permet de connaître les kits équipés par les joueurs.
- `/tfl debug playerStats` : Permet d'afficher toutes les statistiques des joueurs (c'est plus pour voir si la base de donnée fonctionne correctement que vraiment récupérer des stats).
- `/tfl debug respawnTeam` : Permet de lister les ancres de réapparition d'équipe et leurs positions.
- `/tfl debug team` : Permet de lister toutes les équipes et leurs membres.


## Configuration

### Langue
Le plugin est disponible en plusieurs langues, le choix de la langue se fait automatiquement en fonction de la langue de jeu du joueur.
Si le message de la langue du joueur n'est pas disponible, le plugin utilisera la langue par défaut du serveur (modifiable dans `game.yml`).
Si le message est global et ne peut pas être traduit indépendamment de la langue du joueur, le plugin utilisera la langue par défaut du serveur (modifiable dans `game.yml`).

### Gestion de map de jeu

#### Gestion de map personnalisée

Les maps personnalisées doivent être placées dans le dossier `plugins/TheFloorIsLava/maps/`.
Elles doivent être soit un dossier contenant les fichiers de la map, soit un fichier zip contenant les fichiers de la map.
Les maps doivent suivre la structure de fichier de Minecraft 26.1+.
Structure d'une map personnalisée :
```
maps/
└── <nom de la map>/
    ├── data/
    ├── dimensions/
    │   ├── tfl/
    │   │   └── game/ (Si cette dimension n'est pas trouvée, le plugin utilisera l'overworld par défaut)
    │   └── minecraft/
    │       └── overworld/ (Si tfl:game n'est pas trouvé)
    └── mapConfig.yml (fichier de configuration de la map, si il n'est pas présent, le plugin utilisera la configuration par défaut)
```

Ou alors uniquement la dimension :
```
maps/
└── <nom de la map>/
    ├── data/
    ├── entities/
    ├── poi/
    ├── region/
    └── mapConfig.yml (fichier de configuration de la map, si il n'est pas présent, le plugin utilisera la configuration par défaut)
```

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

## Outils

Des outils sont disponibles pour les développeurs et les opérateurs pour faciliter la configuration et le développement du plugin.
Ces outils sont des scripts Python accessibles sur le dépôt GitHub du plugin.

### Installation

Pour utiliser les outils, il vous faut d'abord cloner le dépôt GitHub.

Déplacez-vous dans le dossier où vous souhaitez cloner le dépôt et exécutez la commande suivante :
```bash
git clone https://github.com/BZKgenesis/The-Floor-is-Lava.git
```

Créez un environnement virtuel Python et activez-le :
```bash
python -m venv .venv
source .venv/bin/activate  # Sur Linux/Mac
.venv\Scripts\activate  # Sur Windows
```

Installez les dépendances nécessaires :
```bash
pip install -r requirements.txt
```

### Utilisation

#### Outil de simulation de pari

Le script de simulation de pari est sous la forme d'un module Python.
Pour ouvrir l'interface graphique, exécutez la commande suivante :
```bash
python -m scripts.gambling_main gui
```

#### Outil de détection de traduction manquante

Le script de détection de traduction manquante scanne tout le projet grâce à des règles regex à la recherche de clés de traduction qui ne seraient pas dans les fichiers de configuration de langue.
Cet outil n'est pas infaillible mais permet de détecter le plus gros des traductions manquantes.

```bash
python -m scripts.lang_scan
```

Le script affiche le nombre de clés de traduction manquantes et les écrit dans un fichier `translation_candidates.txt` dans le dossier `scripts/` du projet.