package modeltesting;

import nz.ac.waikato.modeljunit.*;
import nz.ac.waikato.modeljunit.coverage.ActionCoverage;
import nz.ac.waikato.modeljunit.coverage.StateCoverage;
import nz.ac.waikato.modeljunit.coverage.TransitionCoverage;
import nz.ac.waikato.modeljunit.coverage.TransitionPairCoverage;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import util.Utils;

import java.io.FileNotFoundException;
import java.time.Duration;
import java.time.Instant;
import java.util.Random;


public class AssignmentModel implements FsmModel {

    private static final String baseUrl = "localhost:" + webapp.StartJettyHandler.PORT_NUMBER;
    private WebDriver driver = null;
    private WebDriver driver2 = new ChromeDriver();

    private ModelStateEnum currentState = ModelStateEnum.UNREGISTERED;
    private final Duration TEST_DURATION = Duration.ofMinutes(15);

    private final int MAX_MESSAGE_LENGTH = 140;
    private final int MAX_MESSAGES_SENT = 25;
    private final int MAX_MESSAGE_RECEIVED = 25;

    private String agentID;
    private String loginKey;
    private int sessionMessagesSent;
    private int sessionMessagesRecv;
    private int messagesInMailbox;

    private boolean agentRegisteredInSystem;
    private boolean agentWasAutoLoggedOut;

    private long uniqueness = 0;

    @Action
    public void normalRegister() {
        agentWasAutoLoggedOut = false;

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
        agentWasAutoLoggedOut = false;

        loginAgentHelper(driver, driver.findElement(By.id("lKey")).getText());
        Assert.assertTrue(driver.getCurrentUrl().endsWith(baseUrl + "/loggedin"));
        agentRegisteredInSystem = true;
        sessionMessagesSent = 0;
        sessionMessagesRecv = 0;
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

        if (common_checkAgentWasNotAutoLoggedOut(driver)) {
            Assert.assertTrue(driver.getCurrentUrl().endsWith(baseUrl + "/sendmessage"));
            currentState = ModelStateEnum.SENDING_MESSAGE;
        }
    }

    public boolean gotoSendMessagePageGuard() {
        return currentState == ModelStateEnum.LOGGED_IN;
    }

    @Action
    public void sendNormalMessage() {
        sendMessageHelper(driver, agentID, "Msg");

        if (sessionMessagesRecv >= MAX_MESSAGE_RECEIVED) {
            sessionMessagesSent = 0;
            sessionMessagesRecv = 0;
            agentWasAutoLoggedOut = true;
        }
        common_checkThatSuccessfulOrAgentLoggedOut();
    }

    public boolean sendNormalMessageGuard() {
        return currentState == ModelStateEnum.SENDING_MESSAGE;
    }

    @Action
    public void sendLongMessage() {
        sendMessageHelper(driver, agentID, Utils.getNCharacters(MAX_MESSAGE_LENGTH + 10));

        // Note: agent cannot be logged out since no message was sent
        // but may have still been logged out due to receiveMessage

        if (common_checkAgentWasNotAutoLoggedOut(driver)) {
            Assert.assertTrue(driver.getCurrentUrl().endsWith(baseUrl + "/sendmessage"));
            final String notificationText = driver.findElement(By.id("notif")).getText();
            Assert.assertTrue(notificationText.equals("Message not sent since it is longer than 140 characters."));
            currentState = ModelStateEnum.SENDING_MESSAGE;
        }
    }

    public boolean sendLongMessageGuard() {
        return currentState == ModelStateEnum.SENDING_MESSAGE;
    }

    @Action
    public void sendMessageWithBlockedWords() {
        sendMessageHelper(driver, agentID, "Get the nuclEAR REcipe with GinGer");

        if (sessionMessagesRecv >= MAX_MESSAGE_RECEIVED) {
            sessionMessagesSent = 0;
            sessionMessagesRecv = 0;
            agentWasAutoLoggedOut = true;
        }
        common_checkThatSuccessfulOrAgentLoggedOut();
    }

