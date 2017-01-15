package nl.devon.pickles.steps.delays;

import org.joda.time.DateTime;

import nl.devon.pickles.steps.TestExecutionContext;

public class CombinedDelay extends Delay {

	Delay firstDelay;
	Delay secondDelay;

	public CombinedDelay(Delay first, Delay second) {
		firstDelay = first;
		secondDelay = second;
	}

	@Override
	public DateTime getVerifyAt(TestExecutionContext executionContext, DateTime startingFrom) {

		DateTime firstVerifyAt = firstDelay.getVerifyAt(executionContext, startingFrom);
		DateTime secondVerifyAt = secondDelay.getVerifyAt(executionContext, firstVerifyAt);

		return secondVerifyAt;
	}

	Delay getFirstDelay() {
		return firstDelay;
	}

	Delay getSecondDelay() {
		return secondDelay;
	}
}
