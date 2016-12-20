package nl.devon;

import org.joda.time.DateTime;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

public class DelayedVerificationSteps {

	private DelayedVerification verification;
	private DelayedVerificationStore storage;

	public DelayedVerificationSteps(DelayedVerificationStore storage) {
		this.storage = storage;
	}

	@Then("^after (.*) \\(dv-checksum=(.+)\\)$")
	public void initiateDelayedVerification(String expression, String checksum) {
		verification = new DelayedVerification(DateTime.now(), checksum);
		storage.save(verification);
	}

	@Given("^Test Execution Context is loaded with dv-id=(.+)$")
	public void testExecutionContextIsLoadedWithDvId(String dvId) {
		storage.load(dvId);
		System.out.println("Load Test Execution Context with id=" + dvId);
	}

	public DelayedVerification getDelayedVerification() {
		return verification;
	}
}
