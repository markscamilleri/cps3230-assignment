package system;

import java.time.Instant;

public class Message {

    public final String sourceAgentId;
    public final String targetAgentId;
    public final Instant timestamp;
    public final String message;

    Message(String sourceAgentId, String targetAgentId, String message) {
        this.sourceAgentId = sourceAgentId;
        this.targetAgentId = targetAgentId;
        this.timestamp = Instant.now();
        this.message = message;
    }
}
