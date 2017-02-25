package io.pickles.steps.stubs;

import org.joda.time.DateTime;

import io.pickles.steps.DelayedVerification;
import io.pickles.steps.TestExecutionContext;

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

	@Override
	public DateTime verifyTimeFor(String eventName, DateTime startingFrom) {
		if ("noon".equalsIgnoreCase(eventName)) {
			return midnight().withHourOfDay(12);
		}
		return midnight();
	}

	private DateTime midnight() {
		return DateTime.now().withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
	}

}