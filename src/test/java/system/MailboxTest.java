package system;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class MailboxTest {

    private static final int MAX_MESSAGES = 25;
    private static final String OWNER_ID = "1234xy", SENDER_ID = "5678vw", MESSAGE = "message";

    private Mailbox testEmptyMailbox;
    private Mailbox testMailboxWith1Message;
    private Queue<Message> messageQueue;

    @Mock
    private Message mockMessage1;
    @Mock
    private Message mockMessage2;

    @Before
    public void setUp() {

        testEmptyMailbox = new Mailbox(OWNER_ID);

        messageQueue = new LinkedBlockingQueue<>(MAX_MESSAGES);
        testMailboxWith1Message = new Mailbox(OWNER_ID, messageQueue);
        messageQueue.add(mockMessage1);

        when(mockMessage1.getSourceAgentId()).thenReturn(SENDER_ID);
        when(mockMessage1.getTargetAgentId()).thenReturn(OWNER_ID);
        when(mockMessage1.getMessage()).thenReturn(MESSAGE);
        when(mockMessage1.isExpired()).thenReturn(false);

        when(mockMessage2.getSourceAgentId()).thenReturn(SENDER_ID);
        when(mockMessage2.getTargetAgentId()).thenReturn(OWNER_ID);
        when(mockMessage2.getMessage()).thenReturn(MESSAGE);
        when(mockMessage2.isExpired()).thenReturn(false);
    }

    @After
    public void tearDown() {
        testEmptyMailbox = null;
        testMailboxWith1Message = null;
        messageQueue = null;
    }

    @Test
    public void consumeNextMessage_notNullIfMailboxHasMessage() {
        Assert.assertNotNull(testMailboxWith1Message.consumeNextMessage());
    }

    @Test
    public void consumeNextMessage_nullIfMailboxIsEmpty() {
        Assert.assertNull(testEmptyMailbox.consumeNextMessage());
    }

    @Test
    public void consumeNextMessage_unsuccessfulIfTimeLimitExceeded() {
        when(mockMessage1.isExpired()).thenReturn(true);

        Assert.assertNull(testMailboxWith1Message.consumeNextMessage());
    }

    @Test
    public void hasMessages_trueIfMailboxHasMessages() {
        Assert.assertTrue(testMailboxWith1Message.hasMessages());
    }

    @Test
    public void hasMessages_falseIfMailboxIsEmpty() {
        Assert.assertFalse(testEmptyMailbox.hasMessages());
    }

    @Test
    public void hasMessages_falseIfTimeLimitExceeded() {
        when(mockMessage1.isExpired()).thenReturn(true);

        Assert.assertFalse(testMailboxWith1Message.hasMessages());
    }

    @Test
    public void addMessage_trueBelowLimit() {

        for (int i = 0; i < MAX_MESSAGES; i++) {
            Assert.assertTrue(testEmptyMailbox.addMessage(mockMessage1));
        }
    }

    @Test
    public void addMessage_trueIfMessagesExpire() {

        for (int i = 0; i < MAX_MESSAGES; i++) {
            Assume.assumeTrue(testEmptyMailbox.addMessage(mockMessage1));
        }
        Assume.assumeFalse(testEmptyMailbox.addMessage(mockMessage1));

        when(mockMessage1.isExpired()).thenReturn(true);

        Assert.assertTrue(testEmptyMailbox.addMessage(mockMessage2));
    }

    @Test
    public void addMessage_falseIfMailboxFull() {

        for (int i = 0; i < MAX_MESSAGES; i++) {
            Assume.assumeTrue(testEmptyMailbox.addMessage(mockMessage1));
        }
        Assert.assertFalse(testEmptyMailbox.addMessage(mockMessage1));
    }

    @Test
    public void addMessage_falseIfMessageTimestampIsTooLongAgo() {
        when(mockMessage1.isExpired()).thenReturn(true);

        Assert.assertFalse(testEmptyMailbox.addMessage(mockMessage1));
    }

    @Test
    public void addMessage_falseIfOwnerIsNotMessageTarget() {
        when(mockMessage1.getTargetAgentId()).thenReturn("AnotherID");

        Assert.assertFalse(testEmptyMailbox.addMessage(mockMessage1));
    }
}