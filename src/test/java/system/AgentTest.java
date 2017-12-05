package system;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Random;

import static org.mockito.Mockito.when;

public class AgentTest {
    
    private final String CORRECT_AGENT_ID = "1234xy";
    private final String LOGIN_KEY = getNRandomCharacters(10);
    private Agent testAgent;

    private class LoggedInTestAgent extends Agent {
        public LoggedInTestAgent(String sessionKey){
            this.sessionKey = sessionKey;
        }
    }

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
        testAgent = new Agent();
        testAgent.id = CORRECT_AGENT_ID;
        testAgent.name = "Gamri";
        testAgent.supervisor = mockSupervisor;
    }

    @After
    public void tearDown() throws Exception {
        mockSupervisor = null;
        mockMessagingSystem = null;
    }

    @Test
    public void testLoginSuccessfulReturnsTrue() throws Exception {
        testAgent.id = CORRECT_AGENT_ID;
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

        LoggedInTestAgent loggedInTestAgent = new LoggedInTestAgent(getNRandomCharacters(50));

        Assert.assertFalse(loggedInTestAgent.sendMessage("Agent P", "Hello"));
    }

    @Test
    public void testSendMessageSuccess() throws Exception {
        when(mockMessagingSystem.sendMessage(Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString())).thenReturn(
                MessagingSystemStatusCodes.OK.getValue());

        LoggedInTestAgent loggedInTestAgent = new LoggedInTestAgent(getNRandomCharacters(50));

        Assert.assertTrue(loggedInTestAgent.sendMessage("Agent P", "Hello"));

    }
}