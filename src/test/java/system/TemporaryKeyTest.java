package system;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

public class TemporaryKeyTest {

    private final String keyString = "temporaryKey";

    // default clock used for testing
    private final Clock fixedClock = Clock.fixed(Instant.EPOCH, ZoneId.of("UTC")); // Fixed Clock for independent testing

    private TemporaryKey testTemporaryKey;

    @Before
    public void setUp() {}

    @After
    public void tearDown() {}

    @Test
    public void getKey_nullIfKeyExpired() {

    }

    @Test
    public void getKey_returnsKeyIfNotExpired() {

    }

    @Test
    public void equals_falseIfKeysUnequal() {

    }

    @Test
    public void equals_falseIfKeyExpired() {

    }

    @Test
    public void equals_trueIfKeysEqualAndNotExpired() {

    }
}
