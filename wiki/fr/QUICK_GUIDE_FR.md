<p align="center">
<img src="https://cdn.modrinth.com/data/cached_images/9d3eb8d2fa3cc2c46fb6252af693e34df405f88b.png" alt="drawing" width="350"/>
</p>

The Floor is Lava est un mini-jeu compétitif pour Paper où les équipes doivent survivre pendant que la lave monte progressivement.

Récoltez des ressources, vendez-les contre deux devises, achetez des objets spéciaux dans la boutique et éliminez les autres équipes avant d'être engloutis par la lave.

## Fonctionnalités

* Montée de lave progressive et entièrement configurable.
* Gestion complète des équipes.
* Boutique avec économie basée sur les ressources et les matériaux.
* Kits de départ.
* Maps personnalisées ou génération aléatoire.
* Système de pari configurable.
* Statistiques des joueurs.
* Traduction automatique selon la langue du joueur.

## Installation

1. Téléchargez le plugin.
2. Placez le fichier `.jar` dans le dossier `plugins`.
3. Démarrez votre serveur Paper 1.21.6+ (26.1+).

> **Recommandé :** le plugin TreeFeller permet de couper les arbres plus rapidement et rend les premières minutes de jeu plus dynamiques.

## Démarrage rapide

### 1. Générer une map

```
/tfl map reset random
```

ou charger une map personnalisée

```
/tfl map reset map <nom>
```

### 2. Lancer la partie

```
/tfl start
```

### 3. Jouer

Pendant la phase de préparation :

* récoltez des ressources 
* vendez-les dans la boutique (`/shop`) 
* achetez des objets 
* construisez votre base 
* placez votre ancre de réapparition

Lorsque la lave commence à monter, survivez le plus longtemps possible et éliminez les autres équipes.

La dernière équipe encore en vie remporte la partie.

## Commandes principales

### Joueurs

```
/shop
/tfl team
/kit choose <kit>
```

### Administrateurs

```
/tfl start
/tfl stop
/tfl map reset random
/tfl map reset map <nom>
/tfl map preview
```

Toutes les commandes de configuration sont disponibles dans la documentation complète, sur de dépôt Github.

## Configuration

Le plugin fonctionne sans configuration.

Tous les paramètres (durée de préparation, vitesse de montée de la lave, boutique, objets, probabilités du système de pari, etc.) peuvent être modifiés via des fichiers YAML ou directement en jeu grâce aux commandes `/tfl config`.

## Documentation

Le wiki contient :

* la configuration détaillée 
* la création de maps personnalisées 
* la liste complète des objets 
* les paramètres de l'économie 
* les outils de développement 
* la documentation complète des commandes

## Utilisation de l'IA

L'architecture du projet, les choix de conception, les fonctionnalités, les algorithmes et la logique de jeu ont été imaginés et réalisés par l'auteur du projet. L'IA a été utilisée comme un outil d'assistance, et non comme le concepteur du plugin.

Des outils d'intelligence artificielle ont été utilisés au cours du développement de ce projet pour assister certaines tâches, notamment :

- la correction de fautes d'orthographe et de grammaire 
- la reformulation de textes 
- la traduction 
- l'aide à la rédaction de la documentation 
- l'aide au débogage et à la correction de code 
- l'implémentation de certaines classes isolées (par exemple `ConfigGUI`) 
- l'autocomplétion de code 
- des explications et des réponses à des questions concernant l'API Paper
