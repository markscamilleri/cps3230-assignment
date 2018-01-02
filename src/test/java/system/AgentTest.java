package system;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import util.Utils;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AgentTest {

    // Main agent details
    private final String AGENT_ID = "1234xy";
    private final String LOGIN_KEY = Utils.getNCharacters(10);
    private final String SESSN_KEY = Utils.getNCharacters(50);

    // For messaging
    private final String TARGET_AGENT_ID = "5678ab";
    private final String MESSAGE = "message";

    private Agent testAgent_default;
    private Agent testAgent_registered;
    private Agent testAgent_loggedIn;
    private Agent testAgent_bothKeys;

    @Mock
    private Supervisor mockSupervisor;
    @Mock
    private MessagingSystem mockMessagingSystem;

    @Before
    public void setUp() {
        testAgent_default = new Agent(AGENT_ID, mockSupervisor, mockMessagingSystem);
        testAgent_registered = new Agent(AGENT_ID, mockSupervisor, mockMessagingSystem, LOGIN_KEY, null);
        testAgent_loggedIn = new Agent(AGENT_ID, mockSupervisor, mockMessagingSystem, null, SESSN_KEY);
        testAgent_bothKeys = new Agent(AGENT_ID, mockSupervisor, mockMessagingSystem, LOGIN_KEY, SESSN_KEY);
    }

    @After
    public void tearDown() {
        mockSupervisor = null;
        mockMessagingSystem = null;
        testAgent_default = null;
        testAgent_registered = null;
        testAgent_loggedIn = null;
    }

    @Test
    public void register_trueIfSuccessful() {
        when(mockSupervisor.getLoginKey(AGENT_ID)).thenReturn(LOGIN_KEY);

        Assert.assertTrue(testAgent_default.register());
    }

    @Test
    public void register_falseIfNoLoginKeyFromSupervisor() {
        when(mockSupervisor.getLoginKey(Mockito.anyString())).thenReturn(null);

        Assert.assertFalse(testAgent_default.register());
    }

    @Test
    public void login_trueIfSuccessful() {
        when(mockMessagingSystem.login(AGENT_ID, LOGIN_KEY)).thenReturn(Utils.getNCharacters(50));

        Assert.assertTrue(testAgent_registered.login());
    }

    @Test
    public void login_falseIfNoLoginKeyFromSupervisor() {
        when(mockMessagingSystem.login(AGENT_ID, LOGIN_KEY)).thenReturn(null);

        Assert.assertFalse(testAgent_registered.login());
    }

    @Test
    public void sendMessage_falseIfNotLoggedIn() {
        // by default agent is not logged in

        Assert.assertFalse(testAgent_default.sendMessage(TARGET_AGENT_ID, MESSAGE));
    }

    @Test
    public void sendMessage_falseIfMessagingSystemFailure() {
        when(mockMessagingSystem.sendMessage(Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString())).thenReturn(StatusCodes.GENERIC_ERROR);

        Assert.assertFalse(testAgent_loggedIn.sendMessage(TARGET_AGENT_ID, MESSAGE));
    }

    @Test
    public void sendMessage_trueIfSuccessful() {
        when(mockMessagingSystem.sendMessage(Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString())).thenReturn(
                StatusCodes.OK);

        Assert.assertTrue(testAgent_loggedIn.sendMessage(TARGET_AGENT_ID, MESSAGE));
    }

    @Test
    public void getLoginKey_returnsLoginKey() {
        Assert.assertEquals(LOGIN_KEY, testAgent_bothKeys.getLoginKey());
    }

    @Test
    public void getSessionKey_returnsSessionKey() {
        Assert.assertEquals(SESSN_KEY, testAgent_bothKeys.getSessionKey());
    }
}