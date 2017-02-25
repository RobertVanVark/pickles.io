package io.pickles.steps.delays;

import org.joda.time.DateTime;

import io.pickles.steps.TestExecutionContext;

public abstract class Delay {

	protected Integer hours;
	protected Integer minutes;

	public DateTime getVerifyAt(TestExecutionContext executionContext) {
		DateTime startingFrom = DateTime.now();
		if (executionContext.get() != null) {
			startingFrom = executionContext.get().getVerifyAt();
		}

		return getVerifyAt(executionContext, startingFrom);
	}

	abstract DateTime getVerifyAt(TestExecutionContext executionContext, DateTime startingFrom);
}