    public boolean sendMessageWithBlockedWordsGuard() {
        return currentState == ModelStateEnum.SENDING_MESSAGE;
    }

    @Action
    public void sendMessageToNonExistentTarget() {
        // "AGENT_2" prefix so that it doesn't clash with primary agent's id
        final String AGENT_2 = "AGENT_2_" + Utils.getNRandomCharacters(5);
        sendMessageHelper(driver, AGENT_2, "Msg");

        // Note: agent cannot be logged out since no message was sent
        // but may have still been logged out due to receiveMessage

        if (common_checkAgentWasNotAutoLoggedOut(driver)) {
            Assert.assertTrue(driver.getCurrentUrl().endsWith(baseUrl + "/sendmessage"));
            final String notificationText = driver.findElement(By.id("notif")).getText();
            Assert.assertTrue(notificationText.equals("Message not sent since the target agent does not exist."));
            currentState = ModelStateEnum.SENDING_MESSAGE;
        }
    }

    public boolean sendMessageToNonExistentTargetGuard() {
        return currentState == ModelStateEnum.SENDING_MESSAGE;
    }

    @Action
    public void gotoReadMessagePage() {
        driver.findElement(By.id("consumeMessage")).click();

        if (common_checkAgentWasNotAutoLoggedOut(driver)) {
            Assert.assertTrue(driver.getCurrentUrl().endsWith(baseUrl + "/readmessage"));
            currentState = ModelStateEnum.READING_MESSAGE;
        }
    }

    public boolean gotoReadMessagePageGuard() {
        return currentState == ModelStateEnum.LOGGED_IN;
    }

    @Action
    public void consumeAnotherMessage() {
        driver.findElement(By.id("consume")).click();

        if (common_checkAgentWasNotAutoLoggedOut(driver)) {
            Assert.assertTrue(driver.getCurrentUrl().endsWith(baseUrl + "/readmessage"));
            currentState = ModelStateEnum.READING_MESSAGE;
        }
    }

    public boolean consumeAnotherMessageGuard() {
        return currentState == ModelStateEnum.HAS_READ_MESSAGE;
    }

    @Action
    public void consumeMessage() {
        Assert.assertTrue(driver.getCurrentUrl().endsWith(baseUrl + "/readmessage"));

        if (messagesInMailbox == 0) {
            Assert.assertEquals("You have no new messages.", driver.findElement(By.id("messageContainer")).getText());
            Assert.assertEquals("Try again", driver.findElement(By.id("consume")).getText());
        } else {
            Assert.assertNotEquals("You have no new messages.", driver.findElement(By.id("messageContainer")).getText());
            Assert.assertEquals("Consume another message", driver.findElement(By.id("consume")).getText());
            messagesInMailbox--;
        }
        currentState = ModelStateEnum.HAS_READ_MESSAGE;
    }

    public boolean consumeMessageGuard() {
        return currentState == ModelStateEnum.READING_MESSAGE;
    }

    @Action
    public void goBack() {
        driver.findElement(By.id("backToMailbox")).click();

        if (common_checkAgentWasNotAutoLoggedOut(driver)) {
            Assert.assertTrue(driver.getCurrentUrl().endsWith(baseUrl + "/loggedin"));
            currentState = ModelStateEnum.LOGGED_IN;
        }
    }

    public boolean goBackGuard() {
        return currentState == ModelStateEnum.SENDING_MESSAGE
                || currentState == ModelStateEnum.HAS_READ_MESSAGE;
    }

    @Action
    public void manualLogout() {
        driver.findElement(By.id("logout")).click();
        Assert.assertTrue(driver.getCurrentUrl().endsWith(baseUrl + "/register"));
        sessionMessagesSent = 0;
        sessionMessagesRecv = 0;
        currentState = ModelStateEnum.UNREGISTERED;
    }

    public boolean manualLogoutGuard() {
        return currentState == ModelStateEnum.LOGGED_IN;
    }

