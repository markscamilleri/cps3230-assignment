package system;

import org.junit.*;
import util.Utils;

import java.util.HashMap;
import java.util.Map;

import static system.MessagingSystem.*;
import static system.StatusCodes.*;

// todo: check bad session key length?
public class MessagingSystemTest {

    // Valid login keys (one per agent)
    private final String VALID_LKEY_1 = Utils.getNCharacters(LOGIN_KEY_LENGTH, "1");
    private final String VALID_LKEY_2 = Utils.getNCharacters(LOGIN_KEY_LENGTH, "2");

    // Valid session keys (one per agent)
    private final String VALID_SKEY_1 = Utils.getNCharacters(SESSION_KEY_LENGTH, "1");
    private final String VALID_SKEY_2 = Utils.getNCharacters(SESSION_KEY_LENGTH, "2");

    // Two agent IDs and valid message
    private final String AID_1 = "1234xy";
    private final String AID_2 = "5678ab";
    private final String VALID_MSG = "msg";
    private MessagingSystem testSystem;
    private Map<String, AgentInfo> agentInfos;

    @Before
    public void setUp() {
        agentInfos = new HashMap<>();
        testSystem = new MessagingSystem(agentInfos);
    }

    @After
    public void tearDown() {
        agentInfos = null;
        testSystem = null;
    }

    @Test
    public void registerFailsIfLoginKeyIncorrectLength() {
        Assert.assertFalse(testSystem.registerLoginKey(AID_1, Utils.getNCharacters(LOGIN_KEY_LENGTH + 1)));
        Assert.assertFalse(testSystem.registerLoginKey(AID_1, Utils.getNCharacters(LOGIN_KEY_LENGTH - 1)));
    }

    @Test
    public void registerFailsIfLoginKeyNotUnique() {
        addAgent(agentInfos, 1, AddType.REGISTERED);

        Assert.assertFalse(testSystem.registerLoginKey(AID_2, VALID_LKEY_1));
    }

    @Test
    public void registerSucceedsIfLoginKeyValid() {
        Assert.assertTrue(testSystem.registerLoginKey(AID_1, VALID_LKEY_1));
    }

    @Test
    public void loginFailsIfAgentDoesNotExist() {
        Assert.assertEquals(null, testSystem.login(AID_1, VALID_LKEY_1));
    }

    @Test
    public void loginFailsIfAgentExistsButDidNotRegister() {
        addAgent(agentInfos, 1, AddType.UNREGISTERED);

        Assert.assertEquals(null, testSystem.login(AID_1, VALID_LKEY_1)); // given a valid login key before registering
        Assert.assertEquals(null, testSystem.login(AID_1, null)); // when agent.login() called before registering
    }

    @Test // todo
    public void loginFailsIfLoginKeyExpired() {
        addAgent(agentInfos, 1, AddType.REGISTERED);

        // 1 minute passes...
        // Assert.assertEquals(null, testSystem.login(AID_1, VALID_LKEY_1));
    }

    @Test
    public void loginFailsIfLoginKeyDoesNotMatch() {
        addAgent(agentInfos, 1, AddType.REGISTERED);

        Assert.assertEquals(null, testSystem.login(AID_1, VALID_LKEY_2));
    }

    @Test
    public void loginReturnsValidSessionKeyIfLoginKeyValid() {
        addAgent(agentInfos, 1, AddType.REGISTERED);

        final String sessionKey = testSystem.login(AID_1, VALID_LKEY_1);
        Assert.assertNotEquals(null, sessionKey);
        Assert.assertTrue(sessionKey.length() == SESSION_KEY_LENGTH);
    }

    @Test
    public void sendMessageFailsIfSourceAgentDoesNotExist() {
        addAgent(agentInfos, 2, AddType.REGISTERED); // only target agent exists

        Assert.assertEquals(testSystem.sendMessage(VALID_SKEY_1, AID_1, AID_2, VALID_MSG), AGENT_DOES_NOT_EXIST);
    }

    @Test
    public void sendMessageFailsIfTargetAgentDoesNotExist() {
        addAgent(agentInfos, 1, AddType.REGISTERED); // only source agent exists

        Assert.assertEquals(testSystem.sendMessage(VALID_SKEY_1, AID_1, AID_2, VALID_MSG), AGENT_DOES_NOT_EXIST);
    }

