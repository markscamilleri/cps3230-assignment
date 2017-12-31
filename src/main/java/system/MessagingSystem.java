package system;

import util.Utils;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static system.StatusCodes.*;

public class MessagingSystem {

    final static Duration LOGIN_KEY_TIME_LIMIT = Duration.ofMinutes(1);
    final static Duration SESSION_KEY_TIME_LIMIT = Duration.ofMinutes(10);

    final static int LOGIN_KEY_LENGTH = 10;
    final static int SESSION_KEY_LENGTH = 50;

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
        deleteExpiredKeys();

        if (isValidRegister(loginKey)) {

            // Obtain (or create) agent info and set login key
            final AgentInfo info = agentInfos.computeIfAbsent(agentId, AgentInfo::new);
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
        deleteExpiredKeys();

        final AgentInfo info = agentInfos.get(agentId);
        if (info != null && isValidLogin(info.loginKey, loginKey)) {
            info.loginKey = null; // login key not needed anymore
            info.sessionKey = new TemporaryKey(Utils.getNRandomCharacters(SESSION_KEY_LENGTH), SESSION_KEY_TIME_LIMIT);
            return info.sessionKey.key;
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
        deleteExpiredKeys();

        final AgentInfo sourceAgentInfo = agentInfos.get(sourceAgentId);
        final AgentInfo targetAgentInfo = agentInfos.get(targetAgentId);

        if (sourceAgentInfo == null || targetAgentInfo == null) {
            return AGENT_DOES_NOT_EXIST;

        } else if (sourceAgentInfo.sessionKey == null) {
            return AGENT_NOT_LOGGED_IN;

        } else if (sourceAgentInfo.sessionKey.key.length() != SESSION_KEY_LENGTH) {
            return SESSION_KEY_INVALID_LENGTH; // todo: remove this check?

        } else if (!sourceAgentInfo.sessionKey.equals(sessionKey)) {
            return SESSION_KEY_UNRECOGNIZED;

        } else if (message.length() > MAX_MESSAGE_LENGTH) {
            return MESSAGE_LENGTH_EXCEEDED; // todo: send only valid prefix?

        } else {
            for (final String word : BLOCKED_WORDS) {
                if (message.toLowerCase().contains(word)) {
                    return MESSAGE_CONTAINS_BLOCKED_WORD;
                }
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
        return agentInfo != null &&
                agentInfo.sessionKey != null &&
                agentInfo.sessionKey.equals(sessionKey) &&
                agentInfo.mailbox.hasMessages();

    }

    /**
     * Consumes the next message from the agent's mailbox
     *
     * @return the next message if the agent is logged in and has
     * messages, null otherwise
     */
    public Message getNextMessage(String sessionKey, String agentID) {

        final AgentInfo agentInfo = agentInfos.get(agentID);
        if (agentInfo != null &&
                agentInfo.sessionKey != null &&
                agentInfo.sessionKey.equals(sessionKey)) {
            return agentInfo.mailbox.consumeNextMessage();
        } else {
            return null;
        }
    }

    /**
     * Checks length of login key and that it is unique.
     */
    private boolean isValidRegister(String loginKeyToCheck) {
        final Stream<AgentInfo> infos = agentInfos.values().stream();
        return loginKeyToCheck.length() == LOGIN_KEY_LENGTH
                && infos.noneMatch(v -> v.loginKey != null && v.loginKey.equals(loginKeyToCheck));
    }

    /**
     * Checks agent had a registered login key and that it matches the login key
     * used to attempt to login. Also checks that login time limit was not exceeded.
     */
    private boolean isValidLogin(TemporaryKey registeredLoginKey, String loginKeyToCheck) {
        return registeredLoginKey != null
                && loginKeyToCheck != null
                && registeredLoginKey.equals(loginKeyToCheck)
                && !registeredLoginKey.isExpired();
    }

    private void deleteExpiredKeys() {
        for (AgentInfo info : agentInfos.values()) {
            if (info.loginKey != null && info.loginKey.isExpired()) {
                info.loginKey = null;
            }
            if (info.sessionKey != null && info.sessionKey.isExpired()) {
                info.sessionKey = null;
            }
        }
    }
}
