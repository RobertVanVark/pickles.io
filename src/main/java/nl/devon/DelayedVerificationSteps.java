package nl.devon;

import org.joda.time.DateTime;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

public class DelayedVerificationSteps {

	private DelayedVerification verification;
	private DelayedVerificationStore storage;
    private TestExecutionContext context;

    public void setDelayedVerificationStore(DelayedVerificationStore store) {
		storage = store;
	}

	@Then("^after (.*) \\(dv-checksum=(.+)\\)$")
	public void initiateDelayedVerification(String expression, String checksum) {
		verification = new DelayedVerification(DateTime.now(), checksum);
		storage.save(verification);
		if (context != null) {
            context.save(verification);
        }
	}

	@Given("^Test Execution Context is loaded with dv-id=(.+)$")
	public void testExecutionContextIsLoadedWithDvId(String dvId) {
		verification = storage.load(dvId);
		if (context != null) {
		    context.load(verification);
        }
	}

    public void setContext(TestExecutionContext context) {
        this.context = context;
    }

    public DelayedVerification getDelayedVerification() {
        return verification;
    }
}
