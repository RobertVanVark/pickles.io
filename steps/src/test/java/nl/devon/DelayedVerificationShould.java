package nl.devon;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.joda.time.DateTime;
import org.junit.Test;

public class DelayedVerificationShould {

	@Test
	public void haveCreationTime() {
		DelayedVerification verification = delayedVerification(DateTime.now());
		assertThat(verification.getCreatedAt(), notNullValue());
	}

	@Test
	public void haveVerificationTime() {
		DateTime verifyAt = DateTime.now().plusHours(2);
		DelayedVerification verification = delayedVerification(verifyAt);
		assertThat(verification.getVerifyAt(), is(verifyAt));
	}

	@Test
	public void identifyCreatingScenario() {
		String scenarioChecksum = "12345";
		DelayedVerification verification = new DelayedVerification(DateTime.now(), scenarioChecksum);
		assertThat(verification.getScenarioChecksum(), is(scenarioChecksum));
	}

	@Test
	public void generateUniqueId() {
		DelayedVerification first = delayedVerification(DateTime.now());
		assertThat(first.getId(), notNullValue());

		DelayedVerification second = delayedVerification(DateTime.now());
		assertThat(second.getId(), notNullValue());

		assertThat(first.getId(), not(second.getId()));
	}

	private DelayedVerification delayedVerification(DateTime verifyAt) {
		return new DelayedVerification(verifyAt, "");
	}
}
