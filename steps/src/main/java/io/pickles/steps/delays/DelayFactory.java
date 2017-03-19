package io.pickles.steps.delays;

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

	public static Delay create(String baseExpression) {

		baseExpression = baseExpression.replace("after ", "");

		if (baseExpression.contains(" + ")) {
			int index = baseExpression.indexOf(" + ");
			Delay first = create(baseExpression.substring(0, index));
			Delay second = create(baseExpression.substring(index + 3, baseExpression.length()));
			return new CombinedDelay(first, second);
		}

		if (baseExpression.matches(TimeOffsetDelay.EXPRESSION)) {
			return new TimeOffsetDelay(baseExpression);
		}
		if (baseExpression.matches(BusinessEventDelay.EXPRESSION)) {
			return new BusinessEventDelay(baseExpression);
		}

		throw new DelayException("No delay for expression : " + baseExpression);
	}
}
