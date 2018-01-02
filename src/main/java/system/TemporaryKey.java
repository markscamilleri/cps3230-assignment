package system;

import util.TemporaryObject;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

class TemporaryKey extends TemporaryObject<String> {

    TemporaryKey(String key, Duration timeLimit) {
        this(key, timeLimit, Clock.systemUTC());
    }

    TemporaryKey(String key, Duration timeLimit, Clock clock) {
        super(key, Instant.now(clock).plus(timeLimit), clock);
    }

    public String getKey() {
        return getTempObject();
    }

    public boolean equals(String anotherKey) {
        return !isExpired() && getTempObject().equals(anotherKey);
    }
}
