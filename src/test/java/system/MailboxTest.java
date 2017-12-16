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

    private final String OWNER_ID = "1234xy", SENDER_ID = "5678vw", MESSAGE = "message";
    private final Instant TIMESTAMP = Instant.now(); // todo: should not use this

    private class SingleMessageMailbox extends Mailbox {
        SingleMessageMailbox(String ownerId) {
            super(ownerId);
            this.messages.add(mockMessage);
        }
    }

    @Mock
    private Message mockMessage;
    private Mailbox testMailbox;
    private Mailbox testSingleMessageMailbox;

    @Before
    public void setUp() {

        testMailbox = new Mailbox(OWNER_ID);
        testSingleMessageMailbox = new SingleMessageMailbox(OWNER_ID);

        when(mockMessage.getSourceAgentId()).thenReturn(SENDER_ID);
        when(mockMessage.getTargetAgentId()).thenReturn(OWNER_ID);
        when(mockMessage.getMessage()).thenReturn(MESSAGE);
        when(mockMessage.getTimestamp()).thenReturn(TIMESTAMP);
    }

    @After
    public void tearDown() {
        testMailbox = null;
    }

    @Test
    public void consumeNextMessageSuccessfulIfMailboxHasMessage() {
        Assert.assertNotEquals(null, testSingleMessageMailbox.consumeNextMessage());
    }

    @Test
    public void consumeNextMessageUnsuccessfulIfMailboxIsEmpty() {
        Assert.assertEquals(null, testMailbox.consumeNextMessage());
    }

    @Test // todo
    public void consumeNextMessageUnsuccessfulIfTimeLimitExceeded() {
        //Assume.assumeTrue(testMailbox.addMessage(mockMessage));
        // ...30 minutes pass...
        //Assert.assertEquals(null, testMailbox.consumeNextMessage());
    }

    @Test
    public void hasMessagesTrueIfMailboxHasMessages() {
        Assert.assertTrue(testSingleMessageMailbox.hasMessages());
    }

    @Test
    public void hasMessagesFalseIfMailboxIsEmpty() {
        Assert.assertFalse(testMailbox.hasMessages());
    }

    @Test // todo
    public void hasMessagesFalseIfTimeLimitExceeded() {
        //Assume.assumeTrue(testMailbox.addMessage(mockMessage));
        // ...30 minutes pass...
        //Assert.assertFalse(testMailbox.hasMessages());
    }

    @Test
    public void addMessageSuccessfulBelowLimit() {

        for (int i = 0; i < Mailbox.MAX_MESSAGES; i++) {
            Assert.assertTrue(testMailbox.addMessage(mockMessage));
        }
    }

    @Test // todo
    public void addMessageSucessfulIfMessagesExpire() {

        for (int i = 0; i < Mailbox.MAX_MESSAGES; i++) {
            Assume.assumeTrue(testMailbox.addMessage(mockMessage));
        }
        Assume.assumeFalse(testMailbox.addMessage(mockMessage));
        // ...30 minutes pass...
        // Assert.assertTrue(testMailbox.addMessage(mockMessage));
    }

    @Test
    public void addMessageUnsuccessfulIfMailboxFull() {

        for (int i = 0; i < Mailbox.MAX_MESSAGES; i++) {
            Assume.assumeTrue(testMailbox.addMessage(mockMessage));
        }
        Assert.assertFalse(testMailbox.addMessage(mockMessage));
    }

    @Test
    public void addMessageUnsuccessfulIfMessageTimestampIsTooLongAgo() {

        when(mockMessage.getTimestamp()).thenReturn(Instant.EPOCH);
        Assert.assertFalse(testMailbox.addMessage(mockMessage));
    }

    @Test
    public void addMessageUnsuccessfulIfOwnerIsNotMessageTarget() {
        when(mockMessage.getTargetAgentId()).thenReturn("AnotherID");

        Assert.assertFalse(testMailbox.addMessage(mockMessage));
    }
}