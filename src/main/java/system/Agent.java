package system;

/**
 * This class encapsulates an Agent that can use the system.
 */
public class Agent {

    public String id;
    public String name;
    public Supervisor supervisor;

    /**
     * Initiates contact with a supervisor to get a login key and subsequently logs into the system.
     *
     * @return true if login successful, false otherwise.
     */
    public boolean login() {
        //final String loginKey = supervisor.getLoginKey(id);
        return false;
    }

    /**
     * Sends a message to the destination agent.
     *
     * @param destinationAgentId The id of the destination agent.
     * @param message The content of the message.
     * @return true if successful, false otherwise.
     */
    public boolean sendMessage(final String destinationAgentId, final String message) {
        return false;
    }
    
    
}
