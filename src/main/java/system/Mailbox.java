package system;

import java.time.Duration;
import java.time.Instant;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This class encapsulates the functionality of a mailbox that holds all container for a user.
 */
public class Mailbox {
    
    final static int MAX_MESSAGES = 25;
    final static Duration MESSAGE_TIME_LIMIT = Duration.ofMinutes(30);

    private final Queue<Message> messages;

    /**
     * The id of the owner of the mailbox.
     */
    private final String ownerId;
    
    Mailbox(String ownerId) {
        this(ownerId, new LinkedBlockingQueue<>(MAX_MESSAGES));
    }

    Mailbox(String ownerId, Queue<Message> messages) {
        this.ownerId = ownerId;
        this.messages = messages;
    }
    
    /**
     * Returns the next message in the box on a FIFO basis.
     *
     * @return A message or null if the mailbox is empty.
     */
    public synchronized Message consumeNextMessage() {
        deleteExpiredMessages();
        return messages.poll();
    }
    
    /**
     * Checks if there are any container in the mailbox.
     *
     * @return true if there is at least one message in the mailbox.
     */
    public synchronized boolean hasMessages() {
        deleteExpiredMessages();
        return !messages.isEmpty();
    }
    
    /**
     * Adds a message to the mailbox.
     *
     * @param message Message to add to mailbox.
     * @return true if successful, false otherwise.
     */
    public synchronized boolean addMessage(Message message) {
        deleteExpiredMessages();
        return isValidMessage(message) && messages.offer(message);
    }

    /**
     * Checks if the message is valid
     * @param message the message to check
     * @return true if it is valid, false otherwise.
     */
    private boolean isValidMessage(Message  message) {
        return message.getTargetAgentId().equals(this.ownerId) &&
                Instant.now().isBefore(message.getTimeout());
    }

    /**
     * Checks if there are any objects that timed out.
     * This must be called before any operation on messages
     *
     * @return the number of objects that timed out.
     */
    private int deleteExpiredMessages() {
        int count = 0;
        for (Message msg : messages) {
            if (msg.isExpired()){
                messages.remove(msg);
                count++;
            }
        }

        return count;

    }
}
