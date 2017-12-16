package system;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Random;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AgentTest {

    private final String CORRECT_AGENT_ID = "1234xy";
    private final String LOGIN_KEY = getNRandomCharacters(10);
    private Agent testAgent;
    private Agent loggedInTestAgent;
    @Mock
    private Supervisor mockSupervisor;
    @Mock
    private MessagingSystem mockMessagingSystem;

    private String getNRandomCharacters(int n) {
        String characters = "";

        Random r = new Random();

        for (int i = 0; i < n; i++) {
            characters = characters + (char) r.nextInt();
        }

        return characters;
    }

    @Before
    public void setUp() throws Exception {

        testAgent = new Agent(CORRECT_AGENT_ID, "Gamri", mockSupervisor, mockMessagingSystem);
        loggedInTestAgent = new LoggedInTestAgent(CORRECT_AGENT_ID, "Gamri",
                mockSupervisor, mockMessagingSystem, getNRandomCharacters(50));
    }

    @After
    public void tearDown() throws Exception {
        mockSupervisor = null;
        mockMessagingSystem = null;
    }

    @Test
    public void testLoginSuccessfulReturnsTrue() throws Exception {
        when(mockSupervisor.getLoginKey(CORRECT_AGENT_ID)).thenReturn(LOGIN_KEY);
        when(mockMessagingSystem.login(CORRECT_AGENT_ID, LOGIN_KEY)).thenReturn(getNRandomCharacters(50));

        Assert.assertTrue(testAgent.login());
    }

    @Test
    public void testLoginNoLoginKeyReturnsFalse() throws Exception {
        when(mockSupervisor.getLoginKey(Mockito.anyString())).thenReturn(null);

        Assert.assertFalse(testAgent.login());
    }

    @Test
    public void testLoginNoSessionKeyReturnsFalse() throws Exception {
        when(mockSupervisor.getLoginKey(CORRECT_AGENT_ID)).thenReturn(LOGIN_KEY);
        when(mockMessagingSystem.login(CORRECT_AGENT_ID, LOGIN_KEY)).thenReturn(null);

        Assert.assertFalse(testAgent.login());
    }

    @Test
    public void testSendMessageNotLoggedInFailure() throws Exception {
        // by default agent is not logged in

        Assert.assertFalse(testAgent.sendMessage("Agent P", "Hello"));
    }

    @Test
    public void testSendMessageMessagingSystemErrorFailure() throws Exception {
        when(mockMessagingSystem.sendMessage(Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString())).thenReturn(
                MessagingSystemStatusCodes.ERROR.getValue());

        Assert.assertFalse(loggedInTestAgent.sendMessage("Agent P", "Hello"));
    }

    @Test
    public void testSendMessageSuccess() throws Exception {
        when(mockMessagingSystem.sendMessage(Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString())).thenReturn(
                MessagingSystemStatusCodes.OK.getValue());

        Assert.assertTrue(loggedInTestAgent.sendMessage("Agent P", "Hello"));

    }

    private class LoggedInTestAgent extends Agent {
        LoggedInTestAgent(String id, String name, Supervisor supervisor,
                          MessagingSystem messagingSystem, String sessionKey) {
            super(id, name, supervisor, messagingSystem);
            this.sessionKey = sessionKey;
        }
    }
}