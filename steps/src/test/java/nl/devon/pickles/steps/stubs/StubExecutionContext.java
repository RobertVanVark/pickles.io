package nl.devon.pickles.steps.stubs;

import nl.devon.pickles.steps.DelayedVerification;
import nl.devon.pickles.steps.TestExecutionContext;

public class StubExecutionContext implements TestExecutionContext {

	private DelayedVerification verification;

	@Override
	public void set(DelayedVerification verification) {
		this.verification = verification;
	}

	@Override
	public DelayedVerification get() {
		return verification;
	}
}