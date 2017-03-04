package io.pickles.steps;

import org.joda.time.DateTime;

public interface TestExecutionContext {

	void set(DelayedVerification verification);

	DelayedVerification get();

	DateTime firstBusinessDay(DateTime onOrAfter);

	DateTime firstVerificationTimeFor(String eventName, DateTime atOrAfter);
}
