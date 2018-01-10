package system;

import java.time.Duration;

class AgentInfo {
    final Mailbox mailbox;
    TemporaryKey loginKey = new TemporaryKey("", Duration.ZERO); // initially, expired key
    TemporaryKey sessionKey = new TemporaryKey("", Duration.ZERO); // initially, expired key
    
    AgentInfo(String agentId) {
        this.mailbox = new Mailbox(agentId);
    }
}
