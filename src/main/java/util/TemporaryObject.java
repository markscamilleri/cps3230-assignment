package util;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

/**
 * An object that would timeout must extend this class.
 * It provides functionality for timing objects out
 */
public abstract class TemporaryObject<T> {

    private T tempObject;
    private Instant timeout;
    private Clock clock;

    protected TemporaryObject(T tempObject, Instant timeout, Clock clock) {
        this.tempObject = tempObject;
        this.timeout = timeout;
        this.clock = clock;
    }

    public boolean isExpired() {
        return Instant.now(clock).compareTo(timeout) >= 0;
    }

    protected Instant getTimeout() {
        return timeout;
    }

    protected T getTempObject() {
        tempObject = isExpired() ? null : tempObject;
        return tempObject;
    }
}
