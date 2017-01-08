package nl.devon.pickles.steps.stubs;

import org.joda.time.DateTime;

import nl.devon.pickles.steps.DelayedVerification;
import nl.devon.pickles.steps.TestExecutionContext;

public class StubNextDayExecutionContext implements TestExecutionContext {

	private DelayedVerification verification;

	@Override
	public void set(DelayedVerification verification) {
		this.verification = verification;
	}

	@Override
	public DelayedVerification get() {
		return verification;
	}

	@Override
	public DateTime firstBusinessDayOnOrAfter(DateTime reference) {
		return reference.plusDays(1);
	}
}