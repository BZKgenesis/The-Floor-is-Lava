package net.bzkgns.theFloorIsLavaManager;

/**
 * État global d'une partie. Remplace les booléens épars (hasStarted, isPaused) qui
 * pouvaient auparavant se contredire (ex: hasStarted resté à true après un stop()).
 */
public enum GameState {
    /** Aucune partie en cours : la configuration est éditable, /tfl team fonctionne. */
    LOBBY,
    /** Compte à rebours avant la montée de la lave (spreadplayers, inventaires donnés...). */
    PREPARING,
    /** La lave monte : dégâts, pose de lave et rétrécissement de bordure actifs. */
    RISING,
    /** Partie mise en pause manuellement (voir DangerManager#pause / #resume). */
    PAUSED
}
