package io.pickles.steps.delays;

import io.pickles.steps.DelayException;

public class DelayFactory {

	private static final String SINGLE_DELAY_EXPRESSION = "" //
			+ "(?:" //
			+ TimeOffsetDelay.EXPRESSION //
			+ "|" //
			+ BusinessEventDelay.EXPRESSION //
			+ ")";

	public static final String DELAY_EXPRESSION = "" //
			+ "after " //
			+ SINGLE_DELAY_EXPRESSION //
			+ "(?: \\+ " //
			+ SINGLE_DELAY_EXPRESSION//
			+ ")*";

	public static Delay create(String expression) {

		expression = expression.replace("after ", "");

		if (expression.contains(" + ")) {
			int index = expression.indexOf(" + ");
			Delay first = create(expression.substring(0, index));
			Delay second = create(expression.substring(index + 3, expression.length()));
			return new CombinedDelay(first, second);
		}

		if (expression.matches(TimeOffsetDelay.EXPRESSION)) {
			return new TimeOffsetDelay(expression);
		}
		if (expression.matches(BusinessEventDelay.EXPRESSION)) {
			return new BusinessEventDelay(expression);
		}

		throw new DelayException("No delay for expression : " + expression);
	}
}
