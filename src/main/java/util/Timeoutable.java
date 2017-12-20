package util;

import java.time.Instant;

/**
 * An object that would timeout must extend this class.
 * It provides functionality for marking this object as deleted
 *
 * For the timeout functionality, this must be used with a timeout
 * handler
 *
 * @See util.Timeout
 */
public abstract class Timeoutable implements Comparable<Timeoutable> {
    private Instant timeout;
    private boolean deleted = false;
    
    protected Timeoutable(Instant timeout) {
        this.timeout = timeout;
    }

    public Instant getTimeout() {
        return timeout;
    }

    @Override
    public int compareTo(Timeoutable timeoutable) {
        return this.timeout.compareTo(timeoutable.getTimeout());
    }
}
