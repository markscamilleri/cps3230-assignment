package system;

import java.util.ArrayList;
import java.util.List;

/**
 * This class encapsulates the functionality of a mailbox that holds all messages for a user.
 */
public class Mailbox {

    public final static int MAX_MESSAGES = 25;

    /**
     * The id of the owner of the mailbox.
     */
    public String ownerId;

    /**
     * The list of unconsumed messages in the mailbox.
     */
    private List<Message> messages = new ArrayList<>();

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

    /**
     * Adds a message to the mailbox.
     *
     * @param message Message to add to mailbox.
     * @return true if successful, false otherwise.
     */
    public boolean addMessage(Message message) {
        return false;
    }
}
