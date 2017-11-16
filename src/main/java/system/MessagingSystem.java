package system;

public class MessagingSystem {

    /**
     * Takes a login key and agentId such that when an agent with that Id
     * tries to login she will only be allowed access if the key also matches.
     */
    public boolean registerLoginKey(String loginKey, String agentId) {
        return false;
    }

    /**
     * Logs in a user given an agent id and key.
     */
    public String login(String agentId, String loginKey) {
        return "";
    }

    /**
     * Sends a message from the sourceAgent to the targetAgent.
     */
    public String sendMessage(String sessionKey, String sourceAgentId, String targetAgentId, String message) {
        return "";
    }
}
