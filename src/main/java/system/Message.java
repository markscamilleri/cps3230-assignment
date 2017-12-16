package system;

import util.Timeoutable;

import java.time.Clock;
import java.time.Instant;

public class Message extends Timeoutable {

    private final String sourceAgentId;
    private final String targetAgentId;
    private final String message;
    private final Instant timestamp;

    /**
     * Creates a new message
     *
     * @param sourceAgentId Sender of the message
     * @param targetAgentId Receiver of the message
     * @param message       The message contents
     */
    Message(String sourceAgentId, String targetAgentId, String message) {
        this(sourceAgentId, targetAgentId, message, Clock.systemUTC());
    }

    /**
     * Creates a new message
     *
     * @param sourceAgentId Sender of the message
     * @param targetAgentId Receiver of the message
     * @param message       The message contents
     * @param clock         Clock to use for setting the timestamp
     */
    Message(String sourceAgentId, String targetAgentId, String message, Clock clock) {
        super(Instant.now(clock).plus(Mailbox.TIME_LIMIT));
        this.sourceAgentId = sourceAgentId;
        this.targetAgentId = targetAgentId;
        this.message = message;
        this.timestamp = getTimeout().minus(Mailbox.TIME_LIMIT); //To keep the same timestamp
    }

    public String getSourceAgentId() {
        return sourceAgentId;
    }

    public String getTargetAgentId() {
        return targetAgentId;
    }

    public String getMessage() {
        return message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
