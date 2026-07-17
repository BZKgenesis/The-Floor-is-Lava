package net.bzkgns.theFloorIsLavaManager.exception;

public class NoMapFoundException extends WorldGenerationException {
    public NoMapFoundException(String message) {
        super(message);
    }
    public NoMapFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
