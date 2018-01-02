package system;

import util.Timeoutable;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

class TemporaryKey extends Timeoutable {

    // Null when timed out
    private String key;

    TemporaryKey(String key, Duration timeLimit) {
        this(key, timeLimit, Clock.systemUTC());
    }

    TemporaryKey(String key, Duration timeLimit, Clock clock) {
        super(Instant.now(clock).plus(timeLimit));
    
        this.key = isExpired()? null : key;
    }
    
    public String getKey() {
        key = isExpired()? null : key;
    
        return key;
    }
    
    public boolean equals(String anotherKey) {
        key = isExpired()? null : key;
        
        return this.key.equals(anotherKey);
    }
}
