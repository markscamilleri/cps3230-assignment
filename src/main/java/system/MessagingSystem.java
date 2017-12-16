package system;

import java.util.HashMap;
import java.util.Map;

public class MessagingSystem {

    public final static int MAX_MESSAGE_LENGTH = 140;
    public final static String BLOCKED_WORDS[] = {"recipe", "ginger", "nuclear"};
    private static final MessagingSystem INSTANCE = new MessagingSystem();
    private Map<String, Mailbox> mailboxes = new HashMap<>();

    private MessagingSystem() {
    }

    public static MessagingSystem getInstance() {
        return INSTANCE;
    }

    /**
     * Takes a login key and agentId such that when an agent with that Id
     * tries to login she will only be allowed access if the key also matches.
     * <p>
     * This method also checks that the login key is exactly 10 characters long
     * and that the key is unique.
     *
     * @param agentId  The agent ID.
     * @param loginKey The login key.
     * @return true if the checks on the key succeed and the (agent,key) pair has been stored.
     */
    public boolean registerLoginKey(String agentId, String loginKey) {
        return false;
    }

    /**
     * Logs in a user given an agent id and key.
     * Should check that the key is not older than 1 minute.
     * Should check that the login key and agentId match.
     *
     * @param agentId  The agent id
     * @param loginKey The login key
     * @return A randomly generated 50-character session key if login succeeds, null otherwise.
     */
    public String login(String agentId, String loginKey) {
        return "";
    }

    /**
     * Sends a message from the sourceAgent to the targetAgent.
     * Creates a message object and stores it in the target agent's mailbox.
     * <p>
     * Should check that the sourceAgent is the same as the one currently logged in (by matching the session key).
     * Should check that a message does not contain any blocked words.
     * Should check that a message is not longer than 140 characters.
     *
     * @param sessionKey
     * @param sourceAgentId
     * @param targetAgentId
     * @param message
     * @return "OK" if the message is sent, or an appropriate error if not.
     */
    public String sendMessage(String sessionKey, String sourceAgentId, String targetAgentId, String message) {
        return "";
    }
}
