package system;

import java.time.Instant;
import java.time.temporal.TemporalAmount;

public class Message {

    private final String sourceAgentId;
    private final String targetAgentId;
    private final String message;
    private final Instant timestamp;

    Message(String sourceAgentId, String targetAgentId, String message) {
        this.sourceAgentId = sourceAgentId;
        this.targetAgentId = targetAgentId;
        this.message = message;
        this.timestamp = Instant.now();
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
