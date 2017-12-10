package cucumber.stepdefs;

import cucumber.api.PendingException;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class SystemStepDefs {

    private WebDriver driver;

    @Before
    public void setUp() throws Exception {
        System.setProperty("webdriver.chrome.driver", "chromedriver");
        driver = new ChromeDriver();
    }

    @After
    public void tearDown() throws Exception {
        driver.quit();
    }

    @Given("^I am an agent trying to log in$")
    public void i_am_an_agent_trying_to_log_in() throws Exception {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @When("^I obtain a key from the supervisor using a valid id$")
    public void i_obtain_a_key_from_the_supervisor_using_a_valid_id() throws Exception {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("^the supervisor should give me a valid key$")
    public void the_supervisor_should_give_me_a_valid_key() throws Exception {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @When("^I log in using that key$")
    public void i_log_in_using_that_key() throws Exception {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("^I should be allowed to log in$")
    public void i_should_be_allowed_to_log_in() throws Exception {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @When("^I wait for (\\d+) seconds$")
    public void i_wait_for_seconds(int arg1) throws Exception {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("^I should not be allowed to log in$")
    public void i_should_not_be_allowed_to_log_in() throws Exception {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Given("^I am a logged in agent$")
    public void i_am_a_logged_in_agent() throws Exception {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @When("^I attempt to send (\\d+) messages$")
    public void i_attempt_to_send_messages(int arg1) throws Exception {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("^the messages should be successfully sent$")
    public void the_messages_should_be_successfully_sent() throws Exception {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @When("^I try to send another message$")
    public void i_try_to_send_another_message() throws Exception {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("^the system will inform me that I have exceeded my quota$")
    public void the_system_will_inform_me_that_I_have_exceeded_my_quota() throws Exception {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("^I will be logged out$")
    public void i_will_be_logged_out() throws Exception {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @When("^I attempt to send the message Hello there to another agent$")
    public void i_attempt_to_send_the_message_Hello_there_to_another_agent() throws Exception {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("^the other agent should receive the message Hello there$")
    public void the_other_agent_should_receive_the_message_Hello_there() throws Exception {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @When("^I attempt to send the message Send recipe now to another agent$")
    public void i_attempt_to_send_the_message_Send_recipe_now_to_another_agent() throws Exception {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("^the other agent should receive the message Send now$")
    public void the_other_agent_should_receive_the_message_Send_now() throws Exception {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @When("^I attempt to send the message Nuclear recipe is ready to another agent$")
    public void i_attempt_to_send_the_message_Nuclear_recipe_is_ready_to_another_agent() throws Exception {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("^the other agent should receive the message ready$")
    public void the_other_agent_should_receive_the_message_ready() throws Exception {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @When("^I attempt to send the message GinGer nuclear RECipE\\. to another agent$")
    public void i_attempt_to_send_the_message_GinGer_nuclear_RECipE_to_another_agent() throws Exception {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("^the other agent should receive the message \\.$")
    public void the_other_agent_should_receive_the_message() throws Exception {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }
}
