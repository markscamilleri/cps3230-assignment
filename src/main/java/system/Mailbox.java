package system;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This class encapsulates the functionality of a mailbox that holds all messages for a user.
 */
public class Mailbox {

    public final static int MAX_MESSAGES = 25;
    public final static Duration TIME_LIMIT = Duration.ofMinutes(30);

    /**
     * The id of the owner of the mailbox.
     */
    public final String ownerId;

    /**
     * The list of unconsumed messages in the mailbox.
     */
    protected final Queue<Message> messages = new LinkedBlockingQueue<>(MAX_MESSAGES);

    Mailbox(String ownerId) {
        this.ownerId = ownerId;
    }

    /**
     * Returns the next message in the box on a FIFO basis.
     *
     * @return A message or null if the mailbox is empty.
     */
    public synchronized Message consumeNextMessage() {
        Message message;

        while (!Objects.isNull(message = messages.poll())) {
            if (!message.isDeleted())
                return message;
        }

        return null;
    }

    /**
     * Checks if there are any messages in the mailbox.
     *
     * @return true if there is at least one message in the mailbox.
     */
    public synchronized boolean hasMessages() {
        return !messages.isEmpty();
    }

    /**
     * Adds a message to the mailbox.
     *
     * @param message Message to add to mailbox.
     * @return true if successful, false otherwise.
     */
    public synchronized boolean addMessage(Message message) {
        return isValidMessage(message) && messages.offer(message);
    }

    private boolean isValidMessage(Message message) {
        return message.getTargetAgentId().equals(this.ownerId) &&
                Instant.now().isBefore(message.getTimestamp().plus(TIME_LIMIT));
    }
}
