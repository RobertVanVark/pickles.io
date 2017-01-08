package nl.devon.pickles.steps;

import org.joda.time.DateTime;

public interface TestExecutionContext {

	void set(DelayedVerification verification);

	DelayedVerification get();

	DateTime firstBusinessDayOnOrAfter(DateTime reference);
}
