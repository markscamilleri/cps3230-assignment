package system;

/**
 * This class encapsulates an Agent that can use the system.
 */
public class Agent {

    private final String id;
    private final String name;
    private final Supervisor supervisor;
    private final MessagingSystem messagingSystem;

    private String loginKey = null;
    private String sessionKey = null;

    public Agent(String id, String name, Supervisor supervisor, MessagingSystem messagingSystem) {
        this.id = id;
        this.name = name;
        this.supervisor = supervisor;
        this.messagingSystem = messagingSystem;
    }

    public Agent(String id, String name, Supervisor supervisor, MessagingSystem messagingSystem, String loginKey, String sessionKey) {
        this(id, name, supervisor, messagingSystem);
        this.loginKey = loginKey;
        this.sessionKey = sessionKey;
    }

    /**
     * Initiates contact with the supervisor to get a login key
     *
     * @return true if login key successfully obtained, false otherwise
     */
    public boolean register() {
        loginKey = supervisor.getLoginKey(id);
        return loginKey != null;
    }

    /**
     * Logs into the system using the previously obtained login key
     *
     * @return true if login successful, false otherwise.
     */
    public boolean login() {
        sessionKey = messagingSystem.login(id, loginKey);
        return sessionKey != null;
    }

    /**
     * Sends a message to the destination agent.
     *
     * @param destinationAgentId The id of the destination agent.
     * @param message            The content of the message.
     * @return true if successful, false otherwise.
     */
    public boolean sendMessage(final String destinationAgentId, final String message) {

        if (sessionKey == null) {
            return false;
        } else {
            final StatusCodes temp = messagingSystem.sendMessage(sessionKey, id, destinationAgentId, message);
            return temp == StatusCodes.OK;
        }
    }

    /**
     * @return the login key that the agent has
     */
    public String getLoginKey() {
        return loginKey;
    }

    /**
     * @return the session key that the agent has
     */
    public String getSessionKey() {
        return sessionKey;
    }
}
