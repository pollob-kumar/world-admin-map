package io.github.pollob_kumar.worldadmin.exception;

public class GeoDataLoadException extends RuntimeException {
    public GeoDataLoadException(String message) {
        super(message);
    }

    public GeoDataLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
