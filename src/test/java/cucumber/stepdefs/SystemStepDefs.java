package cucumber.stepdefs;

import cucumber.api.PendingException;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.junit.Assert;
import org.junit.Assume;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import static system.MessagingSystem.LOGIN_KEY_LENGTH;
import static webapp.StartJettyHandler.PORT_NUMBER;

public class SystemStepDefs {

    private WebDriver driver;
    private final static String baseUrl = "localhost:" + PORT_NUMBER;
    private final static String AGENT_ID = "1234xy";
    private final static String OTHER_AGENT_ID = "4567ab";

    @Before
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "chromedriver");
        driver = new ChromeDriver();
    }

    @After
    public void tearDown() {
        driver.quit();
    }

    @Given("^I am an agent trying to log in$")
    public void i_am_an_agent_trying_to_log_in() {
        driver.get(baseUrl + "/register");
    }

    @When("^I obtain a key from the supervisor using a valid id$")
    public void i_obtain_a_key_from_the_supervisor_using_a_valid_id() {
        Assume.assumeTrue(driver.getCurrentUrl().endsWith(baseUrl + "/register"));

        driver.findElement(By.id("idInput")).click();
        driver.findElement(By.id("idInput")).sendKeys(AGENT_ID);
        driver.findElement(By.id("submit")).click();
    }

    @Then("^the supervisor should give me a valid key$")
    public void the_supervisor_should_give_me_a_valid_key() {
        Assume.assumeTrue(driver.getCurrentUrl().endsWith(baseUrl + "/login"));

        final String loginKey = driver.findElement(By.id("lKey")).getText();
        Assert.assertTrue(loginKey.length() == LOGIN_KEY_LENGTH);
    }

    @When("^I log in using that key$")
    public void i_log_in_using_that_key() {
        Assume.assumeTrue(driver.getCurrentUrl().endsWith(baseUrl + "/login"));

        final String loginKey = driver.findElement(By.id("lKey")).getText();
        driver.findElement(By.id("lKeyInput")).click();
        driver.findElement(By.id("lKeyInput")).sendKeys(loginKey);
        driver.findElement(By.id("submit")).click();
    }

    @Then("^I should be allowed to log in$")
    public void i_should_be_allowed_to_log_in() {
        System.out.println(driver.getCurrentUrl());
        Assert.assertTrue(driver.getCurrentUrl().endsWith(baseUrl + "/sendmail"));
    }

    @When("^I wait for (\\d+) seconds$")
    public void i_wait_for_seconds(int arg1) throws InterruptedException {
        Thread.sleep(arg1 * 1000);
    }

    @Then("^I should not be allowed to log in$")
    public void i_should_not_be_allowed_to_log_in() {
        Assert.assertTrue(driver.getCurrentUrl().endsWith(baseUrl + "/login"));
    }

    @Given("^I am a logged in agent$")
    public void i_am_a_logged_in_agent() {
        driver.get(baseUrl + "/register");

        driver.findElement(By.id("idInput")).click();
        driver.findElement(By.id("idInput")).sendKeys(AGENT_ID);
        driver.findElement(By.id("submit")).click();

        final String loginKey = driver.findElement(By.id("lKey")).getText();
        driver.findElement(By.id("lKeyInput")).click();
        driver.findElement(By.id("lKeyInput")).sendKeys(loginKey);
        driver.findElement(By.id("submit")).click();
    }

    @When("^I attempt to send (\\d+) messages$")
    public void i_attempt_to_send_messages(int arg1) {
        for (int i = 0; i < arg1; i++) {
            driver.findElement(By.id("destination")).click();
            driver.findElement(By.id("destination")).sendKeys(AGENT_ID);
            driver.findElement(By.id("messageBody")).click();
            driver.findElement(By.id("messageBody")).sendKeys("message_" + i);
            driver.findElement(By.id("submit")).click();
        }
    }

    @Then("^the messages should be successfully sent$")
    public void the_messages_should_be_successfully_sent() {
        final String notificationText = driver.findElement(By.id("notif")).getText();
        Assert.assertTrue(notificationText.equals("Message Sent Successfully"));
    }

    @When("^I try to send another message$")
    public void i_try_to_send_another_message() {
        driver.findElement(By.id("destination")).click();
        driver.findElement(By.id("destination")).sendKeys(AGENT_ID);
        driver.findElement(By.id("messageBody")).click();
        driver.findElement(By.id("messageBody")).sendKeys("Another message");
        driver.findElement(By.id("submit")).click();
    }

    @Then("^the system will inform me that I have exceeded my quota$")
    public void the_system_will_inform_me_that_I_have_exceeded_my_quota() { // todo: "exceeded your quota"
        final String notificationText = driver.findElement(By.id("notif")).getText();
        Assert.assertTrue(notificationText.equals("Failed to add the message to the destination mailbox"));
    }

    @Then("^I will be logged out$")
    public void i_will_be_logged_out() {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @When("^I attempt to send the message Hello there to another agent$")
    public void i_attempt_to_send_the_message_Hello_there_to_another_agent() {
        driver.findElement(By.id("destination")).click();
        driver.findElement(By.id("destination")).sendKeys(OTHER_AGENT_ID);
        driver.findElement(By.id("messageBody")).click();
        driver.findElement(By.id("messageBody")).sendKeys("Hello there");
        driver.findElement(By.id("submit")).click();
    }

    @Then("^the other agent should receive the message Hello there$")
    public void the_other_agent_should_receive_the_message_Hello_there() {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @When("^I attempt to send the message Send recipe now to another agent$")
    public void i_attempt_to_send_the_message_Send_recipe_now_to_another_agent() {
        driver.findElement(By.id("destination")).click();
        driver.findElement(By.id("destination")).sendKeys(OTHER_AGENT_ID);
        driver.findElement(By.id("messageBody")).click();
        driver.findElement(By.id("messageBody")).sendKeys("Send recipe now");
        driver.findElement(By.id("submit")).click();
    }

    @Then("^the other agent should receive the message Send now$")
    public void the_other_agent_should_receive_the_message_Send_now() {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @When("^I attempt to send the message Nuclear recipe is ready to another agent$")
    public void i_attempt_to_send_the_message_Nuclear_recipe_is_ready_to_another_agent() {
        driver.findElement(By.id("destination")).click();
        driver.findElement(By.id("destination")).sendKeys(OTHER_AGENT_ID);
        driver.findElement(By.id("messageBody")).click();
        driver.findElement(By.id("messageBody")).sendKeys("Nuclear recipe");
        driver.findElement(By.id("submit")).click();
    }

    @Then("^the other agent should receive the message ready$")
    public void the_other_agent_should_receive_the_message_ready() {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @When("^I attempt to send the message GinGer nuclear RECipE\\. to another agent$")
    public void i_attempt_to_send_the_message_GinGer_nuclear_RECipE_to_another_agent() {
        driver.findElement(By.id("destination")).click();
        driver.findElement(By.id("destination")).sendKeys(OTHER_AGENT_ID);
        driver.findElement(By.id("messageBody")).click();
        driver.findElement(By.id("messageBody")).sendKeys("GinGer nuclear RECipE.");
        driver.findElement(By.id("submit")).click();
    }

    @Then("^the other agent should receive the message \\.$")
    public void the_other_agent_should_receive_the_message() {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @When("^I click on “Log out”$")
    public void i_click_on_Log_out() {
        // Write code here that turns the phrase above into concrete actions
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("^I should be logged out$")
    public void i_should_be_logged_out() {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }
}
