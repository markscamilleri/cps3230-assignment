package system;

public interface Supervisor {

    /**
     * Generates a login key for the given agent.
     */
    public abstract String getLoginKey(final String agentId);
}
