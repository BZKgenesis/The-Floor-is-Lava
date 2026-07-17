package net.bzkgns.theFloorIsLavaManager.exception;

public class NoWorldCreatedException extends WorldGenerationException {
    public NoWorldCreatedException(String message) {
        super(message);
    }
    public NoWorldCreatedException(String message, Throwable cause) {
        super(message, cause);
    }
}
