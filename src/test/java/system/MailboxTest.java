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
    public void setUp() throws Exception {

        testMailbox = new Mailbox(OWNER_ID);
        testSingleMessageMailbox = new SingleMessageMailbox(OWNER_ID);

        when(mockMessage.getSourceAgentId()).thenReturn(SENDER_ID);
        when(mockMessage.getTargetAgentId()).thenReturn(OWNER_ID);
        when(mockMessage.getMessage()).thenReturn(MESSAGE);
        when(mockMessage.getTimestamp()).thenReturn(TIMESTAMP);
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

    @Test // todo
    public void consumeNextMessageUnsuccessfulIfTimeLimitExceeded() throws Exception {
        //Assume.assumeTrue(testMailbox.addMessage(mockMessage));
        // ...30 minutes pass...
        //Assert.assertEquals(null, testMailbox.consumeNextMessage());
    }

    @Test
    public void hasMessagesTrueIfMailboxHasMessages() throws Exception {
        Assert.assertTrue(testSingleMessageMailbox.hasMessages());
    }

    @Test
    public void hasMessagesFalseIfMailboxIsEmpty() throws Exception {
        Assert.assertFalse(testMailbox.hasMessages());
    }

    @Test // todo
    public void hasMessagesFalseIfTimeLimitExceeded() throws Exception {
        //Assume.assumeTrue(testMailbox.addMessage(mockMessage));
        // ...30 minutes pass...
        //Assert.assertFalse(testMailbox.hasMessages());
    }

    @Test
    public void addMessageSuccessfulBelowLimit() throws Exception {

        for (int i = 0; i < Mailbox.MAX_MESSAGES; i++) {
            Assert.assertTrue(testMailbox.addMessage(mockMessage));
        }
    }

    @Test // todo
    public void addMessageSucessfulIfMessagesExpire() throws Exception {

        for (int i = 0; i < Mailbox.MAX_MESSAGES; i++) {
            Assume.assumeTrue(testMailbox.addMessage(mockMessage));
        }
        Assume.assumeFalse(testMailbox.addMessage(mockMessage));
        // ...30 minutes pass...
        // Assert.assertTrue(testMailbox.addMessage(mockMessage));
    }

    @Test
    public void addMessageUnsuccessfulIfMailboxFull() throws Exception {

        for (int i = 0; i < Mailbox.MAX_MESSAGES; i++) {
            Assume.assumeTrue(testMailbox.addMessage(mockMessage));
        }
        Assert.assertFalse(testMailbox.addMessage(mockMessage));
    }

    @Test
    public void addMessageUnsuccessfulIfMessageTimestampIsTooLongAgo() throws Exception {

        when(mockMessage.getTimestamp()).thenReturn(Instant.EPOCH);
        Assert.assertFalse(testMailbox.addMessage(mockMessage));
    }

    @Test
    public void addMessageUnsuccessfulIfOwnerIsNotMessageTarget() throws Exception {
        when(mockMessage.getTargetAgentId()).thenReturn("AnotherID");

        Assert.assertFalse(testMailbox.addMessage(mockMessage));
    }
}