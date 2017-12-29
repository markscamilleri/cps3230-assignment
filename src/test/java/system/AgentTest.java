package system;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import util.Utils;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AgentTest {

    private Agent testAgent_notLoggedIn;
    private Agent testAgent_loggedIn;
    @Mock
    private Supervisor mockSupervisor;
    @Mock
    private MessagingSystem mockMessagingSystem;

    // Main agent details
    private final String AGENT_ID = "1234xy";
    private final String AGENT_NAME = "abcdef";
    private final String LOGIN_KEY = Utils.getNCharacters(10);
    private final String SESSN_KEY = Utils.getNCharacters(50);

    // For messaging
    private final String TARGET_AGENT_ID = "5678ab";
    private final String MESSAGE = "message";

    @Before
    public void setUp() {
        testAgent_notLoggedIn = new Agent(AGENT_ID, AGENT_NAME, mockSupervisor, mockMessagingSystem);
        testAgent_loggedIn = new Agent(AGENT_ID, AGENT_NAME, mockSupervisor, mockMessagingSystem, SESSN_KEY);
    }

    @After
    public void tearDown() {
        mockSupervisor = null;
        mockMessagingSystem = null;
        testAgent_notLoggedIn = null;
        testAgent_loggedIn = null;
    }

    @Test
    public void testLoginSuccessfulReturnsTrue() {
        when(mockSupervisor.getLoginKey(AGENT_ID)).thenReturn(LOGIN_KEY);
        when(mockMessagingSystem.login(AGENT_ID, LOGIN_KEY)).thenReturn(Utils.getNCharacters(50));

        Assert.assertTrue(testAgent_notLoggedIn.login());
    }

    @Test
    public void testLoginNoLoginKeyReturnsFalse() {
        when(mockSupervisor.getLoginKey(Mockito.anyString())).thenReturn(null);

        Assert.assertFalse(testAgent_notLoggedIn.login());
    }

    @Test
    public void testLoginNoSessionKeyReturnsFalse() {
        when(mockSupervisor.getLoginKey(AGENT_ID)).thenReturn(LOGIN_KEY);
        when(mockMessagingSystem.login(AGENT_ID, LOGIN_KEY)).thenReturn(null);

        Assert.assertFalse(testAgent_notLoggedIn.login());
    }

    @Test
    public void testSendMessageNotLoggedInFailure() {
        // by default agent is not logged in

        Assert.assertFalse(testAgent_notLoggedIn.sendMessage(TARGET_AGENT_ID, MESSAGE));
    }

    @Test
    public void testSendMessageMessagingSystemErrorFailure() {
        when(mockMessagingSystem.sendMessage(Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString())).thenReturn(StatusCodes.GENERIC_ERROR);

        Assert.assertFalse(testAgent_loggedIn.sendMessage(TARGET_AGENT_ID, MESSAGE));
    }

    @Test
    public void testSendMessageSuccess() {
        when(mockMessagingSystem.sendMessage(Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString())).thenReturn(
                StatusCodes.OK);

        Assert.assertTrue(testAgent_loggedIn.sendMessage(TARGET_AGENT_ID, MESSAGE));
    }
}