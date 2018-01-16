package modeltesting;

import nz.ac.waikato.modeljunit.*;
import nz.ac.waikato.modeljunit.coverage.ActionCoverage;
import nz.ac.waikato.modeljunit.coverage.StateCoverage;
import nz.ac.waikato.modeljunit.coverage.TransitionCoverage;
import nz.ac.waikato.modeljunit.coverage.TransitionPairCoverage;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import util.Utils;

import java.io.FileNotFoundException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Random;


public class AssignmentModel implements FsmModel {

    private static final String baseUrl = "localhost:" + webapp.StartJettyHandler.PORT_NUMBER;
    private WebDriver driver = new ChromeDriver();

    private ModelStateEnum currentState = ModelStateEnum.UNREGISTERED;
    private final Duration TEST_DURATION = Duration.ofMinutes(15);

    private final int MAX_MESSAGES_SENT = 25;
    private final int MAX_MESSAGE_RECEIVED = 25;
    private static HashSet<String> usedAgent2Ids = new HashSet<>();

    private String agentID = Utils.getNRandomCharacters(5);
    private String loginKey = null;
    private int sessionMessagesSent = 0; // Session messages sent
    private int sessionMessagesRecv = 0; // Session messages Received
    private int messagesRecv = 0; // message received

    /**
     * Registers the agent. No checks are done if this was successful
     *
     * @param driver  the WebDriver to use
     * @param agentId the agent id to login
     */
    private static void registerAgentHelper(WebDriver driver, String agentId) {
        driver.get(baseUrl + "/register");
        driver.findElement(By.id("idInput")).click();
        driver.findElement(By.id("idInput")).sendKeys(agentId);
        driver.findElement(By.id("submit")).click();
    }

    /**
     * Logs in the agent on the specified WebDriver. No checks are done.
     *
     * @param driver   the WebDriver to use
     * @param loginKey the loginKey to use
     */
    private static void loginAgentHelper(WebDriver driver, String loginKey) {
        driver.findElement(By.id("lKeyInput")).click();
        driver.findElement(By.id("lKeyInput")).sendKeys(loginKey);
        driver.findElement(By.id("submit")).click();
    }

    /**
     * Sends a message to the target agent. No checks are done
     *
     * @param driver        the WebDriver to use
     * @param targetAgentId the target agent's id
     * @param message       the message to send
     */
    private static void sendMessageHelper(WebDriver driver, String targetAgentId, String message) {
        driver.findElement(By.id("destination")).click();
        driver.findElement(By.id("destination")).sendKeys(targetAgentId);
        driver.findElement(By.id("messageBody")).click();
        driver.findElement(By.id("messageBody")).sendKeys(message);
        driver.findElement(By.id("submit")).click();
    }

    private static String createUniqueAgent2Helper(WebDriver driver) {
        String agent2Id;
        
        do {
            agent2Id = "agent2ID-" + Utils.getNRandomCharacters(10);
        } while(!usedAgent2Ids.add(agent2Id));
        
        registerAgentHelper(driver, agent2Id);
        return agent2Id;
    }

    @Action
    public void normalRegister() {
        registerAgentHelper(driver, agentID);

        Assert.assertTrue(driver.getCurrentUrl().endsWith(baseUrl + "/login"));
        currentState = ModelStateEnum.REGISTERED;
    }

    public boolean normalRegisterGuard() {
        return currentState == ModelStateEnum.UNREGISTERED;
    }

    @Action
    public void spyRegister() {
        registerAgentHelper(driver, "spy-" + Utils.getNRandomCharacters(5));

        Assert.assertTrue(driver.getCurrentUrl().endsWith(baseUrl + "/register"));
        currentState = ModelStateEnum.UNREGISTERED;
    }

    public boolean spyRegisterGuard() {
        return currentState == ModelStateEnum.UNREGISTERED;
    }

    @Action
    public void validLoginKeyLogin() {
        loginKey = driver.findElement(By.id("lKey")).getText();
        loginAgentHelper(driver, loginKey);

        Assert.assertTrue(driver.getCurrentUrl().endsWith(baseUrl + "/loggedin"));
        sessionMessagesRecv = 0;
        sessionMessagesSent = 0;
        currentState = ModelStateEnum.LOGGED_IN;
    }

    public boolean validLoginKeyLoginGuard() {
        return currentState == ModelStateEnum.REGISTERED;
    }

    @Action
    public void invalidLoginKeyLogin() {
        loginAgentHelper(driver, "invalidLoginKey");

        Assert.assertTrue(driver.getCurrentUrl().endsWith(baseUrl + "/login"));
        currentState = ModelStateEnum.REGISTERED;
    }

    public boolean invalidLoginKeyLoginGuard() {
        return currentState == ModelStateEnum.REGISTERED;
    }

