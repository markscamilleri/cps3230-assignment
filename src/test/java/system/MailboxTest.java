package system;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Duration;
import java.time.Instant;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.mockito.Mockito.when;
import static system.Mailbox.MAX_MESSAGES;

// todo: consumeMessage fails if single message was deleted

@RunWith(MockitoJUnitRunner.Silent.class)
public class MailboxTest {

    private final String OWNER_ID = "1234xy", SENDER_ID = "5678vw", MESSAGE = "message";
    private final Instant TIMESTAMP = Instant.now(); // todo: should not use this
    private Mailbox testEmptyMailbox;
    private Mailbox testMailboxWith1Message;
    private Queue<Message> messageQueue;

    @Mock
    private Message mockMessage;

    @Before
    public void setUp() {

        testEmptyMailbox = new Mailbox(OWNER_ID);

        messageQueue = new LinkedBlockingQueue<>(MAX_MESSAGES);
        testMailboxWith1Message = new Mailbox(OWNER_ID, messageQueue);
        messageQueue.add(mockMessage);

        // todo: make sure all of this is correct and necessary
        when(mockMessage.getSourceAgentId()).thenReturn(SENDER_ID);
        when(mockMessage.getTargetAgentId()).thenReturn(OWNER_ID);
        when(mockMessage.getMessage()).thenReturn(MESSAGE);
        when(mockMessage.getTimestamp()).thenReturn(TIMESTAMP);
        when(mockMessage.getTimeout()).thenReturn(TIMESTAMP.plus(Duration.ofMinutes(30)));
    }

    @After
    public void tearDown() {
        testEmptyMailbox = null;
        testMailboxWith1Message = null;
        messageQueue = null;
    }

    @Test
    public void consumeNextMessage_notNullIfMailboxHasMessage() {
        Assert.assertNotEquals(null, testMailboxWith1Message.consumeNextMessage());
    }

    @Test
    public void consumeNextMessage_nullIfMailboxIsEmpty() {
        Assert.assertEquals(null, testEmptyMailbox.consumeNextMessage());
    }

    @Test // todo
    public void consumeNextMessage_unsuccessfulIfTimeLimitExceeded() {
        //Assume.assumeTrue(testEmptyMailbox.addMessage(mockMessage));
        // ...30 minutes pass...
        //Assert.assertEquals(null, testEmptyMailbox.consumeNextMessage());
    }

    @Test
    public void hasMessages_trueIfMailboxHasMessages() {
        Assert.assertTrue(testMailboxWith1Message.hasMessages());
    }

    @Test
    public void hasMessages_falseIfMailboxIsEmpty() {
        Assert.assertFalse(testEmptyMailbox.hasMessages());
    }

    @Test // todo
    public void hasMessages_falseIfTimeLimitExceeded() {
        //Assume.assumeTrue(testEmptyMailbox.addMessage(mockMessage));
        // ...30 minutes pass...
        //Assert.assertFalse(testEmptyMailbox.hasMessages());
    }

    @Test
    public void addMessage_trueBelowLimit() {

        for (int i = 0; i < MAX_MESSAGES; i++) {
            Assert.assertTrue(testEmptyMailbox.addMessage(mockMessage));
        }
    }

    @Test // todo
    public void addMessage_trueIfMessagesExpire() {

        for (int i = 0; i < MAX_MESSAGES; i++) {
            Assume.assumeTrue(testEmptyMailbox.addMessage(mockMessage));
        }
        Assume.assumeFalse(testEmptyMailbox.addMessage(mockMessage));
        // ...30 minutes pass...
        // Assert.assertTrue(testEmptyMailbox.addMessage(mockMessage));
    }

    @Test
    public void addMessage_falseIfMailboxFull() {

        for (int i = 0; i < MAX_MESSAGES; i++) {
            Assume.assumeTrue(testEmptyMailbox.addMessage(mockMessage));
        }
        Assert.assertFalse(testEmptyMailbox.addMessage(mockMessage));
    }

    @Test
    public void addMessage_falseIfMessageTimestampIsTooLongAgo() {

        when(mockMessage.getTimestamp()).thenReturn(Instant.EPOCH);
        when(mockMessage.getTimeout()).thenReturn(Instant.EPOCH.plus(Duration.ofMinutes(30)));

        Assert.assertFalse(testEmptyMailbox.addMessage(mockMessage));
    }

    @Test
    public void addMessage_falseIfOwnerIsNotMessageTarget() {
        when(mockMessage.getTargetAgentId()).thenReturn("AnotherID");

        Assert.assertFalse(testEmptyMailbox.addMessage(mockMessage));
    }
}