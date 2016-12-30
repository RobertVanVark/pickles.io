package nl.devon.pickles.steps;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertTrue;

import org.joda.time.DateTime;
import org.junit.Test;

import nl.devon.pickles.steps.DelayedVerification;

public class DelayedVerificationShould {

	@Test
	public void initiateCreationTimeToNow() {
		DateTime before = DateTime.now().minusMillis(1);
		DelayedVerification verification = delayedVerification(DateTime.now());
		assertTrue(verification.getCreatedAt().isAfter(before));
		assertTrue(verification.getCreatedAt().isBefore(DateTime.now().plusMillis(1)));
	}

	@Test
	public void haveVerificationTime() {
		DateTime verifyAt = DateTime.now().plusHours(2);
		DelayedVerification verification = delayedVerification(verifyAt);
		assertThat(verification.getVerifyAt(), is(verifyAt));
	}

	@Test
	public void haveProcessingTime() {
		DelayedVerification verification = restoredDelayedVerification();
		assertThat(verification.getProcessedAt(), notNullValue());
	}

	@Test
	public void identifyCreatingScenario() {
		String scenarioChecksum = "12345";
		DelayedVerification verification = new DelayedVerification(DateTime.now(), scenarioChecksum, "feature name");
		assertThat(verification.getScenarioChecksum(), is(scenarioChecksum));
	}

	@Test
	public void identifyFeature() {
		String feature = "feature name";
		DelayedVerification verification = new DelayedVerification(DateTime.now(), "checksum", feature);
		assertThat(verification.getFeature(), is(feature));
	}

	@Test
	public void generateUniqueId() {
		DelayedVerification first = delayedVerification(DateTime.now());
		System.out.println(first.getId());
		assertThat(first.getId(), notNullValue());

		DelayedVerification second = delayedVerification(DateTime.now());
		assertThat(second.getId(), notNullValue());
		System.out.println(second.getId());

		assertThat(first.getId(), not(second.getId()));
	}

	private DelayedVerification delayedVerification(DateTime verifyAt) {
		return new DelayedVerification(verifyAt, "dummy checksum", "dummy feature");
	}

	private DelayedVerification restoredDelayedVerification() {
		return new DelayedVerification("fake id", DateTime.now().minusHours(2), DateTime.now().minusHours(1),
				DateTime.now(), "dummy checksum", "dummy feature");
	}
}
