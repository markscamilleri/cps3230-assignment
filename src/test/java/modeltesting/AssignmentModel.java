package modeltesting;

import nz.ac.waikato.modeljunit.Action;
import nz.ac.waikato.modeljunit.FsmModel;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import util.Utils;

public class AssignmentModel implements FsmModel {

    private final static String baseUrl = "localhost:" + webapp.StartJettyHandler.PORT_NUMBER;

    private WebDriver driver = new ChromeDriver();
    private ModelStateEnum currentState = ModelStateEnum.UNREGISTERED;

    private String agentID = null;
    private String loginKey = null;

    @Action
    public void normalRegister() {
        agentID = Utils.getNRandomCharacters(5);
        registerAgent(driver, agentID);

        Assert.assertTrue(driver.getCurrentUrl().endsWith(baseUrl + "/login"));
        currentState = ModelStateEnum.REGISTERED;
    }

    public boolean normalRegisterGuard() {
        return currentState == ModelStateEnum.UNREGISTERED;
    }

    @Action
    public void spyRegister() {
        agentID = "spy-" + Utils.getNRandomCharacters(5);
        registerAgent(driver, agentID);

        Assert.assertTrue(driver.getCurrentUrl().endsWith(baseUrl + "/register"));
        currentState = ModelStateEnum.UNREGISTERED;
    }

    public boolean spyRegisterGuard() {
        return currentState == ModelStateEnum.UNREGISTERED;
    }

    @Action
    public void validLoginKeyLogin() {
        loginKey = driver.findElement(By.id("lKey")).getText();
        loginAgent(driver, loginKey);

        Assert.assertTrue(driver.getCurrentUrl().endsWith(baseUrl + "/sendmail"));
        currentState = ModelStateEnum.LOGGED_IN;
    }

    public boolean validLoginKeyLoginGuard() {
        return currentState == ModelStateEnum.REGISTERED;
    }

    @Action
    public void invalidLoginKeyLogin() {
        loginAgent(driver, "invalidLoginKey");

        Assert.assertTrue(driver.getCurrentUrl().endsWith(baseUrl + "/login"));
        currentState = ModelStateEnum.LOGGED_IN;
    }

    public boolean invalidLoginKeyLoginGuard() {
        return currentState == ModelStateEnum.REGISTERED;
    }

    @Action
    public void sendMessage() {
        // todo: should probably create multiple for valid/invalid
        // - sendLongMessage
        // - sendMessageWithBlockedWords
        // - sendNormalMessage
        // todo: should probably apply the following change in system's sendMessage
        // - FROM: final AgentInfo targetAgentInfo = agentInfos.get(targetAgentId);
        // - TO:   final AgentInfo targetAgentInfo = agentInfos.computeIfAbsent(targetAgentId, AgentInfo::new);
    }

    public boolean sendMessageGuard() {
        return currentState == ModelStateEnum.LOGGED_IN
                || currentState == ModelStateEnum.SENDING_MESSAGE;
    }

    @Action
    public void readMessage() {
        // todo
        // - readMessageWithEmptyMailbox
        // - readMessageWithNonEmptyMailbox
    }

    public boolean readMessageGuard() {
        return currentState == ModelStateEnum.LOGGED_IN
                || currentState == ModelStateEnum.READING_MESSAGE;
    }

    @Action
    public void goBack() {
        // todo (note that this covers both sendmessage and readmessage pages)
    }

    public boolean goBackGuard() {
        return currentState == ModelStateEnum.SENDING_MESSAGE
                || currentState == ModelStateEnum.READING_MESSAGE;
    }

    @Override
    public Object getState() {
        return currentState;
    }

    @Override
    public void reset(boolean b) {
        currentState = ModelStateEnum.UNREGISTERED;
        agentID = null;
        loginKey = null;

        if (b) {
            driver = new ChromeDriver();
        }
    }

    /**
     * Registers the agent. No checks are done if this was successful
     *
     * @param driver  the WebDriver to use
     * @param agentId the agent id to login
     */
    private static void registerAgent(WebDriver driver, String agentId) {
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
    private static void loginAgent(WebDriver driver, String loginKey) {
        driver.findElement(By.id("lKeyInput")).click();
        driver.findElement(By.id("lKeyInput")).sendKeys(loginKey);
        driver.findElement(By.id("submit")).click();
    }
}
