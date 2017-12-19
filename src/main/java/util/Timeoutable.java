package util;

import java.time.Instant;

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
