package nl.devon;

import org.joda.time.DateTime;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

public class DelayedVerificationSteps {

	private DelayedVerification verification;
	private DelayedVerificationStore verificationStore;
	private TestExecutionContext context;
	private PersistableTestData testData;

	public void setTestExecutionContext(TestExecutionContext executionContext) {
		context = executionContext;
	}

	public void setPersistableTestData(PersistableTestData testData) {
		this.testData = testData;
	}

	public void setDelayedVerificationStore(DelayedVerificationStore delayedVerificationStore) {
		verificationStore = delayedVerificationStore;
	}

	@Then("^after (.*) \\(dv-checksum=(.+)\\)$")
	public void initiateDelayedVerification(String expression, String checksum) {
		verification = new DelayedVerification(DateTime.now(), checksum);
		verificationStore.save(verification);

		if (context != null) {
			context.set(verification);
		}

		if (testData != null) {
			testData.save(verification);
		}
	}

	@Given("^Test Execution Context is loaded with dv-id=(.+)$")
	public void testExecutionContextIsLoadedWithDvId(String dvId) {
		verification = verificationStore.load(dvId);
		if (context != null) {
			context.set(verification);
			if (testData != null) {
				testData.load(verification);
			}
		}
	}
}
