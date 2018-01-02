package system;

class AgentInfo {

    final Mailbox mailbox;
    TemporaryKey loginKey = null;
    TemporaryKey sessionKey = null;

    AgentInfo(String agentId) {
        this.mailbox = new Mailbox(agentId);
    }

    // todo: add isRegistered and isLoggedIn?
}
