package system;

import org.junit.*;
import util.Utils;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

import static system.MessagingSystem.*;
import static system.StatusCodes.*;

// todo: check bad session key length?
public class MessagingSystemTest {

    private MessagingSystem system;
    private Map<String, AgentInfo> agentInfos;

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
    
    // default clock used for testing
    private Clock defaultFixedClock  = Clock.fixed(Instant.EPOCH, ZoneId.of("UTC")); // Fixed Clock for independent testing
    
    @Before
    public void setUp() {
        agentInfos = new HashMap<>();
        system = new MessagingSystem(agentInfos);
    }

    @After
    public void tearDown() {
        agentInfos = null;
        system = null;
    }

    @Test
    public void registerFailsIfLoginKeyIncorrectLength() {
        Assert.assertFalse(system.registerLoginKey(AID_1, Utils.getNCharacters(LOGIN_KEY_LENGTH + 1)));
        Assert.assertFalse(system.registerLoginKey(AID_1, Utils.getNCharacters(LOGIN_KEY_LENGTH - 1)));
    }

    @Test
    public void registerFailsIfLoginKeyNotUnique() {
        addAgent(agentInfos, 1, AddType.REGISTERED, defaultFixedClock);

        Assert.assertFalse(system.registerLoginKey(AID_2, VALID_LKEY_1));
    }

    @Test
    public void registerSucceedsIfLoginKeyValid() {
        Assert.assertTrue(system.registerLoginKey(AID_1, VALID_LKEY_1));
    }

    @Test
    public void loginFailsIfAgentDoesNotExist() {
        Assert.assertNull(system.login(AID_1, VALID_LKEY_1));
    }

    @Test
    public void loginFailsIfAgentDidNotRegister() {
        addAgent(agentInfos, 1, AddType.UNREGISTERED, defaultFixedClock);

        Assert.assertNull(system.login(AID_1, VALID_LKEY_1));
    }

    @Test // todo
    public void loginFailsIfLoginKeyExpired() {
        // To ensure that the next step will be over the login key time limit
        Clock testClock = new StepClock(Instant.EPOCH, LOGIN_KEY_TIME_LIMIT.plusSeconds(1));
        
        addAgent(agentInfos, 1, AddType.REGISTERED, testClock);
        
        Assert.assertNull(system.login(AID_1, VALID_LKEY_1));
    }

    @Test
    public void loginFailsIfLoginKeyDoesNotMatch() {
        addAgent(agentInfos, 1, AddType.REGISTERED, defaultFixedClock);

        Assert.assertNull(system.login(AID_1, VALID_LKEY_2));
    }

    @Test
    public void loginReturnsValidSessionKeyIfLoginKeyValid() {
        addAgent(agentInfos, 1, AddType.REGISTERED, defaultFixedClock);

        final String sessionKey = system.login(AID_1, VALID_LKEY_1);
        Assert.assertNotNull(sessionKey);
        Assert.assertTrue(sessionKey.length() == SESSION_KEY_LENGTH);
    }

    @Test
    public void sendMessageFailsIfSourceAgentDoesNotExist() {
        addAgent(agentInfos, 2, AddType.REGISTERED, defaultFixedClock); // only target agent exists

        Assert.assertEquals(system.sendMessage(VALID_SKEY_1, AID_1, AID_2, VALID_MSG), AGENT_DOES_NOT_EXIST);
    }

    @Test
    public void sendMessageFailsIfTargetAgentDoesNotExist() {
        addAgent(agentInfos, 1, AddType.REGISTERED, defaultFixedClock); // only source agent exists

        Assert.assertEquals(system.sendMessage(VALID_SKEY_1, AID_1, AID_2, VALID_MSG), AGENT_DOES_NOT_EXIST);
    }

    @Test
    public void sendMessageFailsIfSourceAgentDidNotLogin() {
        addAgent(agentInfos, 1, AddType.REGISTERED, defaultFixedClock); // source did not login
        addAgent(agentInfos, 2, AddType.REGISTERED, defaultFixedClock); // target doesn't have to be logged in


        Assert.assertEquals(AGENT_NOT_LOGGED_IN, system.sendMessage(VALID_SKEY_1, AID_1, AID_2, VALID_MSG));
    }