    @Test
    public void sendMessageFailsIfSourceAgentDidNotLogin() {
        addAgent(agentInfos, 1, AddType.REGISTERED); // source did not login
        addAgent(agentInfos, 2, AddType.REGISTERED); // target doesn't have to be logged in


        Assert.assertEquals(testSystem.sendMessage(VALID_SKEY_1, AID_1, AID_2, VALID_MSG), AGENT_NOT_LOGGED_IN);
    }

    @Test // todo
    public void sendMessageFailsIfSessionKeyExpired() {
        addAgent(agentInfos, 1, AddType.LOGGEDIN);   // source must be logged in
        addAgent(agentInfos, 2, AddType.REGISTERED); // target doesn't have to be logged in

        // 10 minutes pass...
        // Assert.assertEquals(testSystem.sendMessage(VALID_SKEY_1, AID_1, AID_2, VALID_MSG), StatusCodes.AGENT_NOT_LOGGED_IN);
    }

    @Test
    public void sendMessageFailsIfSessionKeyDoesNotMatch() {
        addAgent(agentInfos, 1, AddType.LOGGEDIN);   // source must be logged in
        addAgent(agentInfos, 2, AddType.REGISTERED); // target doesn't have to be logged in

        Assert.assertEquals(testSystem.sendMessage(VALID_SKEY_2, AID_1, AID_2, VALID_MSG), SESSION_KEY_UNRECOGNIZED);
    }

    @Test
    public void sendMessageFailsIfMessageLengthExceeded() {
        addAgent(agentInfos, 1, AddType.LOGGEDIN);   // source must be logged in
        addAgent(agentInfos, 2, AddType.REGISTERED); // target doesn't have to be logged in

        final String LONG_MESSAGE = Utils.getNCharacters(MAX_MESSAGE_LENGTH + 1);
        Assert.assertEquals(testSystem.sendMessage(VALID_SKEY_1, AID_1, AID_2, LONG_MESSAGE), MESSAGE_LENGTH_EXCEEDED);
    }

    @Test
    public void sendMessageFailsIfMessageContainsBlockedWords() {
        addAgent(agentInfos, 1, AddType.LOGGEDIN);   // source must be logged in
        addAgent(agentInfos, 2, AddType.REGISTERED); // target doesn't have to be logged in

        for (String bw : BLOCKED_WORDS) {
            final String msg1 = VALID_MSG + bw.toLowerCase() + VALID_MSG; // message with lowercase blocked word
            final String msg2 = VALID_MSG + bw.toUpperCase() + VALID_MSG; // message with uppercase blocked word
            Assert.assertEquals(testSystem.sendMessage(VALID_SKEY_1, AID_1, AID_2, msg1), MESSAGE_CONTAINS_BLOCKED_WORD);
            Assert.assertEquals(testSystem.sendMessage(VALID_SKEY_1, AID_1, AID_2, msg2), MESSAGE_CONTAINS_BLOCKED_WORD);
        }
    }

    @Test
    public void sendMessageReturnsOkIfAllValid() {
        addAgent(agentInfos, 1, AddType.LOGGEDIN);   // source must be logged in
        addAgent(agentInfos, 2, AddType.REGISTERED); // target doesn't have to be logged in

        Assert.assertEquals(testSystem.sendMessage(VALID_SKEY_1, AID_1, AID_2, VALID_MSG), OK);
    }

    private AgentInfo addAgent(final Map<String, AgentInfo> agentInfos, int agent, AddType type) {

        Assume.assumeTrue(agent == 1 || agent == 2);

        final String agentId = (agent == 1 ? AID_1 : AID_2);
        final String loginKey = (agent == 1 ? VALID_LKEY_1 : VALID_LKEY_2);
        final String sessnKey = (agent == 1 ? VALID_SKEY_1 : VALID_SKEY_2);
        final AgentInfo agentInfo = new AgentInfo(agentId);

        if (type == AddType.REGISTERED) { // registered; not logged in
            agentInfo.loginKey = new TemporaryKey(loginKey, LOGIN_KEY_TIME_LIMIT);
        } else if (type == AddType.LOGGEDIN) { // logged in
            agentInfo.sessionKey = new TemporaryKey(sessnKey, SESSION_KEY_TIME_LIMIT);
        }
        // Note: if AddType.UNREGISTERED, nothing is done

        agentInfos.put(agentId, agentInfo);
        return agentInfo;
    }

    private enum AddType {
        UNREGISTERED,
        REGISTERED,
        LOGGEDIN
    }
}