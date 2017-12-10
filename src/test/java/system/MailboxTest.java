package system;

import org.junit.*;

// todo: consumeMessage fails if single message was deleted

public class MailboxTest {

    private final String OWNER_ID = "1234xy";
    private final String SENDER_ID = "5678vw";
    private final Message DUMMY_MESSAGE = new Message("DUMMY_MESSAGE", SENDER_ID, OWNER_ID);
    private Mailbox testMailbox;
    private Mailbox testSingleMessageMailbox;

    /*public MailboxTest() {
        DUMMY_MESSAGE.message = "DUMMY_MESSAGE";
        DUMMY_MESSAGE.sourceAgentId = SENDER_ID;
        DUMMY_MESSAGE.targetAgentId = OWNER_ID;
        DUMMY_MESSAGE.timestamp = System.currentTimeMillis();
    }*/

    private class SingleMessageMailbox extends Mailbox {
        SingleMessageMailbox(String ownerId) {
            super(ownerId);
            this.messages.add(DUMMY_MESSAGE);
        }
    }

    @Before
    public void setUp() throws Exception {

        testMailbox = new Mailbox(OWNER_ID);
        testSingleMessageMailbox = new SingleMessageMailbox(OWNER_ID);
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
        // todo: 30 minutes elapse
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
        // todo: 30 minutes elapse
        Assert.assertFalse(testMailbox.hasMessages());
    }

    @Test
    public void addMessageSuccessfulBelowLimit() throws Exception {

        for (int i = 0; i < Mailbox.MAX_MESSAGES; i++) {
            Assert.assertTrue(testMailbox.addMessage(DUMMY_MESSAGE));
        }
    }

    @Test
    public void addMessageUnsuccessfulIfMailboxFull() throws Exception {

        for (int i = 0; i < Mailbox.MAX_MESSAGES; i++) {
            Assume.assumeTrue(testMailbox.addMessage(DUMMY_MESSAGE));
        }
        Assert.assertFalse(testMailbox.addMessage(DUMMY_MESSAGE));
    }
}