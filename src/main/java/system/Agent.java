package system;

public class Agent {

    public String id;
    public String name;
    public Supervisor supervisor;

    /**
     * Initiates contact with a supervisor to get a login key and subsequently logs into the system.
     */
    public boolean login() {
        //final String loginKey = supervisor.getLoginKey(id);
        return false;
    }

    /**
     * Sends a message to the destination agent.
     */
    public boolean sendMessage(final String destinationAgentId, final String message) {
        return false;
    }
    
    
}
