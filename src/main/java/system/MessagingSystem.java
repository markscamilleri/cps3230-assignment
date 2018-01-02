package system;

import util.Utils;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static system.StatusCodes.*;

// todo: add logout functionality

public class MessagingSystem {

    final static Duration LOGIN_KEY_TIME_LIMIT = Duration.ofMinutes(1);
    final static Duration SESSION_KEY_TIME_LIMIT = Duration.ofMinutes(10);

    public final static int LOGIN_KEY_LENGTH = 10;
    public final static int SESSION_KEY_LENGTH = 50;

    final static int MAX_MESSAGE_LENGTH = 140;
    final static String BLOCKED_WORDS[] = {"recipe", "ginger", "nuclear"};

    private final Map<String, AgentInfo> agentInfos;

    public MessagingSystem() {
        this(new HashMap<>());
    }

    MessagingSystem(final Map<String, AgentInfo> agentInfos) {
        this.agentInfos = agentInfos;
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
        final Stream<AgentInfo> agentInfos = this.agentInfos.values().stream();

        // If register is valid, obtain (or create) agent info and set login key
        if (loginKey.length() == LOGIN_KEY_LENGTH && agentInfos.noneMatch(v -> v.loginKey.equals(loginKey))) {
            final AgentInfo info = this.agentInfos.computeIfAbsent(agentId, AgentInfo::new);
            info.loginKey = new TemporaryKey(loginKey, LOGIN_KEY_TIME_LIMIT);
            return true;
        } else {
            return false;
        }
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

        final AgentInfo info = agentInfos.get(agentId);
        if (info != null && info.loginKey.equals(loginKey)) {
            info.sessionKey = new TemporaryKey(Utils.getNRandomCharacters(SESSION_KEY_LENGTH), SESSION_KEY_TIME_LIMIT);
            return info.sessionKey.getKey();
        } else {
            return null;
        }
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
    public StatusCodes sendMessage(String sessionKey, String sourceAgentId, String targetAgentId, String message) {

        final AgentInfo sourceAgentInfo = agentInfos.get(sourceAgentId);
        final AgentInfo targetAgentInfo = agentInfos.get(targetAgentId);

        if (sourceAgentInfo == null || targetAgentInfo == null) {
            return AGENT_DOES_NOT_EXIST;

        } else if (sourceAgentInfo.sessionKey.isExpired()) {
            return AGENT_NOT_LOGGED_IN;

        } else if (!sourceAgentInfo.sessionKey.equals(sessionKey)) {
            return SESSION_KEY_UNRECOGNIZED;

        } else if (message.length() > MAX_MESSAGE_LENGTH) {
            return MESSAGE_LENGTH_EXCEEDED; // todo: send only valid prefix?

        } else {
            // Remove blocked words
            for (final String word : BLOCKED_WORDS) {
                message = message.replaceAll("(?i)" + word + "\\s?", "");
            }

            final Message toSend = new Message(sourceAgentId, targetAgentId, message);
            if (targetAgentInfo.mailbox.addMessage(toSend)) {
                return StatusCodes.OK;
            } else {
                return StatusCodes.FAILED_TO_ADD_TO_MAILBOX;
            }
        }
    }

    /**
     * @return true if the agent is logged in and has messages, false otherwise
     */
    public boolean agentHasMessages(String sessionKey, String agentId) {

        final AgentInfo agentInfo = agentInfos.get(agentId);
        return agentInfo != null
                && !agentInfo.sessionKey.isExpired()
                && agentInfo.sessionKey.equals(sessionKey)
                && agentInfo.mailbox.hasMessages();

    }

    /**
     * Consumes the next message from the agent's mailbox
     *
     * @return the next message if the agent is logged in and has
     * messages, null otherwise
     */
    public Message getNextMessage(String sessionKey, String agentId) {

        final AgentInfo agentInfo = agentInfos.get(agentId);
        if (agentInfo != null
                && !agentInfo.sessionKey.isExpired()
                && agentInfo.sessionKey.equals(sessionKey)) {
            return agentInfo.mailbox.consumeNextMessage();
        } else {
            return null;
        }
    }
}
