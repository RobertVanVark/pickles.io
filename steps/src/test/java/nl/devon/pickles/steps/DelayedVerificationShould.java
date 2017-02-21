package nl.devon.pickles.steps;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

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
	public void beUniquelyIdentifiable() {
		String id = "dvId";
		DelayedVerification verification = new DelayedVerification(id, DateTime.now(), "12345",
				"features/name.feature");
		assertThat(verification.getId(), is(id));
	}

	@Test
	public void identifyCreatingScenario() {
		String scenarioChecksum = "12345";
		DelayedVerification verification = new DelayedVerification("id", DateTime.now(), scenarioChecksum,
				"features/name.feature");
		assertThat(verification.getScenarioChecksum(), is(scenarioChecksum));
	}

	@Test
	public void identifyFeature() {
		String feature = "features/name.feature";
		DelayedVerification verification = new DelayedVerification("id", DateTime.now(), "checksum", feature);
		assertThat(verification.getFeatureUri(), is(feature));
	}

	private DelayedVerification delayedVerification(DateTime verifyAt) {
		return new DelayedVerification("id", verifyAt, "dummy checksum", "features/dummy.feature");
	}

	private DelayedVerification restoredDelayedVerification() {
		return new DelayedVerification("fake id", DateTime.now().minusHours(2), DateTime.now().minusHours(1),
				DateTime.now(), "dummy checksum", "features/dummy.feature");
	}
}
