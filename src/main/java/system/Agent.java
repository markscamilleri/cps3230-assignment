package system;

/**
 * This class encapsulates an Agent that can use the system.
 */
public class Agent {

    public String id;
    public String name;
    public Supervisor supervisor;
    public MessagingSystem messagingSystem;

    protected String sessionKey = null;

    /**
     * Initiates contact with a supervisor to get a login key and subsequently logs into the system.
     *
     * @return true if login successful, false otherwise.
     */
    public boolean login() {

        final String loginKey = supervisor.getLoginKey(id);
        if (loginKey == null) {
            return false;
        }

        sessionKey = messagingSystem.login(id, loginKey);
        return sessionKey != null;
    }

    /**
     * Sends a message to the destination agent.
     *
     * @param destinationAgentId The id of the destination agent.
     * @param message The content of the message.
     * @return true if successful, false otherwise.
     */
    public boolean sendMessage(final String destinationAgentId, final String message) {

        if (sessionKey == null) {
            return false;
        } else {
            final String temp = messagingSystem.sendMessage(sessionKey, id, destinationAgentId, message);
            return temp.equals(MessagingSystemStatusCodes.OK.getValue());
        }
    }
}
