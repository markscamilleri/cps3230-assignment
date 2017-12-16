package system;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;

public class MessageTest {

    private Message message;
    private final String src = "src", trg = "trg", msg = "msg";

    @Before
    public void setUp() throws Exception {
        message = new Message(src, trg, msg);
    }

    @After
    public void tearDown() throws Exception {
        message = null;
    }

    @Test
    public void constructorSetsFieldsCorrectly() throws Exception {

        Assert.assertEquals(src, message.getSourceAgentId());
        Assert.assertEquals(trg, message.getTargetAgentId());
        Assert.assertEquals(msg, message.getMessage());

        // Check that timestamp is within 3 seconds of current time
        final Duration marginOfError = Duration.ofSeconds(3);
        final Instant recent = Instant.now().minus(marginOfError);
        Assert.assertTrue(message.getTimestamp().isAfter(recent));
    }
}