    @Action
    public void receiveMessage() {
        // Sender registers ("AGENT_2" prefix for no clashes)
        final String AGENT_2 = "AGENT_2_" + Utils.getNRandomCharacters(5);
        registerAgentHelper(driver2, AGENT_2);

        // Sender logs in
        final String tempLoginKey = driver2.findElement(By.id("lKey")).getText();
        loginAgentHelper(driver2, tempLoginKey);

        // Sender sends the message
        Assume.assumeTrue(driver2.getCurrentUrl().endsWith(baseUrl + "/loggedin"));
        driver2.findElement(By.id("sendMessage")).click();
        Assume.assumeTrue(driver2.getCurrentUrl().endsWith(baseUrl + "/sendmessage"));
        sendMessageHelper(driver2, agentID, "Msg " + sessionMessagesRecv);

        final String notificationText = driver2.findElement(By.id("notif")).getText();
        if (sessionMessagesRecv < MAX_MESSAGE_RECEIVED) {
            Assert.assertTrue(notificationText.equals("Message sent successfully."));
            sessionMessagesRecv++;
            messagesInMailbox++;
        } else {
            Assert.assertTrue(notificationText.equals("Message not sent since target agent's quota exceeded."));
            sessionMessagesSent = 0;
            sessionMessagesRecv = 0;
            agentWasAutoLoggedOut = true;
        }
        // current state does not change
    }

    public boolean receiveMessageGuard() {
        return agentRegisteredInSystem && currentState != ModelStateEnum.READING_MESSAGE;
    }

    @Override
    public Object getState() {
        return currentState;
    }

    @Override
    public void reset(boolean driverReset) {
        currentState = ModelStateEnum.UNREGISTERED;
        agentID = (uniqueness++) + "_" + Utils.getNRandomCharacters(5);
        loginKey = null;
        sessionMessagesSent = 0;
        sessionMessagesRecv = 0;
        messagesInMailbox = 0;
        agentRegisteredInSystem = false;
        agentWasAutoLoggedOut = false;

        if (driverReset) {
            if (driver != null) driver.quit();
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

        driver2.quit();
    }

    private boolean common_checkAgentWasNotAutoLoggedOut(WebDriver driver) {
        if (!agentWasAutoLoggedOut) {
            return true;
        } else {
            Assert.assertTrue(driver.getCurrentUrl().endsWith(baseUrl + "/register"));
            currentState = ModelStateEnum.UNREGISTERED;
            return false;
        }
    }

    private void common_checkThatSuccessfulOrAgentLoggedOut() {
        if (sessionMessagesSent >= MAX_MESSAGES_SENT || agentWasAutoLoggedOut) {
            Assert.assertTrue(driver.getCurrentUrl().endsWith(baseUrl + "/register"));
            currentState = ModelStateEnum.UNREGISTERED;
        } else {
            Assert.assertTrue(driver.getCurrentUrl().endsWith(baseUrl + "/sendmessage"));
            final String notificationText = driver.findElement(By.id("notif")).getText();
            Assert.assertTrue(notificationText.equals("Message sent successfully."));
            sessionMessagesSent++;
            sessionMessagesRecv++;
            messagesInMailbox++;
            currentState = ModelStateEnum.SENDING_MESSAGE;
        }
    }

    /**
     * Registers the agent. No checks are done if this was successful
     *
     * @param driver  the WebDriver to use
     * @param agentId the agent id to login
     */
    private static void registerAgentHelper(WebDriver driver, String agentId) {
        driver.get(baseUrl + "/register");
        Assume.assumeTrue(driver.getCurrentUrl().endsWith(baseUrl + "/register"));

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
        Assume.assumeTrue(driver.getCurrentUrl().endsWith(baseUrl + "/login"));

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
        Assume.assumeTrue(driver.getCurrentUrl().endsWith(baseUrl + "/sendmessage"));

        driver.findElement(By.id("destination")).click();
        driver.findElement(By.id("destination")).sendKeys(targetAgentId);
        driver.findElement(By.id("messageBody")).click();
        driver.findElement(By.id("messageBody")).sendKeys(message);
        driver.findElement(By.id("submit")).click();
    }
}