    @Action
    public void gotoSendMessagePage() {
        driver.findElement(By.id("sendMessage")).click();
        Assert.assertTrue(driver.getCurrentUrl().endsWith(baseUrl + "/sendmessage"));
        currentState = ModelStateEnum.SENDING_MESSAGE;
    }

    public boolean gotoSendMessagePageGuard() {
        return currentState == ModelStateEnum.LOGGED_IN;
    }

    @Action
    public void sendNormalMessage() {
        sendMessageHelper(driver, agentID, "Hello World");
        Assert.assertTrue(driver.getCurrentUrl().endsWith(baseUrl + "/sendmessage"));
        sessionMessagesSent++;
        sessionMessagesRecv++;
        messagesRecv++;
        currentState = ModelStateEnum.SENDING_MESSAGE;
    }

    public boolean sendNormalMessageGuard() {
        return currentState == ModelStateEnum.SENDING_MESSAGE;
    }

    @Action
    public void sendLongMessage() {
        sendMessageHelper(driver, agentID, Utils.getNCharacters(150));
        Assert.assertTrue(driver.getCurrentUrl().endsWith(baseUrl + "/sendmessage"));

        final String notificationText = driver.findElement(By.id("notif")).getText();
        Assert.assertTrue(notificationText.equals("Message not sent since it is longer than 140 characters."));
        currentState = ModelStateEnum.SENDING_MESSAGE;
    }

    public boolean sendLongMessageGuard() {
        return currentState == ModelStateEnum.SENDING_MESSAGE;
    }

    @Action
    public void sendMessageWithBlockedWords() {
        sendMessageHelper(driver, agentID, "Get the nuclEAR REcipe with GinGer");
        Assert.assertTrue(driver.getCurrentUrl().endsWith(baseUrl + "/sendmessage"));

        final String notificationText = driver.findElement(By.id("notif")).getText();
        Assert.assertTrue(notificationText.equals("Message sent successfully."));
        sessionMessagesSent++;
        sessionMessagesRecv++;
        messagesRecv++;
        currentState = ModelStateEnum.SENDING_MESSAGE;
    }

    public boolean sendMessageWithBlockedWordsGuard() {
        return currentState == ModelStateEnum.SENDING_MESSAGE;
    }

    @Action
    public void sendMessageWithNonExistentTarget() {
        // 6 characters to not clash with test agent of 5 characters
        final String AGENT_2 = Utils.getNRandomCharacters(6);

        sendMessageHelper(driver, AGENT_2, "Hello World");
        Assert.assertTrue(driver.getCurrentUrl().endsWith(baseUrl + "/sendmessage"));

        final String notificationText = driver.findElement(By.id("notif")).getText();
        Assert.assertTrue(notificationText.equals("Message not sent since the target agent does not exist."));
        currentState = ModelStateEnum.SENDING_MESSAGE;
    }

    public boolean sendMessageWithNonExistentTargetGuard() {
        return currentState == ModelStateEnum.SENDING_MESSAGE;
    }

    @Action
    public void gotoReadMessagePage() {
        driver.findElement(By.id("consumeMessage")).click();
        Assert.assertTrue(driver.getCurrentUrl().endsWith(baseUrl + "/readmessage"));
        currentState = ModelStateEnum.READING_MESSAGE;
    }

    public boolean gotoReadMessagePageGuard() {
        return currentState == ModelStateEnum.LOGGED_IN;
    }

    @Action
    public void consumeAnotherMessage() {
        driver.findElement(By.id("consume")).click();

        Assert.assertTrue(driver.getCurrentUrl().endsWith(baseUrl + "/readmessage"));
        currentState = ModelStateEnum.READING_MESSAGE;
    }

    public boolean consumeAnotherMessageGuard() {
        return currentState == ModelStateEnum.HAS_READ_MESSAGE;
    }

    @Action
    public void consumeMessage() {
        Assert.assertTrue(driver.getCurrentUrl().endsWith(baseUrl + "/readmessage"));
        if (messagesRecv == 0) {
            Assert.assertEquals("You have no new messages.", driver.findElement(By.id("messageContainer")).getText());
            Assert.assertEquals("Try again", driver.findElement(By.id("consume")).getText());
        } else {
            Assert.assertNotEquals("You have no new messages.", driver.findElement(By.id("messageContainer")).getText());
            Assert.assertEquals("Consume another message", driver.findElement(By.id("consume")).getText());
            messagesRecv--;
            if (sessionMessagesRecv != 0) {
                sessionMessagesRecv--;
            }
        }

        currentState = ModelStateEnum.HAS_READ_MESSAGE;
    }

    public boolean consumeMessageGuard() {
        return currentState == ModelStateEnum.READING_MESSAGE;
    }

    @Action
    public void goBack() {
        driver.findElement(By.id("backToMailbox")).click();
        Assert.assertTrue(driver.getCurrentUrl().endsWith(baseUrl + "/loggedin"));
        currentState = ModelStateEnum.LOGGED_IN;
    }

