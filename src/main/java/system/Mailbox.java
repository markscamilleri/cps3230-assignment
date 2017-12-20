package system;

import util.Timeout;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This class encapsulates the functionality of a mailbox that holds all messages for a user.
 */
public class Mailbox extends Timeout{
    
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
        checkAndDelete();
        return messages.poll();
    }
    
    /**
     * Checks if there are any messages in the mailbox.
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
    private boolean isValidMessage(Message message) {
        return message.getTargetAgentId().equals(this.ownerId) &&
                Instant.now().isBefore(message.getTimeout());
    }
    
    @Override
    protected int checkAndDelete() {
        Message message;
        int count = 0;
        
        while (!Objects.isNull(message = messages.peek())) {
            if (isValidMessage(message)) {
                break;
            } else{
                messages.poll();
                count++;
            }
        }
    
        return count;
    }
}
