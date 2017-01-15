package nl.devon.pickles.steps.delays;

import nl.devon.pickles.steps.PicklesDelayException;

public class DelayFactory {

	public static final String DELAY_EXPRESSION = "[" + //
			TimeOffsetDelay.EXPRESSION + "|" + //
			BusinessEventDelay.EXPRESSION + //
			"]";

	public static final String COMBINED_EXPRESSION = DELAY_EXPRESSION + "[ + " + DELAY_EXPRESSION + "]*";

	public static Delay create(String expression) {

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

		throw new PicklesDelayException("No delay for expression : " + expression);
	}
}
