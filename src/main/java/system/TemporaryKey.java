package system;

import util.TemporaryObject;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

class TemporaryKey extends TemporaryObject<String> {

    TemporaryKey(String key, Duration timeLimit) {
        super(key, Instant.now().plus(timeLimit), Clock.systemUTC());
    }

    public String getKey() {
        return getTempObject();
    }

    public boolean equals(String anotherKey) {
        return !isExpired() && getTempObject().equals(anotherKey);
    }
}
