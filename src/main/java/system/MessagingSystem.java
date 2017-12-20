package system;

import util.Timeoutable;
import util.Utils;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static system.MessagingSystemStatusCodes.*;

public class MessagingSystem {

    private static MessagingSystem INSTANCE = null;

    public final static Duration LOGIN_KEY_TIME_LIMIT = Duration.ofMinutes(1);
    public final static Duration SESSION_KEY_TIME_LIMIT = Duration.ofMinutes(10);

    public final static int LOGIN_KEY_LENGTH = 10;
    public final static int SESSION_KEY_LENGTH = 50;

    public final static int MAX_MESSAGE_LENGTH = 140;
    public final static String BLOCKED_WORDS[] = {"recipe", "ginger", "nuclear"};

    private Map<String, AgentInfo> agentInfos = new HashMap<>();

    private MessagingSystem() {
    }

    public static MessagingSystem getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MessagingSystem();
        }
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
        deleteExpiredKeys();

        if (isValidRegister(loginKey)) {

            // Obtain (or create) agent info and set login key
            AgentInfo info = agentInfos.get(agentId);
            if (info == null) {
                info = new AgentInfo(agentId);
                agentInfos.put(agentId, info);
            }
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
    public String sendMessage(String sessionKey, String sourceAgentId, String targetAgentId, String message) {
        deleteExpiredKeys();

        final AgentInfo sourceAgentInfo = agentInfos.get(sourceAgentId);
        final AgentInfo targetAgentInfo = agentInfos.get(targetAgentId);

        if (sourceAgentInfo == null || targetAgentInfo == null) {
            return AGENT_DOES_NOT_EXIST.getValue();

        } else if (sourceAgentInfo.sessionKey == null) {
            return AGENT_NOT_LOGGED_IN.getValue();

        } else if (!sourceAgentInfo.sessionKey.equals(sessionKey)) {
            return SESSION_KEY_UNRECOGNIZED.getValue();

        } else if (message.length() > MAX_MESSAGE_LENGTH) {
            return MESSAGE_LENGTH_EXCEEDED.getValue();

        } else {
            for (final String word : BLOCKED_WORDS) {
                if (message.contains(word)) {
                    return MESSAGE_CONTAINS_BLOCKED_WORD.getValue();
                }
            }

            final Message toSend = new Message(sourceAgentId, targetAgentId, message);
            if (targetAgentInfo.mailbox.addMessage(toSend)) {
                return MessagingSystemStatusCodes.OK.getValue();
            } else {
                return MessagingSystemStatusCodes.INVALID_MESSAGE.getValue();
            }
        }
    }

    /**
     * Checks length of login key and that it is unique.
     */
    private boolean isValidRegister(String loginKeyToCheck) {
        return loginKeyToCheck.length() == LOGIN_KEY_LENGTH
                && agentInfos.values().stream().noneMatch(v -> v.loginKey.equals(loginKeyToCheck));
    }

    /**
     * Checks agent had a registered login key and that it matches the login key
     * used to attempt to login. Also checks that login time limit was not exceeded.
     */
    private boolean isValidLogin(TemporaryKey registeredLoginKey, String loginKeyToCheck) {
        return registeredLoginKey != null
                && registeredLoginKey.equals(loginKeyToCheck)
                && !registeredLoginKey.isExpired();
    }

    //@Override
    protected int deleteExpiredKeys() {

        int count = 0;
        for (AgentInfo info : agentInfos.values()) {
            if (info.loginKey != null && info.loginKey.isExpired()) {
                info.loginKey = null;
                count++;
            }
            if (info.sessionKey != null && info.sessionKey.isExpired()) {
                info.sessionKey = null;
                count++;
            }
        }
        return count;
    }

    private class AgentInfo {

        final String agentId;
        final Mailbox mailbox;
        TemporaryKey loginKey = null;
        TemporaryKey sessionKey = null;

        AgentInfo(String agentId) {
            this.agentId = agentId;
            this.mailbox = new Mailbox(agentId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(agentId);
        }
    }

    private class TemporaryKey extends Timeoutable {

        public final String key;

        public TemporaryKey(String key, Duration timeLimit) {
            this(key, timeLimit, Clock.systemUTC());
        }

        public TemporaryKey(String key, Duration timeLimit, Clock clock) {
            super(Instant.now(clock).plus(timeLimit));
            this.key = key;
        }

        public boolean equals(String anotherKey) {
            return this.key.equals(anotherKey);
        }
    }
}
