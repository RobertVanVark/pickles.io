package io.pickles.steps;

import org.joda.time.DateTime;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import io.pickles.steps.delays.Delay;
import io.pickles.steps.delays.DelayFactory;

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

	@Then("^(" + DelayFactory.DELAY_EXPRESSION + ") (.*) \\(dvChecksum=(.+), dvId=(.+), dvFeatureUri=(.+)\\)$")
	public void initiateDelayedVerification(String expression, String stepdef, String checksum, String id,
			String featureUri) {

		Delay delay = DelayFactory.create(expression);
		DateTime verifyAt = delay.getVerifyAt(context);

		verification = new DelayedVerification(id, verifyAt, checksum, featureUri);
		verificationStore.create(verification);

		context.set(verification);

		if (testData != null) {
			testData.save(verification);
		}
	}

	@Given("^Test Execution Context is loaded for dvId=(.+)$")
	public void testExecutionContextIsLoadedForDvId(String dvId) {
		verification = verificationStore.read(dvId);

		context.set(verification);

		if (testData != null) {
			testData.load(verification);
		}
	}
}
