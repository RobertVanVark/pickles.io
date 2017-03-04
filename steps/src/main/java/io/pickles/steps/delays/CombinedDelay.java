package io.pickles.steps.delays;

import org.joda.time.DateTime;

import io.pickles.steps.TestExecutionContext;

public class CombinedDelay extends Delay {

	Delay firstDelay;
	Delay secondDelay;

	public CombinedDelay(Delay first, Delay second) {
		firstDelay = first;
		secondDelay = second;
	}

	@Override
	public DateTime getVerificationTime(TestExecutionContext testEecutionContext, DateTime onOrAfter) {

		DateTime firstVerifyAt = firstDelay.getVerificationTime(testEecutionContext, onOrAfter);
		DateTime secondVerifyAt = secondDelay.getVerificationTime(testEecutionContext, firstVerifyAt);

		return secondVerifyAt;
	}

	Delay getFirstDelay() {
		return firstDelay;
	}

	Delay getSecondDelay() {
		return secondDelay;
	}
}