    public boolean goBackGuard() {
        return currentState == ModelStateEnum.SENDING_MESSAGE
                || currentState == ModelStateEnum.HAS_READ_MESSAGE;
    }

    @Action
    public void logout() {
        driver.findElement(By.id("logout")).click();
        Assert.assertTrue(driver.getCurrentUrl().endsWith(baseUrl + "/register"));
        currentState = ModelStateEnum.UNREGISTERED;
    }

    public boolean logoutGuard() {
        return currentState == ModelStateEnum.LOGGED_IN;
    }

    @Action
    public void autoLogoutOnReceivingMaxMessages() {
        final WebDriver driver2 = new ChromeDriver();
        String agent2ID = createUniqueAgent2Helper(driver2);
        String LOGIN_KEY = driver2.findElement(By.id("lKey")).getText();
        loginAgentHelper(driver2, LOGIN_KEY);
        driver2.findElement(By.id("sendMessage")).click();
        for (; min(sessionMessagesRecv, messagesRecv) < MAX_MESSAGE_RECEIVED; sessionMessagesRecv++, messagesRecv++) {
            sendMessageHelper(driver2, agentID, "Message " + sessionMessagesRecv);
        }
    
        driver2.get(baseUrl + "/register");
        registerAgentHelper(driver2, agent2ID);
        LOGIN_KEY = driver2.findElement(By.id("lKey")).getText();
        loginAgentHelper(driver2, LOGIN_KEY);
        driver2.findElement(By.id("sendMessage")).click();
        for (; min(sessionMessagesRecv, messagesRecv) <= MAX_MESSAGE_RECEIVED + 2; sessionMessagesRecv++, messagesRecv++) {
            System.out.println(messagesRecv);
            System.out.println(sessionMessagesRecv);
            sendMessageHelper(driver2, agentID, "Message " + sessionMessagesRecv);
        }
        driver2.quit();
        
        driver.get(baseUrl + "/loggedin");
        Assert.assertTrue(driver.getCurrentUrl().endsWith(baseUrl + "/register"));
        Assert.assertEquals("You were logged out of the system.", driver.findElement(By.id("notif")).getText());
        
        currentState = ModelStateEnum.UNREGISTERED;
    }
    
    private int min(int n1, int n2) {
        if(n1 < n2) {
            return  n1;
        }
        
        return n2;
    }
    
    public boolean autoLogoutOnReceivingMaxMessagesGuard() {
        return currentState == ModelStateEnum.LOGGED_IN ||
                currentState == ModelStateEnum.SENDING_MESSAGE ||
                currentState == ModelStateEnum.HAS_READ_MESSAGE;
    }

    @Action
    public void autoLogoutOnSendingMaxMessages() {
        final WebDriver driver2 = new ChromeDriver();
        final String AGENT_2_ID = "agent2ID";
        registerAgentHelper(driver2, AGENT_2_ID);
        driver2.quit();

        for (; sessionMessagesSent <= MAX_MESSAGES_SENT +1; sessionMessagesSent++) {
            sendMessageHelper(driver, AGENT_2_ID, "Message " + sessionMessagesSent);
        }

        Assert.assertTrue(driver.getCurrentUrl().endsWith(baseUrl + "/register"));
        Assert.assertEquals("You were logged out of the system.", driver.findElement(By.id("notif")).getText());

        currentState = ModelStateEnum.UNREGISTERED;
    }

    public boolean autoLogoutOnSendingMaxMessagesGuard() {
        return currentState == ModelStateEnum.SENDING_MESSAGE;
    }


    @Override
    public Object getState() {
        return currentState;
    }

    @Override
    public void reset(boolean driverReset) {
        currentState = ModelStateEnum.UNREGISTERED;
        agentID = Utils.getNRandomCharacters(5);
        loginKey = null;
        sessionMessagesSent = 0;
        sessionMessagesRecv = 0;
        messagesRecv = 0;

        if (driverReset) {
            driver.quit();
            driver = new ChromeDriver();
        }
    }

    @Test
    public void main() {
        final Instant startTime = Instant.now();
        final Instant finishTime = startTime.plus(TEST_DURATION);

        System.setProperty("webdriver.chrome.driver", "chromedriver");

        final Tester tester = new GreedyTester(new AssignmentModel());
        tester.setRandom(new Random());
        GraphListener gl = tester.buildGraph();
        tester.addListener(new StopOnFailureListener());
        tester.addCoverageMetric(new TransitionCoverage());
        tester.addCoverageMetric(new TransitionPairCoverage());
        tester.addCoverageMetric(new StateCoverage());
        tester.addCoverageMetric(new ActionCoverage());
        tester.addListener("verbose");
        while (Instant.now().isBefore(finishTime)) {
            tester.generate();
        }
        tester.printCoverage();
        
        try {
            gl.printGraphDot("graph.dot");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}