package modeltesting;

import nz.ac.waikato.modeljunit.Action;
import nz.ac.waikato.modeljunit.FsmModel;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import util.Utils;

import java.util.Random;

import static webapp.StartJettyHandler.PORT_NUMBER;

public class AssignmentModel implements FsmModel {
    
    private final static String baseUrl = "localhost:" + PORT_NUMBER;
    
    private WebDriver driver = new ChromeDriver();
    private ModelStateEnum currentState = ModelStateEnum.UNREGISTERED;
    
    private String agentID = null;
    private String loginKey = null;
    
    @Action
    public void register() {
        currentState = ModelStateEnum.SUPERVISOR_CHECKING;
    }

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
    public void validLogin() {
        loginKey = driver.findElement(By.id("lKey")).getText();
        loginAgent(driver, loginKey);

        Assert.assertTrue(driver.getCurrentUrl().endsWith(baseUrl + "/sendmail"));
        currentState = ModelStateEnum.LOGIN_KEY_CHECKING;
    }
    
    public boolean validLoginGuard() {
        return currentState == ModelStateEnum.REGISTERED;
    }

    @Action
    public void invalidLogin() {
        loginAgent(driver, "invalidLoginKey");

        Assert.assertTrue(driver.getCurrentUrl().endsWith(baseUrl + "/login"));
        currentState = ModelStateEnum.LOGIN_KEY_CHECKING;
    }

    public boolean invalidLoginGuard() {
        return currentState == ModelStateEnum.REGISTERED;
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
