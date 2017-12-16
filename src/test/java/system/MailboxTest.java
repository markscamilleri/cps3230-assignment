package system;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Duration;
import java.time.Instant;

import static org.mockito.Mockito.when;

// todo: consumeMessage fails if single message was deleted

@RunWith(MockitoJUnitRunner.Silent.class)
public class MailboxTest {

    private final String OWNER_ID = "1234xy";
    private final String SENDER_ID = "5678vw";
    private final Duration TIME_MARGIN = Duration.ofSeconds(2);
    @Mock
    private Message mockMessage;
    private Mailbox testMailbox;
    private Mailbox testSingleMessageMailbox;

    @Before
    public void setUp() throws Exception {

        testMailbox = new Mailbox(OWNER_ID);
        testSingleMessageMailbox = new SingleMessageMailbox(OWNER_ID);

        when(mockMessage.getSourceAgentId()).thenReturn(SENDER_ID);
        when(mockMessage.getTargetAgentId()).thenReturn(OWNER_ID);
        when(mockMessage.getMessage()).thenReturn("MOCK_MESSAGE");
        when(mockMessage.getTimestamp()).thenReturn(Instant.now());
    }

    @After
    public void tearDown() throws Exception {
        testMailbox = null;
    }

    @Test
    public void consumeNextMessageSuccessfulIfMailboxHasMessage() throws Exception {
        Assert.assertNotEquals(null, testSingleMessageMailbox.consumeNextMessage());
    }

    @Test
    public void consumeNextMessageUnsuccessfulIfMailboxIsEmpty() throws Exception {
        Assert.assertEquals(null, testMailbox.consumeNextMessage());
    }

    @Test
    public void consumeNextMessageUnsuccessfulIfTimeLimitExceeded() throws Exception {
        final Duration lessThanThirtyMinutes = Mailbox.TIME_LIMIT.minus(TIME_MARGIN);
        when(mockMessage.getTimestamp()).thenReturn(Instant.now().minus(lessThanThirtyMinutes));
        Assume.assumeTrue(testMailbox.addMessage(mockMessage));

        Thread.sleep(TIME_MARGIN.toMillis());

        Assert.assertEquals(null, testMailbox.consumeNextMessage());
    }

    @Test
    public void hasMessagesTrueIfMailboxHasMessages() throws Exception {
        Assert.assertTrue(testSingleMessageMailbox.hasMessages());
    }

    @Test
    public void hasMessagesFalseIfMailboxIsEmpty() throws Exception {
        Assert.assertFalse(testMailbox.hasMessages());
    }

    @Test
    public void hasMessagesFalseIfTimeLimitExceeded() throws Exception {
        final Duration lessThanThirtyMinutes = Mailbox.TIME_LIMIT.minus(TIME_MARGIN);
        when(mockMessage.getTimestamp()).thenReturn(Instant.now().minus(lessThanThirtyMinutes));
        Assume.assumeTrue(testMailbox.addMessage(mockMessage));

        Thread.sleep(TIME_MARGIN.toMillis());

        Assert.assertFalse(testMailbox.hasMessages());
    }

    @Test
    public void addMessageSuccessfulBelowLimit() throws Exception {

        for (int i = 0; i < Mailbox.MAX_MESSAGES; i++) {
            Assert.assertTrue(testMailbox.addMessage(mockMessage));
        }
    }

    @Test
    public void addMessageUnsuccessfulIfMailboxFull() throws Exception {

        for (int i = 0; i < Mailbox.MAX_MESSAGES; i++) {
            Assume.assumeTrue(testMailbox.addMessage(mockMessage));
        }
        Assert.assertFalse(testMailbox.addMessage(mockMessage));
    }

    @Test
    public void addMessageUnsuccessfulIfMessageTimeLimitExceeded() throws Exception {
        final Instant thirtyMinutesAgo = Instant.now().minus(Mailbox.TIME_LIMIT);
        when(mockMessage.getTimestamp()).thenReturn(thirtyMinutesAgo);

        Assert.assertFalse(testMailbox.addMessage(mockMessage));
    }

    @Test
    public void addMessageUnsuccessfulIfOwnerIsNotMessageTarget() throws Exception {
        when(mockMessage.getTargetAgentId()).thenReturn("AnotherID");

        Assert.assertFalse(testMailbox.addMessage(mockMessage));
    }

    private class SingleMessageMailbox extends Mailbox {
        SingleMessageMailbox(String ownerId) {
            super(ownerId);
            this.messages.add(mockMessage);
        }
    }
}