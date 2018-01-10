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
    
    private String agentID;
    
    @Action
    public void register() {
        driver.get(baseUrl + "/register");
        
        agentID = new Random().nextBoolean() ? "" : "spy-";
        agentID += Utils.getNRandomCharacters(5);
        
        driver.findElement(By.id("idInput")).click();
        driver.findElement(By.id("idInput")).sendKeys(agentID);
        driver.findElement(By.id("submit")).click();
        
        currentState = ModelStateEnum.SUPERVISOR_CHECKING;
    }
    
    public boolean registerGuard() {
        return currentState == ModelStateEnum.UNREGISTERED;
    }
    
    @Action
    public void supervisorCheck() {
        if (agentID.startsWith("spy-")) {
            Assert.assertTrue(driver.getCurrentUrl().endsWith(baseUrl + "/register"));
            currentState = ModelStateEnum.UNREGISTERED;
        } else {
            Assert.assertTrue(driver.getCurrentUrl().endsWith(baseUrl + "/login"));
            currentState = ModelStateEnum.REGISTERED;
        }
    }
    
    public boolean supervisorCheckGuard() {
        return currentState == ModelStateEnum.SUPERVISOR_CHECKING;
    }
    
    @Action
    public void login() {
        final String LOGIN_KEY = driver.findElement(By.id("lKey")).getText();
        
        driver.findElement(By.id("lKeyInput")).click();
        driver.findElement(By.id("lKeyInput")).sendKeys(LOGIN_KEY);
        driver.findElement(By.id("submit")).click();
        
        currentState = ModelStateEnum.LOGIN_KEY_CHECKING;
    }
    
    public boolean loginGuard() {
        return currentState == ModelStateEnum.REGISTERED;
    }
    
    @Action
    public void checkLoginKey() {
    
    }
    
    public boolean checkLoginKeyGuard() {
        return currentState == ModelStateEnum.LOGIN_KEY_CHECKING;
    }
    
    
    @Override
    public Object getState() {
        return currentState;
    }
    
    @Override
    public void reset(boolean b) {
        currentState = ModelStateEnum.UNREGISTERED;
        
        if (b) {
            driver = new ChromeDriver();
        }
    }
}
