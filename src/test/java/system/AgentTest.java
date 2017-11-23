package system;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Mockito.when;

public class AgentTest {
    
    private final String CORRECT_AGENT_ID = "1234xy";
    private final String LOGIN_KEY = "loginpass1";
    private Agent testAgent;
    
    @Mock
    private Supervisor mockSupervisor;
    
    @Mock
    private MessagingSystem mockMessagingSystem;
    
    @Before
    public void setUp() throws Exception {
        when(mockSupervisor.getLoginKey(Mockito.anyString())).thenReturn(LOGIN_KEY);
        when(mockMessagingSystem.login(CORRECT_AGENT_ID, LOGIN_KEY)).thenReturn("Success");
        when(mockMessagingSystem.login(Mockito.anyString(), Mockito.anyString())).thenReturn("Failed");
        
        testAgent = new Agent();
        testAgent.id = CORRECT_AGENT_ID;
        testAgent.name = "Gamri";
        testAgent.supervisor = mockSupervisor;
    }

    @After
    public void tearDown() throws Exception {
        mockSupervisor = null;
    }

    @Test
    public void testLoginSuccessfulReturnsTrue() throws Exception {
        testAgent.id = CORRECT_AGENT_ID;
    
        Assert.assertTrue(testAgent.login());
    }
    
    @Test
    public void testLoginUnsuccessfulReturnsFalse() throws Exception {
        testAgent.id = "358403";
        
        Assert.assertFalse(testAgent.login());
    }

    @Test
    public void sendMessage() throws Exception {
    }

}