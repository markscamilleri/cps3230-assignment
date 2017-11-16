package system;

import java.util.List;

public class Mailbox {

    public String ownerId;
    private List<String> messages;

    /**
     * Returns the next message in the box on a FIFO basis.
     */
    public Message consumeNextMessage() {
        return null;
    }

    /**
     * Checks if there are any messages in the mailbox.
     */
    public boolean hasMessages() {
        return false;
    }
}
