package nl.devon.pickles.steps;

import org.joda.time.DateTime;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

public class DelayedVerificationSteps {

	static final String DELAY_EXPRESSION = TimeOffsetDelay.EXPRESSION;

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

	@Then("^after (" + DELAY_EXPRESSION + ") (.*) \\(dv-checksum=(\\w{32})\\)$")
	public void initiateDelayedVerification(String expression, String stepdef, String checksum) {

		Delay delay = new TimeOffsetDelay(expression);
		DateTime verifyAt = delay.getVerifyAt(context);

		verification = new DelayedVerification(verifyAt, checksum, "");
		verificationStore.create(verification);

		context.set(verification);

		if (testData != null) {
			testData.save(verification);
		}
	}

	@Given("^Test Execution Context is loaded with dv-id=(.+)$")
	public void testExecutionContextIsLoadedForDvId(String dvId) {
		verification = verificationStore.read(dvId);

		context.set(verification);

		if (testData != null) {
			testData.load(verification);
		}
	}
}
