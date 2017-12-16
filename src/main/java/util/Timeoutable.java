package util;

import java.time.Instant;

/**
 * @author Mark Said Camilleri
 * @version 16/12/17.
 */
public abstract class Timeoutable extends Deletable implements Comparable<Timeoutable> {
    private Instant timeout;

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
}
