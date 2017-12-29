package system;

import util.Timeoutable;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

class TemporaryKey extends Timeoutable {

    public final String key;

    TemporaryKey(String key, Duration timeLimit) {
        this(key, timeLimit, Clock.systemUTC());
    }

    TemporaryKey(String key, Duration timeLimit, Clock clock) {
        super(Instant.now(clock).plus(timeLimit));
        this.key = key;
    }

    public boolean equals(String anotherKey) {
        return this.key.equals(anotherKey);
    }
}
