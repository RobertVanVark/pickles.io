package nl.devon;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

public class DelayedVerificationSteps {

    private Integer delayedVerification;


    public DelayedVerificationSteps() {
        delayedVerification = 0;
    }

    @Then("^after (.*) \\(dv-checksum=(.+)\\)$")
    public void initiateDelayedVerification(String expression, String checksum) {
        delayedVerification++;
    }

    @Given("^Test Execution Context is loaded with dv-id=(\\d+)$")
    public void testExecutionContextIsLoadedWithDvId(Integer dvId)  {
        System.out.println("Load Test Execution Context with id=" + dvId);
    }


    public String getDelayedVerification() {
        return delayedVerification.toString();
    }
}