    @Test // todo
    public void sendMessageFailsIfSessionKeyExpired() {
        // To ensure that the next step will be over the login key time limit
        Clock agent1clock = new StepClock(Instant.EPOCH, SESSION_KEY_TIME_LIMIT.plusSeconds(1));
        
        addAgent(agentInfos, 1, AddType.LOGGEDIN, agent1clock);   // source must be logged in
        addAgent(agentInfos, 2, AddType.REGISTERED, defaultFixedClock); // target doesn't have to be logged in
        
        Assert.assertEquals(StatusCodes.AGENT_NOT_LOGGED_IN, system.sendMessage(VALID_SKEY_1, AID_1, AID_2, VALID_MSG));
    }

    @Test
    public void sendMessageFailsIfSessionKeyDoesNotMatch() {
        addAgent(agentInfos, 1, AddType.LOGGEDIN, defaultFixedClock);   // source must be logged in
        addAgent(agentInfos, 2, AddType.REGISTERED, defaultFixedClock); // target doesn't have to be logged in

        Assert.assertEquals(system.sendMessage(VALID_SKEY_2, AID_1, AID_2, VALID_MSG), SESSION_KEY_UNRECOGNIZED);
    }

    @Test
    public void sendMessageFailsIfMessageLengthExceeded() {
        addAgent(agentInfos, 1, AddType.LOGGEDIN, defaultFixedClock);   // source must be logged in
        addAgent(agentInfos, 2, AddType.REGISTERED, defaultFixedClock); // target doesn't have to be logged in

        final String LONG_MESSAGE = Utils.getNCharacters(MAX_MESSAGE_LENGTH + 1);
        Assert.assertEquals(system.sendMessage(VALID_SKEY_1, AID_1, AID_2, LONG_MESSAGE), MESSAGE_LENGTH_EXCEEDED);
    }

    @Test
    public void sendMessageFailsIfMessageContainsBlockedWords() {
        addAgent(agentInfos, 1, AddType.LOGGEDIN, defaultFixedClock);   // source must be logged in
        addAgent(agentInfos, 2, AddType.REGISTERED, defaultFixedClock); // target doesn't have to be logged in

        for (String bw : BLOCKED_WORDS) {
            final String msg = VALID_MSG + bw + VALID_MSG;
            Assert.assertEquals(system.sendMessage(VALID_SKEY_1, AID_1, AID_2, msg), MESSAGE_CONTAINS_BLOCKED_WORD);
        }
    }

    @Test
    public void sendMessageReturnsOkIfAllValid() {
        addAgent(agentInfos, 1, AddType.LOGGEDIN, defaultFixedClock);   // source must be logged in
        addAgent(agentInfos, 2, AddType.REGISTERED, defaultFixedClock); // target doesn't have to be logged in

        Assert.assertEquals(system.sendMessage(VALID_SKEY_1, AID_1, AID_2, VALID_MSG), OK);
    }

    private enum AddType {
        UNREGISTERED,
        REGISTERED,
        LOGGEDIN
    }
    
    private AgentInfo addAgent(final Map<String, AgentInfo> agentInfos, int agent, AddType type, Clock clock) {

        Assume.assumeTrue(agent == 1 || agent == 2);

        final String agentId = (agent == 1 ? AID_1 : AID_2);
        final String loginKey = (agent == 1 ? VALID_LKEY_1 : VALID_LKEY_2);
        final String sessnKey = (agent == 1 ? VALID_SKEY_1 : VALID_SKEY_2);
        final AgentInfo agentInfo = new AgentInfo(agentId);
        
        switch (type) {
            case REGISTERED:
                agentInfo.loginKey = new TemporaryKey(loginKey, LOGIN_KEY_TIME_LIMIT, clock);
                break;
            case LOGGEDIN:
                agentInfo.sessionKey = new TemporaryKey(sessnKey, SESSION_KEY_TIME_LIMIT, clock);
                break;
            default:
                break;
        }
        // Note: if AddType.UNREGISTERED, nothing is done

        agentInfos.put(agentId, agentInfo);
        return agentInfo;
    }

    private class StepClock extends Clock {
        private int stepMultiplier = 0;
        private final Instant baseTime;
        private final Duration step;
        
        StepClock(Instant baseTime, Duration step){
            this.baseTime = baseTime;
            this.step = step;
        }
        
        @Override
        public long millis() {
            return super.millis();
        }
        
        @Override
        public ZoneId getZone() {
            return ZoneId.of("UTC");
        }
        
        @Override
        //Ignored
        public Clock withZone(ZoneId zoneId) {
            return this;
        }
        
        @Override
        public Instant instant() {
            return baseTime.plus(step.multipliedBy(stepMultiplier++));
        }
    }
    
}