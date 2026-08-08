package net.bzkgns.theFloorIsLava.game;

public class PvpManager {

    private static boolean pvpEnabled = false;

    public static boolean isPvpEnabled() {
        return pvpEnabled;
    }

    public static void setPvpEnabled(boolean enabled) {
        pvpEnabled = enabled;
    }
}
