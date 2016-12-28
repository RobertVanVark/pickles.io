package nl.devon.stubs;

import nl.devon.DelayedVerification;
import nl.devon.TestExecutionContext;

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