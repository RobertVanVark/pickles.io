package io.pickles.steps;

import org.joda.time.DateTime;

public interface TestExecutionContext {

	void set(DelayedVerification verification);

	DelayedVerification get();

	DateTime firstBusinessDayOnOrAfter(DateTime reference);

	DateTime verifyTimeFor(String eventName, DateTime startingFrom);
}
