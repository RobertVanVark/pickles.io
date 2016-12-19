package nl.devon;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import org.joda.time.DateTime;

public class DelayedVerificationSteps {

    private DelayedVerification verification;

    public DelayedVerificationSteps() {
    }

    @Then("^after (.*) \\(dv-checksum=(.+)\\)$")
    public void initiateDelayedVerification(String expression, String checksum) {
        verification = new DelayedVerification(DateTime.now(), checksum);
    }

    @Given("^Test Execution Context is loaded with dv-id=(.+)$")
    public void testExecutionContextIsLoadedWithDvId(String dvId)  {
        System.out.println("Load Test Execution Context with id=" + dvId);
    }


    public DelayedVerification getDelayedVerification() {
        return verification;
    }
}
