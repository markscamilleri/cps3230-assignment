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
        this(timeout, Timeout.getInstance());
    }

    protected Timeoutable(Instant timeout, Timeout timeoutHandler) {
        this.timeout = timeout;
        timeoutHandler.register(this);
    }

    public Instant getTimeout() {
        return timeout;
    }

    @Override
    public int compareTo(Timeoutable timeoutable) {
        return this.timeout.compareTo(timeoutable.getTimeout());
    }
    
    /**
     * Flags this object as deleted
     *
     * @return true if successful, false otherwise.
     */
    public boolean delete() {
        this.deleted = true;
        return this.deleted;
    }
    
    /**
     * Returns whether this object is deleted or not
     *
     * @return true if it is flagged as deleted, false otherwise
     */
    public boolean isDeleted() {
        return this.deleted;
    }
}
