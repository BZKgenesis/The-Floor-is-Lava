package net.bzkgns.theFloorIsLavaManager.managers;

/**
 * État global d'une partie. Remplace les booléens épars (hasStarted, isPaused) qui
 * pouvaient auparavant se contredire (ex: hasStarted resté à true après un stop()).
 */
public enum GameState {
    /** Aucune partie en cours : la configuration est éditable, /tfl team fonctionne. */
    LOBBY,
    /** Compte a rebourd avant le début, tout est vérrouiller */
    STARTING,
    /** Une partie est en cours : la configuration est verrouillée, /tfl team est désactivé. */
    RUNNING,
    /** Partie finie, en attente de reset du monde. /tfl team est désactivé. */
    ENDING
}
