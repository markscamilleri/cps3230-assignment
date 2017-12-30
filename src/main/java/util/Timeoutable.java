package util;

import java.time.Instant;

/**
 * An object that would timeout must extend this class.
 * It provides functionality for timing objects out
 */
public abstract class Timeoutable {
    private final Instant timeout;

    protected Timeoutable(Instant timeout) {
        this.timeout = timeout;
    }

    public Instant getTimeout() {
        return timeout;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(timeout);
    }
}
