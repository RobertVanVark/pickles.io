package io.pickles.steps;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import io.pickles.steps.delays.Delay;
import io.pickles.steps.delays.DelayFactory;

/**
 * DelayedVerficationSteps implements the cucumber steps that are needed to handle delayed verifications. Your
 * implementation needs to provide a {@link PersistableTestData}, a {@link DelayedVerificationStore} and a
 * {@link TestExecutionContext}.
 */
public class DelayedVerificationSteps {

	private static final Logger LOGGER = LoggerFactory.getLogger(DelayedVerificationSteps.class);

	private DelayedVerification verification;
	private DelayedVerificationStore verificationStore;
	private TestExecutionContext context;
	private PersistableTestData testData;

	/**
	 * Sets the {@link TestExecutionContext} to be used by the step implementations.
	 *
	 * @param executionContext
	 *            {@link TestExecutionContext} to be used
	 *
	 */
	public void setTestExecutionContext(TestExecutionContext executionContext) {
		context = executionContext;
	}

	/**
	 * Sets the {@link PersistableTestData} to be used by the step implementations.
	 *
	 * @param testData
	 *            {@link PersistableTestData} to be used
	 *
	 */
	public void setPersistableTestData(PersistableTestData testData) {
		this.testData = testData;
	}

	/**
	 * Sets the {@link DelayedVerificationStore} to be used by the step implementations.
	 *
	 * @param delayedVerificationStore
	 *            {@link DelayedVerificationStore} to be used
	 *
	 */
	public void setDelayedVerificationStore(DelayedVerificationStore delayedVerificationStore) {
		verificationStore = delayedVerificationStore;
	}

	/**
	 * Generic step to trigger a verfication scenario. The method stores a {@link DelayedVerification} in the
	 * {@link @DelayedVerificationStore}. The delayed verification is linked to the feature/scenario to a calculated
	 * SHA1 checksum and the uri of the feature file.
	 *
	 * Note: This method should not be used directly.
	 *
	 * @param expression
	 * @param stepdef
	 * @param checksum
	 * @param id
	 * @param featureUri
	 *
	 */
	@Then("^(" + DelayFactory.DELAY_EXPRESSION + ") (.*) \\(dvChecksum=(.+), dvId=(.+), dvFeatureUri=(.+)\\)$")
	public void initiateDelayedVerification(String expression, String stepdef, String checksum, String id,
			String featureUri) {

		Delay delay = DelayFactory.create(expression);
		DateTime verifyAt = delay.getVerificationTime(context);

		verification = new DelayedVerification(id, verifyAt, checksum, featureUri);
		verificationStore.create(verification);

		context.set(verification);
		LOGGER.debug("storing delayed verification for " + stepdef + " (" + id + ")");

		if (testData != null) {
			LOGGER.debug("storing test data for " + stepdef + " (" + id + ")");
			testData.saveFor(verification);
		}
	}

	/**
	 * Generic step to load the {@link DelayedVerification} that triggered this delayed verification scenario and load
	 * the {@link PersistableTestData} using that delayed verification.
	 *
	 * Note: This method should not be used directly.
	 *
	 * @param dvId
	 *            the unique id of the delayed verification triggering this delayed verification scenario
	 *
	 */
	@Given("^Test Execution Context is loaded for dvId=(.+)$")
	public void testExecutionContextIsLoadedForDvId(String dvId) {
		LOGGER.debug("loading delayed verification " + dvId);
		verification = verificationStore.read(dvId);
		verification.setProcessedAt(DateTime.now());
		verificationStore.update(verification);
		context.set(verification);

		if (testData != null) {
			LOGGER.debug("loading test data for " + dvId);
			testData.loadFor(verification);
		}
	}
}
