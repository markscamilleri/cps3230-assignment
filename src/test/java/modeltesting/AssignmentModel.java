package modeltesting;

import nz.ac.waikato.modeljunit.Action;
import nz.ac.waikato.modeljunit.FsmModel;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class AssignmentModel implements FsmModel {
    WebDriver driver = new ChromeDriver();
    
    ModelStateEnum currentState =  ModelStateEnum.UNREGISTERED;
    
    @Action
    private void login(){
    
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
