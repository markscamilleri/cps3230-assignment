package system;

public class Message {

    public final String sourceAgentId;
    public final String targetAgentId;
    public final long timestamp;
    public final String message;

    Message(String sourceAgentId, String targetAgentId, String message) {
        this.sourceAgentId = sourceAgentId;
        this.targetAgentId = targetAgentId;
        this.timestamp = System.currentTimeMillis();
        this.message = message;
    }
}
