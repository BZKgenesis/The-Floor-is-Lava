package net.bzkgns.theFloorIsLavaManager.exception;

public class NoLobbyFoundException extends RuntimeException {
    public NoLobbyFoundException(String message) {
        super(message);
    }
    public NoLobbyFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
