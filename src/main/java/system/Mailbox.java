package system;

import java.time.Duration;
import java.time.Instant;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This class encapsulates the functionality of a mailbox that holds all container for a user.
 */
public class Mailbox {
    
    public final static int MAX_MESSAGES = 25;
    public final static Duration TIME_LIMIT = Duration.ofMinutes(30);

    private final Queue<Message> messages = new LinkedBlockingQueue<>(MAX_MESSAGES);

    /**
     * The id of the owner of the mailbox.
     */
    public final String ownerId;
    
    Mailbox(String ownerId) {
        this.ownerId = ownerId;
    }
    
    /**
     * Returns the next message in the box on a FIFO basis.
     *
     * @return A message or null if the mailbox is empty.
     */
    public synchronized Message consumeNextMessage() {
        checkAndDelete();
        return messages.poll();
    }
    
    /**
     * Checks if there are any container in the mailbox.
     *
     * @return true if there is at least one message in the mailbox.
     */
    public synchronized boolean hasMessages() {
        checkAndDelete();
        return !messages.isEmpty();
    }
    
    /**
     * Adds a message to the mailbox.
     *
     * @param message Message to add to mailbox.
     * @return true if successful, false otherwise.
     */
    public synchronized boolean addMessage(Message message) {
        checkAndDelete();
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
     * This must be called before any operation on the data structure
     * storing timeoutable objects
     *
     * @return the number of objects that timed out.
     */
    protected int checkAndDelete() {
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
