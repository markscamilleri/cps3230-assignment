package system;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

public class MessageTest {

    private final String src = "src", trg = "trg", msg = "msg";
    private final Clock fixedClock = Clock.fixed(Instant.EPOCH, ZoneId.of("UTC"));
    private Message message;

    @Before
    public void setUp() {
        message = new Message(src, trg, msg, fixedClock);
    }

    @After
    public void tearDown() {
        message = null;
    }

    @Test
    public void getSourceAgentId_returnsSourceAgentId() {
        Assert.assertEquals(src, message.getSourceAgentId());
    }

    @Test
    public void getTargetAgentId_returnsTargetAgentId() {
        Assert.assertEquals(trg, message.getTargetAgentId());
    }

    @Test
    public void getMessage_returnsMessage() {
        Assert.assertEquals(msg, message.getMessage());
    }
}
