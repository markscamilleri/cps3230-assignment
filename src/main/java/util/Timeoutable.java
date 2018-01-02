package util;

import java.time.Clock;
import java.time.Instant;

/**
 * An object that would timeout must extend this class.
 * It provides functionality for timing objects out
 */
public abstract class Timeoutable {
    private Instant timeout;
    private Clock clock;
    
    protected Timeoutable(Instant timeout, Clock clock){
        this.timeout = timeout;
        this.clock = clock;
    }

    public Instant getTimeout() {
        return timeout;
    }

    public boolean isExpired() {
        return Instant.now(clock).isAfter(timeout);
    }
}
