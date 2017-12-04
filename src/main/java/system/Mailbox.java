package system;

import java.util.List;

/**
 * This class encapsulates the functionality of a mailbox that holds all messages for a user.
 */
public class Mailbox {

    /**
     * The id of the owner of the mailbox.
     */
    public String ownerId;

    private List<String> messages;

    /**
     * Returns the next message in the box on a FIFO basis.
     *
     * @return A message or null if the mailbox is empty.
     */
    public Message consumeNextMessage() {
        return null;
    }

    /**
     * Checks if there are any messages in the mailbox.
     *
     * @return true if there is at least one message in the mailbox.
     */
    public boolean hasMessages() {
        return false;
    }
}
