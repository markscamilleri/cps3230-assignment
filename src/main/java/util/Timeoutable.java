package util;

import java.time.Instant;

/**
 * An object that would timeout must extend this class.
 * It provides functionality for marking this object as deleted
 *
 * For the timeout functionality, this must be used with a timeout
 * handler
 *
 * @See util.TimeoutContainer
 */
public abstract class Timeoutable implements Comparable<Timeoutable> {
    private Instant timeout;
    
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
