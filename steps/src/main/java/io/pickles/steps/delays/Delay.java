package io.pickles.steps.delays;

import org.joda.time.DateTime;

import io.pickles.steps.TestExecutionContext;

public abstract class Delay {

	protected Integer hours;
	protected Integer minutes;

	public DateTime getVerificationTime(TestExecutionContext testExecutionContext) {
		DateTime startingFrom = DateTime.now();
		if (testExecutionContext.get() != null) {
			startingFrom = testExecutionContext.get().getVerifyAt();
		}

		return getVerificationTime(testExecutionContext, startingFrom);
	}

	abstract DateTime getVerificationTime(TestExecutionContext testExecutionContext, DateTime atOrAfter);
}